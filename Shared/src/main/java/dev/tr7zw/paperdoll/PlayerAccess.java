package dev.tr7zw.paperdoll;

import net.minecraft.world.phys.Vec3;

public interface PlayerAccess {

    public Vec3 getLastDelataMovement();
    
    public void setLastDeletaMovement(Vec3 vec3);
    
}
