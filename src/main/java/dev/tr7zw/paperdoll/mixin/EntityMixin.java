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
        if (((Entity)(Object)this).level().isClientSide() && PaperDollShared.instance.isExtractingPaperDoll) {
            ci.setReturnValue(false);
        }
    }

}
