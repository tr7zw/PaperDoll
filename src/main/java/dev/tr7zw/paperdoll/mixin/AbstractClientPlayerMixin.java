package dev.tr7zw.paperdoll.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tr7zw.paperdoll.PlayerAccess;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements PlayerAccess {

    //? if >= 1.21.10 {

    @Override
    public Vec3 getLastDelataMovement() {

        return null;
    }

    @Override
    public void setLastDeletaMovement(Vec3 vec3) {

    }
    //? } else if >= 1.19.4 {
    /*
    @Shadow
    private Vec3 deltaMovementOnPreviousTick;
    
    @Override
    public Vec3 getLastDelataMovement() {
    
        return deltaMovementOnPreviousTick;
    }
    
    @Override
    public void setLastDeletaMovement(Vec3 vec3) {
        deltaMovementOnPreviousTick = vec3;
    }
    *///? } else {

    // public Vec3 getLastDelataMovement() { return Vec3.ZERO;}
    // public void setLastDeletaMovement(Vec3 vec3) {}
    //? }

}
