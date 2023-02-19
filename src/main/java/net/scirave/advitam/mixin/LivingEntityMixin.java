/*
 * -------------------------------------------------------------------
 * Ad Vitam
 * Copyright (c) 2023 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.advitam.mixin;

import ladysnake.requiem.api.v1.possession.Possessable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.scirave.advitam.components.declaration.MementoMoriComponent;
import net.scirave.advitam.registry.AdVitamEffects;
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


    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    public void advitam$ruinedPreventHealing(float amount, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            if (MementoMoriComponent.shouldNotHeal(player)) {
                ci.cancel();
            }
        } else {
            Possessable possessable = (Possessable) this;
            PlayerEntity possessor = possessable.getPossessor();
            if (possessor != null && MementoMoriComponent.shouldNotHeal(possessor)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    public void advitam$hostKicksOut(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (MementoMoriComponent.attemptKickOutFrom((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "drop", at = @At(value = "HEAD"), argsOnly = true)
    public DamageSource advitam$makeSpidersDropEyes(DamageSource deathCause) {
        return deathCause;
    }

    @Override
    public void advitam$secondWind(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasStatusEffect(AdVitamEffects.SECOND_WIND)) {
            String name = damageSource.getName();
            if (damageSource.isFire() || name.equals("inWall") || name.equals("fall") || name.equals("drown") || name.equals("dryout") || name.equals("hurt_by_water") || name.equals("cramming")) {
                cir.setReturnValue(true);
            }
        }
    }

}
