package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//#if MC >= 12106
import dev.tr7zw.paperdoll.future.GameRendererAccessor;
import lombok.Getter;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements GameRendererAccessor {

    @Shadow
    @Getter
    private GuiRenderState guiRenderState;

}
//#else
//$$@Mixin(net.minecraft.client.Minecraft.class)
//$$public class GameRendererMixin {
//$$    // This mixin is a placeholder for versions below 12106, as the custom GUI entity rendering is not supported.
//$$}
//#endif
