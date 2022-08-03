package dev.tr7zw.paperdoll;

import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("paperdoll")
public class PaperDollMod extends PaperDollShared {

    public PaperDollMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("PaperDoll Mod installed on a Server. Going to sleep.");
            return;
        }
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory((mc, screen) -> {
            return createConfigScreen(screen);
        }));
        init();
        MinecraftForge.EVENT_BUS.addListener(this::onOverlay);
    }

    @SubscribeEvent
    public void onOverlay(RenderGuiEvent.Post e) {
        renderer.render(e.getPartialTick());
    }
    
}
