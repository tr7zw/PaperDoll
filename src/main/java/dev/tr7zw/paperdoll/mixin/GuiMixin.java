package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.client.gui.Gui;

//spotless:off 
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(at = @At("HEAD"), method = "render")
    // spotless:off 
    //#if MC >= 12000
    public void render(GuiGraphics guiGraphics, float delta, CallbackInfo info) {
	//#else
	//$$ public void render(PoseStack poseStack, float delta, CallbackInfo info) {
	//#endif
	//spotless:on
        PaperDollShared.instance.renderer.render(delta);
    }

}
