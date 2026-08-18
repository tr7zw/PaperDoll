package dev.tr7zw.paperdoll.tempholders;

import dev.tr7zw.transition.logic.*;
import net.minecraft.world.entity.*;

public class LivingVehicleHolder implements TemporaryStateScope.TemporalHolder<Entity> {
    float vehicleYBodyRot = 0;
    float vehicleYBodyRotO = 0;

    @Override
    public void prepareState(Entity entity) {
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingVehicle) {
            vehicleYBodyRot = livingVehicle.yBodyRot;
            vehicleYBodyRotO = livingVehicle.yBodyRotO;
        }
    }

    @Override
    public void revertSate(Entity entity) {
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity livingVehicle) {
            livingVehicle.yBodyRot = vehicleYBodyRot;
            livingVehicle.yBodyRotO = vehicleYBodyRotO;
        }
    }
}
