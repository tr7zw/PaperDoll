//#if MC >= 12106
package dev.tr7zw.paperdoll.future;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public class CustomGuiEntityRenderer extends PictureInPictureRenderer<CustomGuiEntityRenderState> {
    private final EntityRenderDispatcher entityRenderDispatcher;

    private final CachedOrthoProjectionMatrixBuffer projectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer(
            "PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true);

    public CustomGuiEntityRenderer(MultiBufferSource.BufferSource bufferSource,
            EntityRenderDispatcher entityRenderDispatcher) {
        super(bufferSource);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public Class<CustomGuiEntityRenderState> getRenderStateClass() {
        return CustomGuiEntityRenderState.class;
    }

    protected void renderToTexture(CustomGuiEntityRenderState guiEntityRenderState, PoseStack poseStack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f vector3f = guiEntityRenderState.translation();
        poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
        poseStack.mulPose(guiEntityRenderState.rotation());
        Quaternionf quaternionf = guiEntityRenderState.overrideCameraAngle();
        if (quaternionf != null) {
            this.entityRenderDispatcher
                    .overrideCameraOrientation(quaternionf.conjugate(new Quaternionf()).rotateY((float) Math.PI));
        }

        this.entityRenderDispatcher.setRenderShadow(false);
        this.entityRenderDispatcher.render(guiEntityRenderState.renderState(), 0.0, 0.0, 0.0, poseStack,
                this.bufferSource, 15728880);
        this.entityRenderDispatcher.setRenderShadow(true);
    }

    @Override
    public void prepare(CustomGuiEntityRenderState pictureInPictureRenderState, GuiRenderState guiRenderState, int i) {
        RenderSystem.setProjectionMatrix(
                this.projectionMatrixBuffer.getBuffer(Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                        Minecraft.getInstance().getWindow().getGuiScaledHeight()),
                ProjectionType.ORTHOGRAPHIC);
        PoseStack poseStack = new PoseStack();
        poseStack.translate(pictureInPictureRenderState.xpos(), pictureInPictureRenderState.ypos(), 0.0F);
        float f = (float) pictureInPictureRenderState.scale();
        poseStack.scale(f, f, -f);
        this.renderToTexture(pictureInPictureRenderState, poseStack);
        this.bufferSource.endBatch();
    }

    @Override
    protected float getTranslateY(int i, int j) {
        return i / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "customentity";
    }
}
//#endif