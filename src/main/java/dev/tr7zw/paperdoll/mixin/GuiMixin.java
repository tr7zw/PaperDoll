package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.client.gui.Gui;
//? if >= 1.21.0 {

//? if < 1.21.6 {

// import net.minecraft.client.gui.LayeredDraw;
// import net.minecraft.client.gui.LayeredDraw.Layer;
// import org.spongepowered.asm.mixin.Shadow;
// import net.minecraft.client.Minecraft;
//? }
import net.minecraft.client.DeltaTracker;
//? }

@Mixin(Gui.class)
public class GuiMixin {

    //? if >= 1.21.6 {

    @Inject(at = @At("HEAD"), method = "render")
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        //? } else if >= 1.21.0 {

        // @Shadow
        // private LayeredDraw layers;
        // @Inject(method = "<init>", at = @At("RETURN"))
        // public void init(Minecraft minecraft, CallbackInfo ci) {
        // layers.add(new Layer() {
        // @Override
        // public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        //? } else if >= 1.20.0 {

        // @Inject(at = @At("HEAD"), method = "render")
        // public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, float delta, CallbackInfo info) {
        //? } else {

        // @Inject(at = @At("HEAD"), method = "render")
        // public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, float delta, CallbackInfo info) {
        //? }
        PaperDollShared.instance.renderer.render(delta);
        //spotless:off
        //? if >= 1.21.0 && < 1.21.6 {

        //    }
        // });
        //? }
    }

}
