package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

//? if >= 26.1 {
import net.minecraft.client.gui.components.toasts.*;

@Mixin(ToastManager.class)
public interface ToastManagerAccessor {

    @Accessor("nowPlayingToast")
    ToastManager.ToastInstance<NowPlayingToast> getNowPlayingToast();

}
//? } else {
/*
@Mixin(net.minecraft.client.Minecraft.class)
public class ToastManagerAccessor {
    // This mixin is a placeholder for versions below 26.1
}
*///? }