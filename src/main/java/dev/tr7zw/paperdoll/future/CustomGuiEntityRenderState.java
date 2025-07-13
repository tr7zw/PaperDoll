//#if MC >= 12106
package dev.tr7zw.paperdoll.future;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public record CustomGuiEntityRenderState(EntityRenderState renderState, PoseStack matrixStack, Vector3f translation,
        Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x0, int y0, int x1, int y1, float scale,
        double xpos, double ypos) implements PictureInPictureRenderState {

    public EntityRenderState renderState() {
        return this.renderState;
    }

    public Vector3f translation() {
        return this.translation;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    @Nullable
    public Quaternionf overrideCameraAngle() {
        return this.overrideCameraAngle;
    }

    public int x0() {
        return this.x0;
    }

    public int y0() {
        return this.y0;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public float scale() {
        return this.scale;
    }

    @Nullable
    public ScreenRectangle scissorArea() {
        return null;
    }

    @Nullable
    public ScreenRectangle bounds() {
        return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, null);
    }
}
//#endif