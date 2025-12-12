//? if fabric {

package dev.tr7zw.paperdoll;

import net.fabricmc.api.ClientModInitializer;

public class PaperDollMod extends PaperDollShared implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        init();
    }
}
//? }
