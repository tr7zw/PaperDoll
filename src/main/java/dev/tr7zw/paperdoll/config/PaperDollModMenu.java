//#if FABRIC
package dev.tr7zw.paperdoll.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class PaperDollModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreenProvider::createConfigScreen;
    }

}
//#endif