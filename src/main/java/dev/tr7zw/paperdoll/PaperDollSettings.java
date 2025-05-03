package dev.tr7zw.paperdoll;

import java.util.HashSet;
import java.util.Set;

public class PaperDollSettings {

    public boolean dollEnabled = true;
    public PaperDollLocation location = PaperDollLocation.TOP_LEFT;
    public int dollXOffset = 0;
    public int dollYOffset = 0;
    public int dollSize = 0;
    public int dollLookingSides = 20;
    public int dollLookingUpDown = -20;
    public DollHeadMode dollHeadMode = DollHeadMode.FREE;
    public boolean lockElytra = true;
    public boolean lockSpinning = true;
    public boolean autoHide = false;
    public final Set<AutoHideException> autoHideBlacklist = new HashSet<>();
    public boolean hideInF5 = true;
    public boolean hideVehicle = false;

    public enum DollHeadMode {
        FREE, STATIC, LOCKED, FREE_HORIZONTAL, STATIC_HORIZONTAL
    }

    public enum PaperDollLocation {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

    public enum AutoHideException {
        CROUCHING, RUNNING, FALL_FLYING, SWIMMING, IN_VEHICLE, BLOCKING, USING_ITEM, SWINGING, TAKING_DAMAGE, ON_FIRE,
        IN_POWDER_SNOW
    }

}
