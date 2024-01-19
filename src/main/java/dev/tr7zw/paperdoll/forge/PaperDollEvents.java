//#if FORGE || NEOFORGE
//$$package dev.tr7zw.paperdoll.forge;
//$$
//$$import dev.tr7zw.paperdoll.PaperDollShared;
//#if FORGE
//$$import net.minecraftforge.eventbus.api.SubscribeEvent;
//$$import net.minecraftforge.client.event.RenderGuiEvent;
//#else
//$$import net.neoforged.bus.api.SubscribeEvent;
//$$import net.neoforged.neoforge.client.event.RenderGuiEvent;
//#endif
//$$
//$$public class PaperDollEvents {
//$$
//$$    @SubscribeEvent
//$$    public void onOverlay(RenderGuiEvent.Post e) {
//$$    	PaperDollShared.instance.renderer.render(e.getPartialTick());
//$$    }
//$$	
//$$}
//#endif