package dev.tr7zw.paperdoll;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import net.minecraft.client.KeyMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.paperdoll.config.ConfigScreenProvider;
import dev.tr7zw.transition.loader.ModLoaderEventUtil;
import dev.tr7zw.transition.loader.ModLoaderUtil;
//#if FORGE || NEOFORGE
//$$ import dev.tr7zw.paperdoll.forge.PaperDollEvents;
//#endif

public class PaperDollShared {

    public static final Logger LOGGER = LogManager.getLogger("PaperDoll");
    public static PaperDollShared instance;
    private final File settingsFile = new File("config", "paperdoll.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final KeyMapping toggleKeybind = new KeyMapping("key.paperdoll.toggle", -1,
            "text.paperdoll.keybinds.title");
    private boolean toggleKeybindPressed = false;
    public PaperDollSettings settings = new PaperDollSettings();
    public PaperDollRenderer renderer;

    public void init() {
        instance = this;
        LOGGER.info("Loading PaperDoll!");
        renderer = new PaperDollRenderer();
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(ConfigScreenProvider::createConfigScreen);
        ModLoaderEventUtil.registerClientTickStartListener(this::onClientTick);
        ModLoaderUtil.registerKeybind(toggleKeybind);
        if (settingsFile.exists()) {
            try {
                settings = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        PaperDollSettings.class);
            } catch (Exception ex) {
                LOGGER.warn("Error while loading config! Creating a new one!", ex);
            }
        }
        if (settings == null) {
            settings = new PaperDollSettings();
            writeSettings();
        }
        //#if FORGE || NEOFORGE
        //#if MC <= 12004 || NEOFORGE
        //$$ ModLoaderUtil.registerForgeEvent(new PaperDollEvents()::onOverlay);
        //#endif
        //#if FORGE && MC >= 12105
        //$$ //RenderGuiEvent.Post.EVENT.register(new PaperDollEvents()::onOverlay);
        //#endif
        //#endif
    }

    public void writeSettings() {
        try {
            if (settingsFile.exists())
                settingsFile.delete();
            Files.write(settingsFile.toPath(), gson.toJson(settings).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            LOGGER.warn("Error while saving config!", e1);
        }
    }

    public void onClientTick() {
        if (toggleKeybind.isDown()) {
            if (toggleKeybindPressed)
                return;

            toggleKeybindPressed = true;
            settings.dollEnabled = !settings.dollEnabled;
        } else {
            toggleKeybindPressed = false;
        }
    }

}
