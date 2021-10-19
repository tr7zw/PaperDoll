package dev.tr7zw.paperdoll;

import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod("paperdoll")
public class PaperDollMod extends PaperDollShared {

    public PaperDollMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("PaperDoll Mod installed on a Server. Going to sleep.");
            return;
        }
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> new BiFunction<Minecraft, Screen, Screen>() {
                    @Override
                    public Screen apply(Minecraft t, Screen screen) {
                        return createConfigScreen(screen);
                    }
                }
        );
        init();
        MinecraftForge.EVENT_BUS.addListener(this::onOverlay);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post e) {
        if(e.getType() != ElementType.ALL)return;
        renderer.render(e.getPartialTicks());
    }
    
}
