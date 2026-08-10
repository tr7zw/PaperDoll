package dev.tr7zw.paperdoll;

import java.util.*;
import java.util.stream.*;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.transition.mc.EntityUtil;
import dev.tr7zw.transition.mc.LightingUtil;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.extending.*;
import dev.tr7zw.trender.gui.client.*;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.*;
//? if < 26.2
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PaperDollRenderer {

    private final PaperDollShared instance = PaperDollShared.instance;
    private long showTill = 0;

    public void render(float delta, RenderContext context) {
        Minecraft mc_instance = Minecraft.getInstance();
        if (!instance.settings.dollEnabled)
            return;
        //? if >= 1.20.2 {

        if (mc_instance.getDebugOverlay().showDebugScreen())
            return;
        //? } else {

        // if (mc_instance.options.renderDebug)
        //     return;
        //? }
        if (mc_instance.level == null)
            return;
        //? if >= 26.2 {

        /*if (mc_instance.gui.hud.isHidden())
            return;
        *///? } else {
        
        if (mc_instance.options.hideGui)
            return;
        //? }

        int xpos = 0;
        int ypos = 0;
        switch (instance.settings.location) {
        case TOP_LEFT:
            xpos = 25 + instance.settings.dollXOffset;
            ypos = 55 + instance.settings.dollYOffset;
            break;
        case TOP_RIGHT:
            xpos = mc_instance.getWindow().getGuiScaledWidth() - (25 + instance.settings.dollXOffset);
            ypos = 55 + instance.settings.dollYOffset;
            break;
        case BOTTOM_LEFT:
            xpos = 25 + instance.settings.dollXOffset;
            ypos = mc_instance.getWindow().getGuiScaledHeight() - (55 + instance.settings.dollYOffset);
            break;
        case BOTTOM_RIGHT:
            xpos = mc_instance.getWindow().getGuiScaledWidth() - (25 + instance.settings.dollXOffset);
            ypos = mc_instance.getWindow().getGuiScaledHeight() - (55 + instance.settings.dollYOffset);
            break;
        }
        // FIXME: Workaround for 26.1 new renderlogic having different positioning
        //? if >= 26.1 {
        /*xpos -= 50;
        ypos -= 50;
        *///? }
        int size = 25 + instance.settings.dollSize;
        int fSize = size;
        int fXpos = xpos;
        int fYpos = ypos;
        int lookSides = -instance.settings.dollLookingSides;
        int lookUpDown = instance.settings.dollLookingUpDown;
        Entity playerEntity = mc_instance.getCameraEntity() != null ? mc_instance.getCameraEntity()
                : mc_instance.player;

        if (instance.settings.autoHide && playerEntity instanceof LivingEntity livingEntity) {
            boolean hide = shouldAutoHide(livingEntity);
            if (hide && System.currentTimeMillis() > showTill) {
                return;
            }
            if (!hide)
                showTill = System.currentTimeMillis() + 500;
        }

        if (instance.settings.hideInF5 && Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        //? if >= 1.21.2 && < 26.1 {
        /*
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        *///? }

        boolean lockYHeadRot = instance.settings.dollHeadMode == DollHeadMode.LOCKED;
        boolean lockXHeadRot = lockYHeadRot || instance.settings.dollHeadMode == DollHeadMode.FREE_HORIZONTAL
                || instance.settings.dollHeadMode == DollHeadMode.STATIC_HORIZONTAL;
        var vehicleRenderStates = new ArrayList<>();
        if (!instance.settings.hideVehicle && playerEntity.isPassenger()) {
            Entity vehicle = playerEntity.getRootVehicle();
            var stream = getPassengersAndSelf(vehicle);
            //? if >= 26.1 {

            /*boolean getRenderState = true;
            *///? } else {
            
            boolean getRenderState = false;
            //? }
            stream.forEachOrdered(entity -> {
                if (entity == playerEntity) {
                    return;
                }
                double yOffset = 0;
                if (entity != playerEntity)
                    yOffset = (playerEntity.getY() - entity.getY());

                vehicleRenderStates.add(drawEntity(context, fXpos, fYpos + (yOffset * fSize), fSize, lookSides,
                        lookUpDown, entity, delta, lockXHeadRot, lockYHeadRot, getRenderState, null, -yOffset));
            });
        }
        drawEntity(context, fXpos, fYpos, size, lookSides, lookUpDown, playerEntity, delta, lockYHeadRot, lockYHeadRot,
                false, vehicleRenderStates.isEmpty() ? null : vehicleRenderStates, 0);

    }

    private boolean shouldAutoHide(LivingEntity livingEntity) {
        Set<PaperDollSettings.AutoHideException> blacklist = instance.settings.autoHideBlacklist;

        // Movement
        if (livingEntity.isCrouching() && !blacklist.contains(PaperDollSettings.AutoHideException.CROUCHING))
            return false;
        if (livingEntity.isSprinting() && !blacklist.contains(PaperDollSettings.AutoHideException.RUNNING))
            return false;
        if (livingEntity.isFallFlying() && !blacklist.contains(PaperDollSettings.AutoHideException.FALL_FLYING))
            return false;
        if (livingEntity.isVisuallySwimming() && !blacklist.contains(PaperDollSettings.AutoHideException.SWIMMING))
            return false;
        if (livingEntity.isPassenger() && !blacklist.contains(PaperDollSettings.AutoHideException.IN_VEHICLE))
            return false;

        // Combat
        if (livingEntity.isBlocking() && !blacklist.contains(PaperDollSettings.AutoHideException.BLOCKING))
            return false;
        if (livingEntity.isUsingItem() && !blacklist.contains(PaperDollSettings.AutoHideException.USING_ITEM))
            return false;
        if (livingEntity.swinging && !blacklist.contains(PaperDollSettings.AutoHideException.SWINGING))
            return false;
        if (livingEntity.hurtTime > 0 && !blacklist.contains(PaperDollSettings.AutoHideException.TAKING_DAMAGE))
            return false;
        if (livingEntity.isOnFire() && !blacklist.contains(PaperDollSettings.AutoHideException.ON_FIRE))
            return false;
        //? if >= 1.17.0 {

        if (livingEntity.isInPowderSnow && !blacklist.contains(PaperDollSettings.AutoHideException.IN_POWDER_SNOW))
            return false;
        //? }

        return true;
    }

    public Stream<Entity> getPassengersAndSelf(Entity vehicle) {
        return Stream.concat(vehicle.getPassengers().stream(), Stream.of(vehicle));
    }

    // Modified version from InventoryScreen
    private Object drawEntity(RenderContext context, double xpos, double ypos, int size, float lookSides,
            float lookUpDown, Entity entity, float delta, boolean lockHeadXRot, boolean lockHeadYRot,
            boolean getRenderState, List<Object> vehicleRenderStates, double stateOffsetY) {
        Minecraft mc_instance = Minecraft.getInstance();
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        if (entity instanceof LivingEntity livingEntity
                && (livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack())) {
            float f2 = (float) livingEntity.getFallFlyingTicks() + delta;
            float f3 = Mth.clamp(f2 * f2 / 100.0F, 0.0F, 1.0F);
            ypos -= (90f + f3) / 90f * (size) - 5;
        }
        prepareViewMatrix(xpos, ypos);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        //? if >= 1.21.6 {

        /*int rot = 180;
        *///? } else if >= 1.20.5 {

         int rot = 0;
        //? } else {

        // int rot = 180;
        //? }
        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;
        if (entity.isPassenger()) {
            Entity vehicle = entity.getVehicle();
            double offsetXTmp = entity.getX() - vehicle.getX();
            double offsetZTmp = entity.getZ() - vehicle.getZ();
            float rotation = EntityUtil.getYRot(vehicle) - rot - rotationSide * 20.0F; // target is 180
            rotation *= MathUtil.DEG_TO_RAD;
            rotation *= -1;
            offsetX += Math.cos(rotation) * offsetXTmp - Math.sin(rotation) * offsetZTmp;
            offsetZ += Math.sin(rotation) * offsetXTmp + Math.cos(rotation) * offsetZTmp;
            // y offset is handeled above since the vehicle is moved down, 26.1 needs it here
        }
        var quaternion = MathUtil.ZP.rotationDegrees(180.0F);
        var quaternion2 = MathUtil.XP.rotationDegrees(rotationUp * 20.0F);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yRot = EntityUtil.getYRot(entity);
        float yRotO = entity.yRotO;
        float xRot = EntityUtil.getXRot(entity);
        float xRotO = entity.xRotO;
        float yHeadRotO = 0;
        float yHeadRot = 0;
        float yBodyRotO = 0;
        float yBodyRot = 0;
        if (entity instanceof LivingEntity livingEntity) {
            yHeadRotO = livingEntity.yHeadRotO;
            yHeadRot = livingEntity.yHeadRot;
            yBodyRotO = livingEntity.yBodyRotO;
            yBodyRot = livingEntity.yBodyRot;
        }
        Vec3 deltaMovement = entity.getDeltaMovement();
        float vehicleYBodyRot = 0;
        float vehicleYBodyRotO = 0;
        EntityUtil.setYRot(entity, rot + rotationSide * 40.0F);
        entity.yRotO = EntityUtil.getYRot(entity);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = rot + rotationSide * 20.0F;
            livingEntity.yBodyRotO = livingEntity.yBodyRot;
        }
        Vec3 lastDeltaMovement = null;
        if (entity instanceof PlayerAccess player) {
            lastDeltaMovement = player.getLastDelataMovement();
            player.setLastDeletaMovement(Vec3.ZERO);
        }
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingVehicle) {
            vehicleYBodyRot = livingVehicle.yBodyRot;
            vehicleYBodyRotO = livingVehicle.yBodyRotO;
            if (entity instanceof LivingEntity livingEntity) {
                livingVehicle.yBodyRot = livingEntity.yBodyRot;
                livingVehicle.yBodyRotO = livingEntity.yBodyRotO;
            }
        }
        if (entity instanceof LivingEntity livingEntity
                && (livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack())) {
            entity.setDeltaMovement(Vec3.ZERO);
            lockHeadXRot = (livingEntity.isFallFlying() && instance.settings.lockElytra)
                    || (livingEntity.isAutoSpinAttack() && instance.settings.lockSpinning);
        }
        if (lockHeadXRot) {
            EntityUtil.setXRot(entity, -rotationUp * 20.0F);
            entity.xRotO = EntityUtil.getXRot(entity);
        }
        if (entity instanceof LivingEntity livingEntity && lockHeadYRot) {
            livingEntity.yHeadRot = EntityUtil.getYRot(entity);
            livingEntity.yHeadRotO = EntityUtil.getYRot(entity);
        } else if (entity instanceof LivingEntity livingEntity) {
            if (instance.settings.dollHeadMode == DollHeadMode.FREE
                    || instance.settings.dollHeadMode == DollHeadMode.FREE_HORIZONTAL) {
                livingEntity.yHeadRot = rot + rotationSide * 40.0F - (yBodyRot - yHeadRot);
                livingEntity.yHeadRotO = rot + rotationSide * 40.0F - (yBodyRotO - yHeadRotO);
            } else {
                livingEntity.yHeadRot = rot + rotationSide * 40.0F - (yRot - yHeadRot);
                livingEntity.yHeadRotO = rot + rotationSide * 40.0F - (yRotO - yHeadRotO);
            }
        }
        prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        MathUtil.conjugate(quaternion2);
        //? if < 1.21.10 {
        
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        //? }
           //? if < 26.2 {
           
           MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
           //? }
           // Mc renders the player in the inventory without delta, causing it to look
           // "laggy". Good luck unseeing this :)
           //? if >= 26.1 {

        /*var vector3f = new org.joml.Vector3f((float) offsetX, 0, (float) offsetZ);
        var state = entityRenderDispatcher.getRenderer(entity).createRenderState(entity, delta);
        state.shadowPieces.clear();
        if (vehicleRenderStates != null && state instanceof ExtensionHolder extensionHolder) {
            extensionHolder.setExtension("PaperDollVehicles", vehicleRenderStates);
        }
        if (!getRenderState) {
            context.getGuiGraphics().entity(state, (float) size, vector3f, quaternion, quaternion2, (int) (xpos),
                    (int) (ypos), (int) (xpos + (size * 4)), (int) (ypos + (size * 4))); // TODO: Magic numbers
        } else {
            state.y = stateOffsetY;
        }
        *///? } else if >= 1.21.6 {

        /*float o = 1;
        var vector3f = new org.joml.Vector3f((float) offsetX, 0, (float) offsetZ);
        float p = (float) size / o;
        ((dev.tr7zw.paperdoll.future.GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderState()
                .submitPicturesInPictureState(new dev.tr7zw.paperdoll.future.CustomGuiEntityRenderState(
                        entityRenderDispatcher.getRenderer(entity).createRenderState(entity, delta), matrixStack,
                        vector3f, quaternion, quaternion2, (int) (xpos), (int) (ypos), (int) (xpos + size),
                        (int) (ypos + size), p, xpos, ypos));
        *///? } else if >= 1.21.2 {

        // entityRenderDispatcher.render(entity, offsetX, offsetY, offsetZ, delta, matrixStack, bufferSource,
        //        15728880);
        //? } else {

         entityRenderDispatcher.render(entity, offsetX, offsetY, offsetZ, 0.0F, delta, matrixStack, bufferSource,
                15728880);
        //? }
        //? if < 26.2 {
        
        bufferSource.endBatch();
        //? }
           //? if < 1.21.10 {
           
           entityRenderDispatcher.setRenderShadow(true);
           //? }
        if (entity instanceof PlayerAccess player) {
            player.setLastDeletaMovement(lastDeltaMovement);
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = yBodyRot;
            livingEntity.yBodyRotO = yBodyRotO;
            livingEntity.yHeadRotO = yHeadRotO;
            livingEntity.yHeadRot = yHeadRot;
        }
        EntityUtil.setYRot(entity, yRot);
        entity.yRotO = yRotO;
        EntityUtil.setXRot(entity, xRot);
        entity.xRotO = xRotO;
        entity.setDeltaMovement(deltaMovement);
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingVehicle) {
            livingVehicle.yBodyRot = vehicleYBodyRot;
            livingVehicle.yBodyRotO = vehicleYBodyRotO;
        }
        resetViewMatrix();
        // #else
        // $$ com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
        // #endif
        //? if >= 26.1 {

        /*return state;
        *///? } else {
        
        return null;
        //? }
    }

    private void prepareViewMatrix(double xpos, double ypos) {
        //? if >= 1.20.5 {

        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate((float) xpos, (float) ypos, 1050.0F);
        RenderSystem.getModelViewStack().scale(-1.0F, 1.0F, 1.0F);
        //? if < 1.21.2 {

         RenderSystem.applyModelViewMatrix();
        //? }
        //? } else if >= 1.17.0 {

        // PoseStack poseStack = RenderSystem.getModelViewStack();
        // poseStack.pushPose();
        // poseStack.translate(xpos, ypos, 1050.0D);
        // poseStack.scale(1.0F, 1.0F, -1.0F);
        // RenderSystem.applyModelViewMatrix();
        //? } else {

        // RenderSystem.pushMatrix();
        // RenderSystem.translatef((float)xpos, (float)ypos, 1050.0F);
        // RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        //? }
    }

    private void resetViewMatrix() {
        //? if >= 1.20.5 {

        RenderSystem.getModelViewStack().popMatrix();
        //? if < 1.21.2 {

         RenderSystem.applyModelViewMatrix();
        //? }
        //? } else if >= 1.17.0 {

        // RenderSystem.getModelViewStack().popPose();
        // RenderSystem.applyModelViewMatrix();
        //? } else {

        // RenderSystem.popMatrix();
        //? }
    }

    private void prepareLighting() {
        //? if >= 1.21.6 {

        /*LightingUtil.prepareLightingEntity();
        *///? } else if >= 1.17.0 {

         com.mojang.blaze3d.platform.Lighting.setupForEntityInInventory();
        //? } else {

        // com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
        //? }
    }

}
