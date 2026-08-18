package dev.tr7zw.paperdoll.tempholders;

import dev.tr7zw.paperdoll.*;
import dev.tr7zw.transition.logic.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;

public class DeltaMovementHolder implements TemporaryStateScope.TemporalHolder<Entity> {
    private Vec3 deltaMovement = null;
    private Vec3 lastDeltaMovement = null;

    @Override
    public void prepareState(Entity entity) {
        deltaMovement = entity.getDeltaMovement();
        if (entity instanceof PlayerAccess player) {
            lastDeltaMovement = player.getLastDelataMovement();
        }
    }

    @Override
    public void revertSate(Entity entity) {
        if (entity instanceof PlayerAccess player) {
            player.setLastDeletaMovement(lastDeltaMovement);
        }
        entity.setDeltaMovement(deltaMovement);
    }
}
