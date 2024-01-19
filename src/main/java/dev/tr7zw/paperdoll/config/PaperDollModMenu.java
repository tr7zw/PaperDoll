package dev.tr7zw.paperdoll.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.tr7zw.paperdoll.PaperDollShared;

public class PaperDollModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return PaperDollShared.instance.createConfigScreen(parent);
        };
    }

}
