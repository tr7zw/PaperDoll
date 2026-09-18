package dev.tr7zw.paperdoll;

import net.minecraft.world.entity.*;

import java.util.*;

public class AutoHideLogic {

    public static boolean shouldAutoHide(LivingEntity livingEntity) {
        Set<PaperDollSettings.AutoHideException> blacklist = PaperDollShared.instance.settings.autoHideBlacklist;

        // Movement
        if (livingEntity.isCrouching() && !blacklist.contains(PaperDollSettings.AutoHideException.CROUCHING))
            return false;
        if (livingEntity.isSprinting() && !blacklist.contains(PaperDollSettings.AutoHideException.RUNNING))
            return false;
        if (livingEntity.isFallFlying() && !blacklist.contains(PaperDollSettings.AutoHideException.FALL_FLYING))
            return false;
        if (livingEntity.isVisuallySwimming() && !blacklist.contains(PaperDollSettings.AutoHideException.SWIMMING))
            return false;
        if (livingEntity.isPassenger() && !blacklist.contains(PaperDollSettings.AutoHideException.IN_VEHICLE))
            return false;

        // Combat
        if (livingEntity.isBlocking() && !blacklist.contains(PaperDollSettings.AutoHideException.BLOCKING))
            return false;
        if (livingEntity.isUsingItem() && !blacklist.contains(PaperDollSettings.AutoHideException.USING_ITEM))
            return false;
        //? if >= 26.3 {
        if (livingEntity.isSwinging() && !blacklist.contains(PaperDollSettings.AutoHideException.SWINGING))
            return false;
        //? } else {
        /*
        if (livingEntity.swinging && !blacklist.contains(PaperDollSettings.AutoHideException.SWINGING))
            return false;
         */
        //? }
        if (livingEntity.hurtTime > 0 && !blacklist.contains(PaperDollSettings.AutoHideException.TAKING_DAMAGE))
            return false;
        if (livingEntity.isOnFire() && !blacklist.contains(PaperDollSettings.AutoHideException.ON_FIRE))
            return false;
        //? if >= 1.17.0 {

        if (livingEntity.isInPowderSnow && !blacklist.contains(PaperDollSettings.AutoHideException.IN_POWDER_SNOW))
            return false;
        //? }

        return true;
    }

}
