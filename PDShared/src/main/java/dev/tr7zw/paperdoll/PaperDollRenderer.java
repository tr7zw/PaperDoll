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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PaperDollRenderer {

    private final Minecraft mc_instance = Minecraft.getInstance();
    private final PaperDollShared instance = PaperDollShared.instance;
    
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
        int size = 25 + instance.settings.dollSize;
        int lookSides = -instance.settings.dollLookingSides;
        int lookUpDown = instance.settings.dollLookingUpDown;
        if (mc_instance.player.isFallFlying() || mc_instance.player.isAutoSpinAttack()) {
            lookSides = 0;
            lookUpDown = 40;
        }
        LivingEntity playerEntity = mc_instance.player;
        if (mc_instance.getCameraEntity() != playerEntity && mc_instance.getCameraEntity() instanceof LivingEntity) {
            playerEntity = (LivingEntity) mc_instance.getCameraEntity();
        }
        drawEntity(xpos, ypos, size, lookSides, lookUpDown, playerEntity, delta,
                instance.settings.dollHeadMode == DollHeadMode.LOCKED);
    }
    
    // Modified version from InventoryScreen
    private void drawEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity, float delta,
            boolean lockHead) {
        float h = (float) Math.atan((double) (f / 40.0F));
        float l = (float) Math.atan((double) (g / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef(i, j, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 0.0D, 1000.0D);
        poseStack.scale((float) k, (float) k, (float) k);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(l * 20.0F);
        quaternion.mul(quaternion2);
        poseStack.mulPose(quaternion);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = livingEntity.yRot;
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = livingEntity.xRot;
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 vel = livingEntity.getDeltaMovement();
        livingEntity.yBodyRot = 180.0F + h * 20.0F;
        livingEntity.yRot = (180.0F + h * 40.0F);
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = livingEntity.yRot;
        livingEntity.setDeltaMovement(Vec3.ZERO);
        if (lockHead) {
            livingEntity.xRot = (-l * 20.0F);
            livingEntity.xRotO = livingEntity.xRot;
            livingEntity.yHeadRot = livingEntity.yRot;
            livingEntity.yHeadRotO = livingEntity.yRot;
        } else {
            if (instance.settings.dollHeadMode == DollHeadMode.FREE) {
                livingEntity.yHeadRot = 180.0F + h * 40.0F - (yBodyRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + h * 40.0F - (yBodyRotO - yHeadRotO);
            } else {
                livingEntity.yHeadRot = 180.0F + h * 40.0F - (yRot - yHeadRot);
                livingEntity.yHeadRotO = 180.0F + h * 40.0F - (yRotO - yHeadRotO);
            }
        }
        Lighting.setupForFlatItems();
        EntityRenderDispatcher entityRenderDispatcher = mc_instance.getEntityRenderDispatcher();
        quaternion2.conj();
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, poseStack, bufferSource, 15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        livingEntity.yRot = (yRot);
        livingEntity.yRotO = yRotO;
        livingEntity.xRot = (xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(vel);
        RenderSystem.popMatrix();
        Lighting.setupFor3DItems();
    }
    
}
