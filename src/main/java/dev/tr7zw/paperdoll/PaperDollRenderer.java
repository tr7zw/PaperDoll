package dev.tr7zw.paperdoll;

import java.util.stream.Stream;

//spotless:off
//#if MC >= 11903
import org.joml.Quaternionf;
//#else
//$$ import com.mojang.math.Quaternion;
//#endif

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.util.NMSHelper;
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

    private final Minecraft mc_instance = Minecraft.getInstance();
    private final PaperDollShared instance = PaperDollShared.instance;
    private long showTill = 0;

    public void render(float delta) {
        // spotless:off
    	//#if MC >= 12002
        if (!instance.settings.dollEnabled || mc_instance.getDebugOverlay().showDebugScreen()
        //#else
        //$$ if (!instance.settings.dollEnabled || mc_instance.options.renderDebug
        //#endif
        // spotless:on
                || mc_instance.level == null) {
            return;
        }

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
            boolean hide = true;
            // Movement
            if (livingEntity.isCrouching() || livingEntity.isSprinting() || livingEntity.isFallFlying()
                    || livingEntity.isPassenger() || livingEntity.isVisuallySwimming()) {
                hide = false;
            }
            // combat
            if (livingEntity.isBlocking() || livingEntity.isUsingItem() || livingEntity.swinging
                    || livingEntity.isOnFire() || livingEntity.hurtTime > 0) {
                hide = false;
            }
            // spotless:off
        	//#if MC >= 11700
            if(livingEntity.isInPowderSnow)hide = false;
            //#endif
            // spotless:on

            if (hide && System.currentTimeMillis() > showTill) {
                return;
            }
            if (!hide)
                showTill = System.currentTimeMillis() + 500;
        }

        if (instance.settings.hideInF5 && Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }

        if (playerEntity.isPassenger()) {
            Entity vehicle = playerEntity.getRootVehicle();
            getPassengersAndSelf(vehicle).forEachOrdered(entity -> {
                double yOffset = fYpos;
                if (entity != playerEntity)
                    yOffset += (playerEntity.getY() - entity.getY()) * size;
                if (entity instanceof LivingEntity living) {
                    drawLivingEntity(fXpos, yOffset, size, lookSides, lookUpDown, living, delta,
                            instance.settings.dollHeadMode == DollHeadMode.LOCKED);
                } else {
                    // yOffset -= 10;
                    drawEntity(fXpos, yOffset, size, lookSides, lookUpDown, entity, delta,
                            instance.settings.dollHeadMode == DollHeadMode.LOCKED);
                }
            });
        } else {
            if (playerEntity instanceof LivingEntity living) {
                drawLivingEntity(fXpos, fYpos, size, lookSides, lookUpDown, living, delta,
                        instance.settings.dollHeadMode == DollHeadMode.LOCKED);
            } else {
                drawEntity(fXpos, fYpos, size, lookSides, lookUpDown, playerEntity, delta,
                        instance.settings.dollHeadMode == DollHeadMode.LOCKED);
            }
        }

    }

    public Stream<Entity> getPassengersAndSelf(Entity vehicle) {
        return Stream.concat(vehicle.getPassengers().stream(), Stream.of(vehicle));
    }

    // Modified version from InventoryScreen
    private void drawLivingEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown,
            LivingEntity livingEntity, float delta, boolean lockHead) {
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        PoseStack poseStack = getPoseStack();
        poseStack.pushPose();
        if (livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack()) {
            float f2 = (float) livingEntity.getFallFlyingTicks() + delta;
            float f3 = Mth.clamp(f2 * f2 / 100.0F, 0.0F, 1.0F);
            ypos -= (90f + f3) / 90f * (size) - 5;
        }
        poseStack.translate(xpos, ypos, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        prepareViewMatrix(xpos, ypos);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        // spotless:off
    	//#if MC >= 11903
        Quaternionf quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion2 = NMSHelper.XP.rotationDegrees(rotationUp * 20.0F);
        //#else
        //$$Quaternion quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        //$$Quaternion quaternion2 = NMSHelper.XP.rotationDegrees(rotationUp * 20.0F);
        //#endif
        // spotless:on
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = NMSHelper.getYRot(livingEntity);
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = NMSHelper.getXRot(livingEntity);
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 deltaMovement = livingEntity.getDeltaMovement();
        float vehicleYBodyRot = 0;
        float vehicleYBodyRotO = 0;
        livingEntity.yBodyRot = 180.0F + rotationSide * 20.0F;
        NMSHelper.setYRot(livingEntity, 180.0F + rotationSide * 40.0F);
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = NMSHelper.getYRot(livingEntity);
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
        }
        if (lockHead || livingEntity.isFallFlying() || livingEntity.isAutoSpinAttack()) {
            NMSHelper.setXRot(livingEntity, -rotationUp * 20.0F);
            livingEntity.xRotO = NMSHelper.getXRot(livingEntity);
            livingEntity.yHeadRot = NMSHelper.getYRot(livingEntity);
            livingEntity.yHeadRotO = NMSHelper.getYRot(livingEntity);
        } else {
            if (instance.settings.dollHeadMode == DollHeadMode.FREE) {
                livingEntity.yHeadRot = 180.0F + rotationSide * 40.0F - (yBodyRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + rotationSide * 40.0F - (yBodyRotO - yHeadRotO);
            } else {
                livingEntity.yHeadRot = 180.0F + rotationSide * 40.0F - (yRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + rotationSide * 40.0F - (yRotO - yHeadRotO);
            }
        }
        prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        conjugate(quaternion2);
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
            float rotation = NMSHelper.getYRot(vehicle) - 180 - rotationSide * 20.0F; // target is 180
            rotation *= NMSHelper.DEG_TO_RAD;
            rotation *= -1;
            offsetX += Math.cos(rotation) * offsetXTmp - Math.sin(rotation) * offsetZTmp;
            offsetZ += Math.sin(rotation) * offsetXTmp + Math.cos(rotation) * offsetZTmp;
            // y offset is handeled above since the vehicle is moved down
        }
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        entityRenderDispatcher.render(livingEntity, offsetX, offsetY, offsetZ, 0.0F, delta, matrixStack, bufferSource,
                15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        if (livingEntity instanceof PlayerAccess player) {
            player.setLastDeletaMovement(lastDeltaMovement);
        }
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        NMSHelper.setYRot(livingEntity, yRot);
        livingEntity.yRotO = yRotO;
        NMSHelper.setXRot(livingEntity, xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(deltaMovement);
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingVehicle) {
            livingVehicle.yBodyRot = vehicleYBodyRot;
            livingVehicle.yBodyRotO = vehicleYBodyRotO;
        }
        poseStack.popPose();
        resetViewMatrix();
        // #else
        // $$ Lighting.setupFor3DItems();
        // #endif
        // spotless:on
        Lighting.setupFor3DItems();
    }

    private void prepareViewMatrix(double xpos, double ypos) {
        // spotless:off
    	//#if MC >= 11700
        RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.pushMatrix();
        //$$ RenderSystem.translatef((float)xpos, (float)ypos, 1050.0F);
        //$$ RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        //#endif
        // spotless:on
    }

    private void resetViewMatrix() {
        // spotless:off
    	//#if MC >= 11700
        RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.popMatrix();
        //#endif
        // spotless:on
    }

    private void prepareLighting() {
        // spotless:off
    	//#if MC >= 11700
        Lighting.setupForEntityInInventory();
        //#else
        //$$ Lighting.setupForFlatItems();
        //#endif
        // spotless:on
    }

    // spotless:off
    	//#if MC >= 11903
        private void conjugate(Quaternionf quaternion2) {
        quaternion2.conjugate();
        //#else
        //$$     private void conjugate(Quaternion quaternion2) {
        //$$ quaternion2.conj();
        //#endif
        // spotless:on
    }

    private PoseStack getPoseStack() {
        // spotless:off
    	//#if MC >= 11700
        PoseStack poseStack = RenderSystem.getModelViewStack();
        //#else
        //$$ PoseStack poseStack = new PoseStack();
        //#endif
        // spotless:on
        return poseStack;
    }

    private void drawEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown, Entity entity,
            float delta, boolean lockHead) {
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        PoseStack poseStack = getPoseStack();
        poseStack.pushPose();
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            ypos -= (90f + entity.xRotO) / 90f * (size) - 5;
        }
        poseStack.translate(xpos, ypos, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        prepareViewMatrix(xpos, ypos);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        // spotless:off
    	//#if MC >= 11903
        Quaternionf quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion2 = NMSHelper.XP.rotationDegrees(rotationUp * 20.0F);
        //#else
        //$$Quaternion quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        //$$Quaternion quaternion2 = NMSHelper.XP.rotationDegrees(rotationUp * 20.0F);
        //#endif
        // spotless:on
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yRot = NMSHelper.getYRot(entity);
        float yRotO = entity.yRotO;
        float xRot = NMSHelper.getXRot(entity);
        float xRotO = entity.xRotO;
        Vec3 vel = entity.getDeltaMovement();
        Vec3 pos = entity.position();
        double yOld = entity.yOld;
        NMSHelper.setYRot(entity, 0);
        entity.yRotO = NMSHelper.getYRot(entity);
        entity.setDeltaMovement(Vec3.ZERO);
        // spotless:off
    	//#if MC >= 11700
        entity.setPos(pos.add(0, 500, 0)); // hack to disconnect minecarts from rails for the rendering
        //#endif
        // spotless:on
        entity.yOld += 500;
        if (lockHead) {
            NMSHelper.setXRot(entity, -rotationUp * 20.0F);
            entity.xRotO = NMSHelper.getXRot(entity);
        }
        prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        conjugate(quaternion2);
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        float extraRotation = 0;
        if (entity instanceof Minecart) {
            extraRotation += 90;
        }
        entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 180.0F + rotationSide * 20.0F + extraRotation, delta,
                matrixStack, bufferSource, 15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        NMSHelper.setYRot(entity, yRot);
        entity.yRotO = yRotO;
        NMSHelper.setXRot(entity, xRot);
        entity.xRotO = xRotO;
        entity.setDeltaMovement(vel);
        // spotless:off
    	//#if MC >= 11700
        entity.setPos(pos);
        //#endif
        // spotless:on
        entity.yOld = yOld;
        poseStack.popPose();
        resetViewMatrix();
        Lighting.setupFor3DItems();
    }

}
