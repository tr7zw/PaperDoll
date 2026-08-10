package dev.tr7zw.paperdoll.mixin;

import dev.tr7zw.trender.gui.client.*;
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

//? if >= 26.2 {

@Mixin(net.minecraft.client.gui.Hud.class)
//? } else {
/*
@Mixin(Gui.class)
*///? }
public class GuiMixin {

    //? if >= 26.2 {

    @Inject(at = @At("HEAD"), method = "extractRenderState")
    public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        RenderContext context = new RenderContext(guiGraphics);
        //? } else if >= 26.1 {
        /*
        @Inject(at = @At("HEAD"), method = "extractHotbarAndDecorations")
        public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        RenderContext context = new RenderContext(guiGraphics);
        *///? } else if >= 1.21.6 {
        /*
        @Inject(at = @At("HEAD"), method = "render")
        public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        RenderContext context = new RenderContext(guiGraphics);
        *///? } else if >= 1.21.0 {

        // @Shadow
        // private LayeredDraw layers;
        // @Inject(method = "<init>", at = @At("RETURN"))
        // public void init(Minecraft minecraft, CallbackInfo ci) {
        // layers.add(new Layer() {
        // @Override
        // public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        // float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
        // RenderContext context = new RenderContext(guiGraphics);
        //? } else if >= 1.20.0 {

        // @Inject(at = @At("HEAD"), method = "render")
        // public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, float delta, CallbackInfo info) {
        // RenderContext context = new RenderContext(guiGraphics);
        //? } else {

        // @Inject(at = @At("HEAD"), method = "render")
        // public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, float delta, CallbackInfo info) {
        // RenderContext context = null;
        //? }
        PaperDollShared.instance.renderer.render(delta, context);
        //spotless:off
        //? if >= 1.21.0 && < 1.21.6 {

        //    }
        // });
        //? }
    }

}
