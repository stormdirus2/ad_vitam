package net.fenrir.advitam.mixin;

import ladysnake.requiem.api.v1.possession.Possessable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 999)
public abstract class LivingEntityMixin extends EntityMixin {

    @Shadow
    protected int playerHitTimer;

    @Shadow
    @Nullable
    protected PlayerEntity attackingPlayer;


    @ModifyVariable(method = "drop", at = @At(value = "HEAD"), argsOnly = true)
    public DamageSource makeSpidersDropEyes(DamageSource deathCause) {
        return deathCause;
    }

    @Override
    public void preventSuffocation(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.getName().equals("inWall") && this instanceof Possessable && ((Possessable) this).getPossessor() != null) {
            cir.setReturnValue(true);
        }
    }

}
