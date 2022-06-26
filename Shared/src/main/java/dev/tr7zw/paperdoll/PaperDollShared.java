package dev.tr7zw.paperdoll;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.paperdoll.PaperDollSettings.PaperDollLocation;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;

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
        if (settingsFile.exists()) {
            try {
                settings = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        PaperDollSettings.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (settings == null) {
            settings = new PaperDollSettings();
            writeSettings();
        }
    }
    
    public void writeSettings() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(settings).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.paperdoll.title") {
            
            @Override
            public void initialize() {
                getOptions().addBig(getOnOffOption("text.paperdoll.enabled", () -> settings.dollEnabled, (b) -> settings.dollEnabled = b));
                List<OptionInstance<?>> options = new ArrayList<>();
                options.add(getEnumOption("text.paperdoll.location", PaperDollLocation.class, () -> settings.location, (loc) -> settings.location = loc));
                options.add(getEnumOption("text.paperdoll.headMode", DollHeadMode.class, () -> settings.dollHeadMode, (mode) -> settings.dollHeadMode = mode));
                options.add(getIntOption("text.paperdoll.xOffset", -50, 50, () -> settings.dollXOffset, (i) -> settings.dollXOffset = i));
                options.add(getIntOption("text.paperdoll.yOffset", -50, 50, () -> settings.dollYOffset, (i) -> settings.dollYOffset = i));
                options.add(getIntOption("text.paperdoll.size", -12, 40, () -> settings.dollSize, (i) -> settings.dollSize = i));
                options.add(getIntOption("text.paperdoll.lookingSides", -80, 80, () -> settings.dollLookingSides, (i) -> settings.dollLookingSides = i));
                options.add(getIntOption("text.paperdoll.lookingUpDown", -80, 80, () -> settings.dollLookingUpDown, (i) -> settings.dollLookingUpDown = i));
                options.add(getOnOffOption("text.paperdoll.autohide", () -> settings.autoHide, (b) -> settings.autoHide = b));
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                
            }
            
            @Override
            public void save() {
                writeSettings();
            }

            @Override
            public void render(PoseStack poseStack, int i, int j, float f) {
                super.render(poseStack, i, j, f);
                renderer.render(f);
            }

            @Override
            public void reset() {
                settings = new PaperDollSettings();
                writeSettings();
            }
            
        };
        
        return screen;
    }
    
}
