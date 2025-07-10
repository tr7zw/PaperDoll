package dev.tr7zw.paperdoll;

import java.util.Set;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.transition.mc.EntityUtil;
import dev.tr7zw.transition.mc.LightingUtil;
import dev.tr7zw.transition.mc.MathUtil;
import lombok.Setter;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;

public class PaperDollRenderer {

    private final PaperDollShared instance = PaperDollShared.instance;
    private long showTill = 0;

    //#if MC >= 12106
    @Setter
    private net.minecraft.client.gui.GuiGraphics guiGraphics;
    //#endif

    public void render(float delta) {
        Minecraft mc_instance = Minecraft.getInstance();
        if (!instance.settings.dollEnabled)
            return;
        //#if MC >= 12002
        if (mc_instance.getDebugOverlay().showDebugScreen())
            return;
        //#else
        //$$ if (mc_instance.options.renderDebug)
        //$$     return;
        //#endif
        if (mc_instance.level == null)
            return;
        if (mc_instance.options.hideGui)
            return;

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
        int fXpos = xpos;
        int fYpos = ypos;
        int size = 25 + instance.settings.dollSize;
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

        //#if MC >= 12102
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        //#endif

        boolean lockYHeadRot = instance.settings.dollHeadMode == DollHeadMode.LOCKED;
        boolean lockXHeadRot = lockYHeadRot || instance.settings.dollHeadMode == DollHeadMode.FREE_HORIZONTAL
                || instance.settings.dollHeadMode == DollHeadMode.STATIC_HORIZONTAL;
        if (!instance.settings.hideVehicle && playerEntity.isPassenger()) {
            Entity vehicle = playerEntity.getRootVehicle();
            getPassengersAndSelf(vehicle).forEachOrdered(entity -> {
                double yOffset = fYpos;
                if (entity != playerEntity)
                    yOffset += (playerEntity.getY() - entity.getY()) * size;
                if (entity instanceof LivingEntity living) {
                    drawLivingEntity(fXpos, yOffset, size, lookSides, lookUpDown, living, delta, lockXHeadRot,
                            lockYHeadRot);
                } else {
                    // yOffset -= 10;
                    drawEntity(fXpos, yOffset, size, lookSides, lookUpDown, entity, delta, lockYHeadRot);
                }
            });
        } else {
            if (playerEntity instanceof LivingEntity living) {
                drawLivingEntity(fXpos, fYpos, size, lookSides, lookUpDown, living, delta, lockXHeadRot, lockYHeadRot);
            } else {
                drawEntity(fXpos, fYpos, size, lookSides, lookUpDown, playerEntity, delta, lockYHeadRot);
            }
        }

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
        //#if MC >= 11700
        if (livingEntity.isInPowderSnow && !blacklist.contains(PaperDollSettings.AutoHideException.IN_POWDER_SNOW))
            return false;
        //#endif

        return true;
    }

    public Stream<Entity> getPassengersAndSelf(Entity vehicle) {
        return Stream.concat(vehicle.getPassengers().stream(), Stream.of(vehicle));
    }

    // Modified version from InventoryScreen
    private void drawLivingEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown,
            LivingEntity livingEntity, float delta, boolean lockHeadXRot, boolean lockHeadYRot) {
        Minecraft mc_instance = Minecraft.getInstance();
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        if (livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack()) {
            float f2 = (float) livingEntity.getFallFlyingTicks() + delta;
            float f3 = Mth.clamp(f2 * f2 / 100.0F, 0.0F, 1.0F);
            ypos -= (90f + f3) / 90f * (size) - 5;
        }
        prepareViewMatrix(xpos, ypos);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        //#if MC >= 12106
        int rot = 180;
        //#elseif MC >= 12005
        //$$ int rot = 0;
        //#else
        //$$ int rot = 180;
        //#endif
        var quaternion = MathUtil.ZP.rotationDegrees(180.0F);
        var quaternion2 = MathUtil.XP.rotationDegrees(rotationUp * 20.0F);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = EntityUtil.getYRot(livingEntity);
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = EntityUtil.getXRot(livingEntity);
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 deltaMovement = livingEntity.getDeltaMovement();
        float vehicleYBodyRot = 0;
        float vehicleYBodyRotO = 0;
        livingEntity.yBodyRot = rot + rotationSide * 20.0F;
        EntityUtil.setYRot(livingEntity, rot + rotationSide * 40.0F);
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = EntityUtil.getYRot(livingEntity);
        Vec3 lastDeltaMovement = null;
        if (livingEntity instanceof PlayerAccess player) {
            lastDeltaMovement = player.getLastDelataMovement();
            player.setLastDeletaMovement(Vec3.ZERO);
        }
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingVehicle) {
            vehicleYBodyRot = livingVehicle.yBodyRot;
            vehicleYBodyRotO = livingVehicle.yBodyRotO;
            livingVehicle.yBodyRot = livingEntity.yBodyRot;
            livingVehicle.yBodyRotO = livingEntity.yBodyRotO;
        }
        if (livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack()) {
            livingEntity.setDeltaMovement(Vec3.ZERO);
            lockHeadXRot = (livingEntity.isFallFlying() && instance.settings.lockElytra)
                    || (livingEntity.isAutoSpinAttack() && instance.settings.lockSpinning);
        }
        if (lockHeadXRot) {
            EntityUtil.setXRot(livingEntity, -rotationUp * 20.0F);
            livingEntity.xRotO = EntityUtil.getXRot(livingEntity);
        }
        if (lockHeadYRot) {
            livingEntity.yHeadRot = EntityUtil.getYRot(livingEntity);
            livingEntity.yHeadRotO = EntityUtil.getYRot(livingEntity);
        } else {
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
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        double offsetX = 0;
        double offsetY = 0;
        double offsetZ = 0;
        if (livingEntity.isPassenger()) {
            Entity vehicle = livingEntity.getVehicle();
            double offsetXTmp = livingEntity.getX() - vehicle.getX();
            double offsetZTmp = livingEntity.getZ() - vehicle.getZ();
            float rotation = EntityUtil.getYRot(vehicle) - rot - rotationSide * 20.0F; // target is 180
            rotation *= MathUtil.DEG_TO_RAD;
            rotation *= -1;
            offsetX += Math.cos(rotation) * offsetXTmp - Math.sin(rotation) * offsetZTmp;
            offsetZ += Math.sin(rotation) * offsetXTmp + Math.cos(rotation) * offsetZTmp;
            // y offset is handeled above since the vehicle is moved down
        }
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        //#if MC >= 12106
        float o = livingEntity.getScale();
        var vector3f = new org.joml.Vector3f(0.0F, livingEntity.getBbHeight() / 2.0F + 0 * o, 0.0F);
        float p = (float) size / o;
        guiGraphics.submitEntityRenderState(
                entityRenderDispatcher.getRenderer(livingEntity).createRenderState(livingEntity, delta), p, vector3f,
                quaternion, quaternion2, (int) (xpos - size * o), (int) (ypos - size * o), (int) (xpos + size * o),
                (int) (ypos + size * o));
        //#elseif MC >= 12102
        //$$entityRenderDispatcher.render(livingEntity, offsetX, offsetY, offsetZ, delta, matrixStack, bufferSource,
        //$$        15728880);
        //#else
        //$$entityRenderDispatcher.render(livingEntity, offsetX, offsetY, offsetZ, 0.0F, delta, matrixStack, bufferSource,
        //$$        15728880);
        //#endif
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        if (livingEntity instanceof PlayerAccess player) {
            player.setLastDeletaMovement(lastDeltaMovement);
        }
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        EntityUtil.setYRot(livingEntity, yRot);
        livingEntity.yRotO = yRotO;
        EntityUtil.setXRot(livingEntity, xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(deltaMovement);
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingVehicle) {
            livingVehicle.yBodyRot = vehicleYBodyRot;
            livingVehicle.yBodyRotO = vehicleYBodyRotO;
        }
        resetViewMatrix();
        // #else
        // $$ Lighting.setupFor3DItems();
        // #endif

    }

    private void prepareViewMatrix(double xpos, double ypos) {
        //#if MC >= 12005
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().translate((float) xpos, (float) ypos, 1050.0F);
        RenderSystem.getModelViewStack().scale(-1.0F, 1.0F, 1.0F);
        //#if MC < 12102
        //$$RenderSystem.applyModelViewMatrix();
        //#endif
        //#elseif MC >= 11700
        //$$ PoseStack poseStack = RenderSystem.getModelViewStack();
        //$$ poseStack.pushPose();
        //$$ poseStack.translate(xpos, ypos, 1050.0D);
        //$$ poseStack.scale(1.0F, 1.0F, -1.0F);
        //$$ RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.pushMatrix();
        //$$ RenderSystem.translatef((float)xpos, (float)ypos, 1050.0F);
        //$$ RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        //#endif
    }

    private void resetViewMatrix() {
        //#if MC >= 12005
        RenderSystem.getModelViewStack().popMatrix();
        //#if MC < 12102
        //$$RenderSystem.applyModelViewMatrix();
        //#endif
        //#elseif MC >= 11700
        //$$ RenderSystem.getModelViewStack().popPose();
        //$$ RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.popMatrix();
        //#endif
    }

    private void prepareLighting() {
        //#if MC >= 12106
        LightingUtil.prepareLightingEntity();
        //#elseif MC >= 11700
        //$$ Lighting.setupForEntityInInventory();
        //#else
        //$$ Lighting.setupForFlatItems();
        //#endif
    }

    private void drawEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown, Entity entity,
            float delta, boolean lockHead) {
        Minecraft mc_instance = Minecraft.getInstance();
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            ypos -= (90f + entity.xRotO) / 90f * (size) - 5;
        }
        prepareViewMatrix(xpos, ypos);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        //#if MC >= 12005
        int rot = 0;
        //#else
        //$$ int rot = 180;
        //#endif
        var quaternion = MathUtil.ZP.rotationDegrees(180.0F);
        var quaternion2 = MathUtil.XP.rotationDegrees(rotationUp * 20.0F);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yRot = EntityUtil.getYRot(entity);
        float yRotO = entity.yRotO;
        float xRot = EntityUtil.getXRot(entity);
        float xRotO = entity.xRotO;
        Vec3 vel = entity.getDeltaMovement();
        Vec3 pos = entity.position();
        double yOld = entity.yOld;
        EntityUtil.setYRot(entity, 0);
        entity.yRotO = EntityUtil.getYRot(entity);
        entity.setDeltaMovement(Vec3.ZERO);
        //#if MC >= 11700
        entity.setPos(pos.add(0, 500, 0)); // hack to disconnect minecarts from rails for the rendering
        //#endif
        entity.yOld += 500;
        if (lockHead) {
            EntityUtil.setXRot(entity, -rotationUp * 20.0F);
            entity.xRotO = EntityUtil.getXRot(entity);
        }
        prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        MathUtil.conjugate(quaternion2);
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        float extraRotation = 0;
        if (entity instanceof Minecart) {
            extraRotation += 90;
        }
        //#if MC >= 12102
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, rot + rotationSide * 20.0F + extraRotation, matrixStack,
                bufferSource, 15728880);
        //#else
        //$$entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, rot + rotationSide * 20.0F + extraRotation, delta,
        //$$        matrixStack, bufferSource, 15728880);
        //#endif
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        EntityUtil.setYRot(entity, yRot);
        entity.yRotO = yRotO;
        EntityUtil.setXRot(entity, xRot);
        entity.xRotO = xRotO;
        entity.setDeltaMovement(vel);
        //#if MC >= 11700
        entity.setPos(pos);
        //#endif
        entity.yOld = yOld;
        resetViewMatrix();
    }

}
