package dev.tr7zw.paperdoll;

public class PaperDollSettings {

    public boolean dollEnabled = true;
    public PaperDollLocation location = PaperDollLocation.TOP_LEFT;
    public int dollXOffset = 0;
    public int dollYOffset = 0;
    public int dollSize = 0;
    public int dollLookingSides = 20;
    public int dollLookingUpDown = -20;
    public DollHeadMode dollHeadMode = DollHeadMode.FREE;
    public boolean autoHide = false;

    public enum DollHeadMode {
        FREE, STATIC, LOCKED
    }

    public enum PaperDollLocation {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }

}
