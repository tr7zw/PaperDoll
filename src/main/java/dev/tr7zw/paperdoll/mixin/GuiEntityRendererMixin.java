package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
//? if >= 26.1 {
import com.mojang.blaze3d.vertex.*;
import dev.tr7zw.paperdoll.*;
import dev.tr7zw.transition.mc.extending.*;
import net.minecraft.client.gui.render.pip.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.client.renderer.state.gui.pip.*;
import net.minecraft.client.renderer.state.level.*;
import net.minecraft.world.entity.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;

@Mixin(GuiEntityRenderer.class)
public class GuiEntityRendererMixin {

    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderToTexture", at = @At("TAIL"))
    //? if >= 26.2 {
    protected void renderToTexture(final GuiEntityRenderState entityState, final PoseStack poseStack,
            final SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
        //? } else {

        /*protected void renderToTexture(final GuiEntityRenderState entityState, final PoseStack poseStack, CallbackInfo ci) {
        var featureRenderDispatcher = net.minecraft.client.Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        var submitNodeCollector = featureRenderDispatcher.getSubmitNodeStorage();
        *///? }
        if (entityState.renderState() instanceof ExtensionHolder holder) {
            List<EntityRenderState> renderStates = holder.getExtension("PaperDollVehicles", List.class);
            if (renderStates != null) {
                CameraRenderState cameraRenderState = new CameraRenderState();
                Quaternionfc overriddenCameraAngle = entityState.overrideCameraAngle();
                if (overriddenCameraAngle != null) {
                    cameraRenderState.orientation = overriddenCameraAngle.conjugate(new Quaternionf())
                            .rotateY((float) Math.PI);
                }
                for (EntityRenderState state : renderStates) {
                    poseStack.pushPose();
                    poseStack.translate(0, state.y, 0);
                    entityRenderDispatcher.submit(state, cameraRenderState, (double) 0.0F, (double) 0.0F, (double) 0.0F,
                            poseStack, submitNodeCollector);
                    poseStack.popPose();
                }
                //? if = 26.1 {

                /*featureRenderDispatcher.renderAllFeatures();
                *///? }
            }
        }
    }

}
//? } else {

/*@Mixin(net.minecraft.client.Minecraft.class)
public class GuiEntityRendererMixin {
    // This mixin is a placeholder for versions below 26.1
}
*///? }