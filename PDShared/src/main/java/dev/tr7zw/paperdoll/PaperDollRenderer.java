package dev.tr7zw.paperdoll;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
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
        if (!instance.settings.dollEnabled || mc_instance.options.renderDebug || mc_instance.level == null) {
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
            if (livingEntity.isBlocking() || livingEntity.isUsingItem() || livingEntity.isInPowderSnow
                    || livingEntity.swinging || livingEntity.isOnFire() || livingEntity.hurtTime > 0) {
                hide = false;
            }

            if (hide && System.currentTimeMillis() > showTill) {
                return;
            }
            if (!hide)
                showTill = System.currentTimeMillis() + 500;
        }

        if (playerEntity.isPassenger()) {
            Entity vehicle = playerEntity.getRootVehicle();
            vehicle.getPassengersAndSelf().forEachOrdered(entity -> {
                double yOffset = fYpos;
                if (entity != playerEntity)
                    yOffset += (playerEntity.getY() - entity.getY()) * 35;
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

    // Modified version from InventoryScreen
    private void drawLivingEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown,
            LivingEntity livingEntity, float delta, boolean lockHead) {
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            ypos -= (90f + livingEntity.xRotO) / 90f * (size) - 5;
        }
        poseStack.translate(xpos, ypos, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(rotationUp * 20.0F);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = livingEntity.getYRot();
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = livingEntity.getXRot();
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 vel = livingEntity.getDeltaMovement();
        float vehicleYBodyRot = 0;
        float vehicleYBodyRotO = 0;
        livingEntity.yBodyRot = 180.0F + rotationSide * 20.0F;
        livingEntity.setYRot(180.0F + rotationSide * 40.0F);
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = livingEntity.getYRot();
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingVehicle) {
            vehicleYBodyRot = livingVehicle.yBodyRot;
            vehicleYBodyRotO = livingVehicle.yBodyRotO;
            livingVehicle.yBodyRot = livingEntity.yBodyRot;
            livingVehicle.yBodyRotO = livingEntity.yBodyRotO;
        }
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            livingEntity.setDeltaMovement(Vec3.ZERO);
        }
        if (lockHead) {
            livingEntity.setXRot(-rotationUp * 20.0F);
            livingEntity.xRotO = livingEntity.getXRot();
            livingEntity.yHeadRot = livingEntity.getYRot();
            livingEntity.yHeadRotO = livingEntity.getYRot();
        } else {
            if (instance.settings.dollHeadMode == DollHeadMode.FREE) {
                livingEntity.yHeadRot = 180.0F + rotationSide * 40.0F - (yBodyRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + rotationSide * 40.0F - (yBodyRotO - yHeadRotO);
            } else {
                livingEntity.yHeadRot = 180.0F + rotationSide * 40.0F - (yRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + rotationSide * 40.0F - (yRotO - yHeadRotO);
            }
        }
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        quaternion2.conj();
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
            float rotation = vehicle.getYRot() - 180 - rotationSide * 20.0F; // target is 180
            rotation *= Mth.DEG_TO_RAD;
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
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        livingEntity.setYRot(yRot);
        livingEntity.yRotO = yRotO;
        livingEntity.setXRot(xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(vel);
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingVehicle) {
            livingVehicle.yBodyRot = vehicleYBodyRot;
            livingVehicle.yBodyRotO = vehicleYBodyRotO;
        }
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    private void drawEntity(double xpos, double ypos, int size, float lookSides, float lookUpDown, Entity entity,
            float delta, boolean lockHead) {
        float rotationSide = (float) Math.atan((double) (lookSides / 40.0F));
        float rotationUp = (float) Math.atan((double) (lookUpDown / 40.0F));
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            ypos -= (90f + entity.xRotO) / 90f * (size) - 5;
        }
        poseStack.translate(xpos, ypos, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(rotationUp * 20.0F);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        float yRot = entity.getYRot();
        float yRotO = entity.yRotO;
        float xRot = entity.getXRot();
        float xRotO = entity.xRotO;
        Vec3 vel = entity.getDeltaMovement();
        Vec3 pos = entity.position();
        double yOld = entity.yOld;
        entity.setYRot(0);
        entity.yRotO = entity.getYRot();
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setPos(pos.add(0, 500, 0)); // hack to disconnect minecarts from rails for the rendering
        entity.yOld += 500;
        if (lockHead) {
            entity.setXRot(-rotationUp * 20.0F);
            entity.xRotO = entity.getXRot();
        }
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        quaternion2.conj();
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
        entity.setYRot(yRot);
        entity.yRotO = yRotO;
        entity.setXRot(xRot);
        entity.xRotO = xRotO;
        entity.setDeltaMovement(vel);
        entity.setPos(pos);
        entity.yOld = yOld;
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

}
