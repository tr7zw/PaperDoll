//#if FORGE || NEOFORGE
//$$package dev.tr7zw.paperdoll.forge;
//$$
//$$import dev.tr7zw.paperdoll.PaperDollShared;
//#if FORGE
//$$import net.minecraftforge.eventbus.api.SubscribeEvent;
//#if MC >= 11900
//$$import net.minecraftforge.client.event.RenderGuiEvent;
//#else
//$$ import net.minecraftforge.client.event.RenderGameOverlayEvent;
//$$ import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
//#endif
//#else
//$$import net.neoforged.bus.api.SubscribeEvent;
//$$import net.neoforged.neoforge.client.event.RenderGuiEvent;
//#endif
//$$
//$$public class PaperDollEvents {
//$$
//#if MC >= 11900
//$$    @SubscribeEvent
//$$    public void onOverlay(RenderGuiEvent.Post e) {
//$$    	PaperDollShared.instance.renderer.render(e.getPartialTick());
//$$    }
//#else
//$$    @SubscribeEvent
//$$    public void onOverlay(RenderGameOverlayEvent.Post e) {
//$$        if(e.getType() != ElementType.ALL)return;
//$$        PaperDollShared.instance.renderer.render(e.getPartialTicks());
//$$    }
//#endif
//$$	
//$$}
//#endif