package dev.tr7zw.paperdoll.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.tr7zw.paperdoll.gui.widget.PaperDollPreviewDummyWidget;
import dev.tr7zw.paperdoll.PaperDollSettings;
import dev.tr7zw.paperdoll.PaperDollSettings.DollHeadMode;
import dev.tr7zw.paperdoll.PaperDollSettings.PaperDollLocation;
import dev.tr7zw.paperdoll.PaperDollShared;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WTabPanel;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.util.ComponentProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

@UtilityClass
public class ConfigScreenProvider {

    public static Screen createConfigScreen(Screen parent) {
        return new ConfigScreen(parent).createScreen();
    }

    private static class ConfigScreen extends AbstractConfigScreen {

        public ConfigScreen(Screen previous) {
            super(ComponentProvider.translatable("text.paperdoll.title"), previous);

            WGridPanel root = new WGridPanel(8);
            root.setBackgroundPainter(BackgroundPainter.VANILLA);
            root.setInsets(Insets.ROOT_PANEL);
            setRootPanel(root);

            WTabPanel wTabPanel = new WTabPanel();

            PaperDollShared inst = PaperDollShared.instance;
            List<OptionInstance> generalOptions = new ArrayList<>();
            generalOptions.add(getOnOffOption("text.paperdoll.enabled", () -> inst.settings.dollEnabled,
                    b -> inst.settings.dollEnabled = b));
            generalOptions.add(getSplitLine("text.paperdoll.category.doll_settings"));
            generalOptions.add(getEnumOption("text.paperdoll.location", PaperDollLocation.class,
                    () -> inst.settings.location, (loc) -> inst.settings.location = loc));
            generalOptions.add(getEnumOption("text.paperdoll.headMode", DollHeadMode.class,
                    () -> inst.settings.dollHeadMode, (mode) -> inst.settings.dollHeadMode = mode));
            generalOptions.add(getIntOption("text.paperdoll.xOffset", -100, 100, () -> inst.settings.dollXOffset,
                    (i) -> inst.settings.dollXOffset = i));
            generalOptions.add(getIntOption("text.paperdoll.yOffset", -100, 100, () -> inst.settings.dollYOffset,
                    (i) -> inst.settings.dollYOffset = i));
            generalOptions.add(getIntOption("text.paperdoll.size", -12, 40, () -> inst.settings.dollSize,
                    (i) -> inst.settings.dollSize = i));
            generalOptions.add(getIntOption("text.paperdoll.lookingSides", -80, 80,
                    () -> inst.settings.dollLookingSides, (i) -> inst.settings.dollLookingSides = i));
            generalOptions.add(getIntOption("text.paperdoll.lookingUpDown", -80, 80,
                    () -> inst.settings.dollLookingUpDown, (i) -> inst.settings.dollLookingUpDown = i));
            generalOptions.add(getSplitLine("text.paperdoll.category.behavior_settings"));
            generalOptions.add(getOnOffOption("text.paperdoll.lock_elytra_rotation", () -> inst.settings.lockElytra,
                    (b) -> inst.settings.lockElytra = b));
            generalOptions.add(getOnOffOption("text.paperdoll.lock_spinning_rotation", () -> inst.settings.lockSpinning,
                    (b) -> inst.settings.lockSpinning = b));

            var generalOptionList = createOptionList(generalOptions);
            generalOptionList.setGap(-1);
            generalOptionList.setSize(14 * 20, 9 * 20);
            wTabPanel.add(generalOptionList,
                    b -> b.title(ComponentProvider.translatable("text.paperdoll.tab.general_options")));

            List<OptionInstance> autoHideOptions = new ArrayList<>();
            autoHideOptions.add(getOnOffOption("text.paperdoll.autohide", () -> inst.settings.autoHide,
                    (b) -> inst.settings.autoHide = b));
            autoHideOptions.add(getOnOffOption("text.paperdoll.hideInF5", () -> inst.settings.hideInF5,
                    (b) -> inst.settings.hideInF5 = b));
            autoHideOptions.add(getOnOffOption("text.paperdoll.hideVehicle", () -> inst.settings.hideVehicle,
                    (b) -> inst.settings.hideVehicle = b));

            autoHideOptions.add(getSplitLine("text.paperdoll.category.auto_hide_exceptions"));
            for (PaperDollSettings.AutoHideException condition : PaperDollSettings.AutoHideException.values()) {
                autoHideOptions
                        .add(getOnOffOption("text.paperdoll.auto_hide." + condition.name().toLowerCase(Locale.US),
                                () -> !inst.settings.autoHideBlacklist.contains(condition), (b) -> {
                                    if (b)
                                        inst.settings.autoHideBlacklist.remove(condition);
                                    else
                                        inst.settings.autoHideBlacklist.add(condition);
                                }));
            }

            var autoHideOptionList = createOptionList(autoHideOptions);
            autoHideOptionList.setGap(-1);
            autoHideOptionList.setSize(14 * 20, 9 * 20);

            wTabPanel.add(autoHideOptionList,
                    b -> b.title(ComponentProvider.translatable("text.paperdoll.tab.auto_hide_options")));

            root.add(wTabPanel, 0, 1);

            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                save();
                Minecraft.getInstance().setScreen(previous);
            });
            root.add(doneButton, 0, 27, 6, 2);

            WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
            resetButton.setOnClick(() -> {
                reset();
                root.layout();
            });
            root.add(resetButton, 29, 27, 6, 2);

            root.add(new PaperDollPreviewDummyWidget(), 0, 0);

            root.validate(this);
            root.setHost(this);
        }

        @Override
        public void save() {
            PaperDollShared.instance.writeSettings();
        }

        @Override
        public void reset() {
            PaperDollShared.instance.settings = new PaperDollSettings();
            PaperDollShared.instance.writeSettings();
        }

    }

}
