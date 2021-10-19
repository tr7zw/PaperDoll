package dev.tr7zw.paperdoll;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class PaperDollModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return PaperDollShared.instance.createConfigScreen(parent);
        };
    }  
    
}
