package dev.tr7zw.paperdoll.config;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.paperdoll.PaperDollSettings;
import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.paperdoll.PaperDollSettings.PaperDollLocation;
import dev.tr7zw.paperdoll.PaperDollShared;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 11900
import net.minecraft.client.OptionInstance;
//#else
//$$ import net.minecraft.client.Option;
//#endif
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@UtilityClass
public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.paperdoll.title") {

            @Override
            public void initialize() {
                PaperDollShared inst = PaperDollShared.instance;
                getOptions().addBig(getOnOffOption("text.paperdoll.enabled", () -> inst.settings.dollEnabled,
                        (b) -> inst.settings.dollEnabled = b));
                List<Object> options = new ArrayList<>();
                options.add(getEnumOption("text.paperdoll.location", PaperDollLocation.class,
                        () -> inst.settings.location, (loc) -> inst.settings.location = loc));
                options.add(getEnumOption("text.paperdoll.headMode", DollHeadMode.class,
                        () -> inst.settings.dollHeadMode, (mode) -> inst.settings.dollHeadMode = mode));
                options.add(getIntOption("text.paperdoll.xOffset", -100, 100, () -> inst.settings.dollXOffset,
                        (i) -> inst.settings.dollXOffset = i));
                options.add(getIntOption("text.paperdoll.yOffset", -100, 100, () -> inst.settings.dollYOffset,
                        (i) -> inst.settings.dollYOffset = i));
                options.add(getIntOption("text.paperdoll.size", -12, 40, () -> inst.settings.dollSize,
                        (i) -> inst.settings.dollSize = i));
                options.add(getIntOption("text.paperdoll.lookingSides", -80, 80, () -> inst.settings.dollLookingSides,
                        (i) -> inst.settings.dollLookingSides = i));
                options.add(getIntOption("text.paperdoll.lookingUpDown", -80, 80, () -> inst.settings.dollLookingUpDown,
                        (i) -> inst.settings.dollLookingUpDown = i));
                options.add(getOnOffOption("text.paperdoll.autohide", () -> inst.settings.autoHide,
                        (b) -> inst.settings.autoHide = b));
                options.add(getOnOffOption("text.paperdoll.hideInF5", () -> inst.settings.hideInF5,
                        (b) -> inst.settings.hideInF5 = b));

                //#if MC >= 11900
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                //#else
                //$$getOptions().addSmall(options.toArray(new Option[0]));
                //#endif
                // spotless:on

            }

            @Override
            public void save() {
                PaperDollShared.instance.writeSettings();
            }

            @Override
            //#if MC >= 12000
            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                super.render(guiGraphics, i, j, f);
                //#else
                //$$ public void render(PoseStack poseStack, int i, int j, float f) {
                //$$ super.render(poseStack, i, j, f);
                //#endif
                // spotless:on
                PaperDollShared.instance.renderer.render(f);
            }

            @Override
            public void reset() {
                PaperDollShared.instance.settings = new PaperDollSettings();
                PaperDollShared.instance.writeSettings();
            }

        };

    }

}
