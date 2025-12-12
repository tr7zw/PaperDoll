//? if forge || neoforge {

// package dev.tr7zw.paperdoll.forge;
//
// import dev.tr7zw.paperdoll.PaperDollShared;
// //? if forge {

// //? if >= 1.20.5 {

// //? } else if >= 1.19.0 {

// // import net.minecraftforge.client.event.RenderGuiEvent;
// //? } else {

// // import net.minecraftforge.client.event.RenderGameOverlayEvent;
// // import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
// //? }
// //? } else {

// // import net.neoforged.neoforge.client.event.RenderGuiEvent;
// //? }
//
// public class PaperDollEvents {
//
// //? if neoforge {

// //? if >= 1.21.0 {

// //    public void onOverlay(RenderGuiEvent.Post e) {
// //            PaperDollShared.instance.renderer.render(e.getPartialTick().getGameTimeDeltaPartialTick(false));
// //    }
// //? } else {

// //    public void onOverlay(RenderGuiEvent.Post e) {
// //            PaperDollShared.instance.renderer.render(e.getPartialTick());
// //    }
// //? }
// //? } else {

// //? if >= 1.20.5 {

// //? } else if >= 1.19.0 {

// //    public void onOverlay(RenderGuiEvent.Post e) {
// //    	PaperDollShared.instance.renderer.render(e.getPartialTick());
// //    }
// //? } else {

// //    public void onOverlay(RenderGameOverlayEvent.Post e) {
// //        if(e.getType() != ElementType.ALL)return;
// //        PaperDollShared.instance.renderer.render(e.getPartialTicks());
// //    }
// //? }
// //? }
//
// }
//? }
