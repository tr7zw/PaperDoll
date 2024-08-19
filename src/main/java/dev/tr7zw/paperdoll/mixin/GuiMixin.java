package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
//spotless:off 
//#if MC >= 12100
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.LayeredDraw.Layer;
import net.minecraft.client.DeltaTracker;
//#endif
//#if MC >= 12000 && MC < 12100
//$$ import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on

@Mixin(Gui.class)
public class GuiMixin {

    // spotless:off 
    
    //#if MC >= 12100
    @Shadow
    private LayeredDraw layers;
    
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Minecraft minecraft, CallbackInfo ci) {
        layers.add(new Layer() {
            
            @Override
            public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
                float delta = deltaTracker.getGameTimeDeltaPartialTick(true);
    //#elseif MC >= 12000
    //$$ @Inject(at = @At("HEAD"), method = "render")
    //$$ public void render(GuiGraphics guiGraphics, float delta, CallbackInfo info) {
    //#else
    //$$ @Inject(at = @At("HEAD"), method = "render")
    //$$ public void render(PoseStack poseStack, float delta, CallbackInfo info) {
    //#endif
         PaperDollShared.instance.renderer.render(delta);
    //#if MC >= 12100
            }
        });
    //#endif
     }
    //spotless:on

}
