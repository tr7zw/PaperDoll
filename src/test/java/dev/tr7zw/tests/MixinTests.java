package dev.tr7zw.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
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
        objenesis.newInstance(LocalPlayer.class);
    }

}
