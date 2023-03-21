package dev.tr7zw.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.paperdoll.PaperDollShared;
import net.minecraft.SharedConstants;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Gui;
import net.minecraft.locale.Language;
import net.minecraft.server.Bootstrap;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        objenesis.newInstance(Gui.class);
    }
    
    @Test
    public void langTests() throws Throwable {
        Language lang = TestUtil.loadDefault("/assets/paperdoll/lang/en_us.json");
        PaperDollShared.instance = new TestMod();
        CustomConfigScreen screen = (CustomConfigScreen) PaperDollShared.instance.createConfigScreen(null);
        List<OptionInstance<?>> options = TestUtil.bootStrapCustomConfigScreen(screen);
        assertNotEquals(screen.getTitle().getString(), lang.getOrDefault(screen.getTitle().getString()));
        for(OptionInstance<?> option : options) {
            Set<String> keys = TestUtil.getKeys(option, true);
            for(String key : keys) {
                System.out.println(key + " " + lang.getOrDefault(key));
                assertNotEquals(key, lang.getOrDefault(key));
            }
        }
    }

}