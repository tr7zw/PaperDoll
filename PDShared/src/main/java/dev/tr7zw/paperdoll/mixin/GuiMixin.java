package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(at = @At("HEAD"), method = "render")
	public void render(PoseStack matrixStack, float delta, CallbackInfo info) {
	    PaperDollShared.instance.renderer.render(delta);
	}


}
