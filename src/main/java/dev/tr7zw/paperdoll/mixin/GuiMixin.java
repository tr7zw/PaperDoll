package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
//#if MC >= 12100
//#if MC < 12106
//$$import net.minecraft.client.gui.LayeredDraw;
//$$import net.minecraft.client.gui.LayeredDraw.Layer;
//$$import org.spongepowered.asm.mixin.Shadow;
//$$import net.minecraft.client.Minecraft;
//#endif
import net.minecraft.client.DeltaTracker;
//#endif
//#if MC >= 12000 && MC < 12100
//$$ import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@Mixin(Gui.class)
public class GuiMixin {

    //#if MC >= 12106
    @Inject(at = @At("HEAD"), method = "render")
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        PaperDollShared.instance.renderer.setGuiGraphics(guiGraphics);
        //#elseif MC >= 12100
        //$$ @Shadow
        //$$ private LayeredDraw layers;
        //$$ @Inject(method = "<init>", at = @At("RETURN"))
        //$$public void init(Minecraft minecraft, CallbackInfo ci) {
        //$$layers.add(new Layer() {
        //$$ @Override
        //$$ public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        //$$float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        //#elseif MC >= 12000
        //$$ @Inject(at = @At("HEAD"), method = "render")
        //$$ public void render(GuiGraphics guiGraphics, float delta, CallbackInfo info) {
        //#else
        //$$ @Inject(at = @At("HEAD"), method = "render")
        //$$ public void render(PoseStack poseStack, float delta, CallbackInfo info) {
        //#endif
        PaperDollShared.instance.renderer.render(delta);
        //spotless:off
        //#if MC >= 12100 && MC < 12106
        //$$    }
        //$$});
        //#endif
    }

}
