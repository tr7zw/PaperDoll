package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.common.collect.ImmutableMap;
//#if MC >= 12106
import dev.tr7zw.paperdoll.future.CustomGuiEntityRenderState;
import dev.tr7zw.paperdoll.future.CustomGuiEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "STORE"), ordinal = 0)
    private ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> captureBuilder(
            ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder) {
        builder.put(CustomGuiEntityRenderState.class,
                new CustomGuiEntityRenderer(Minecraft.getInstance().renderBuffers().bufferSource(),
                        Minecraft.getInstance().getEntityRenderDispatcher()));
        return builder;
    }

}
//#else
//$$@Mixin(net.minecraft.client.Minecraft.class)
//$$public class GuiRendererMixin {
//$$    // This mixin is a placeholder for versions below 12106, as the custom GUI entity rendering is not supported.
//$$}
//#endif
