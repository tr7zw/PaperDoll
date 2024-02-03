package dev.tr7zw.paperdoll;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.tr7zw.paperdoll.config.ConfigScreenProvider;
import dev.tr7zw.util.ModLoaderUtil;
//spotless:off
//#if FORGE || NEOFORGE
//$$ import dev.tr7zw.paperdoll.forge.PaperDollEvents;
//#endif
//spotless:on

public class PaperDollShared {

    public static final Logger LOGGER = LogManager.getLogger("PaperDoll");
    public static PaperDollShared instance;
    private final File settingsFile = new File("config", "paperdoll.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public PaperDollSettings settings = new PaperDollSettings();
    public PaperDollRenderer renderer;

    public void init() {
        instance = this;
        LOGGER.info("Loading PaperDoll!");
        renderer = new PaperDollRenderer();
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(ConfigScreenProvider::createConfigScreen);
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
        // spotless:off
        //#if FORGE || NEOFORGE
        //$$ ModLoaderUtil.registerForgeEvent(new PaperDollEvents()::onOverlay);
        //#endif
        //spotless:on
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

}
