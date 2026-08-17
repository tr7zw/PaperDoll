package dev.tr7zw.paperdoll.mixin;

import dev.tr7zw.paperdoll.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void isCurrentlyGlowing(CallbackInfoReturnable<Boolean> ci) {
        //? if >= 1.20 {
        if (((Entity) (Object) this).level().isClientSide() && PaperDollShared.instance.isExtractingPaperDoll) {
            ci.setReturnValue(false);
        }
        //? } else if >= 1.17 {
        /*
        if (((Entity) (Object) this).getLevel().isClientSide() && PaperDollShared.instance.isExtractingPaperDoll) {
            ci.setReturnValue(false);
        }
        *///? } else {
        /*
        if (((Entity) (Object) this).level.isClientSide() && PaperDollShared.instance.isExtractingPaperDoll) {
            ci.setReturnValue(false);
        }
        *///? }
    }

}
