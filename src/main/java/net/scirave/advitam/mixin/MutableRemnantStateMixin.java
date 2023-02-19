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

import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.core.remnant.MutableRemnantState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.scirave.advitam.helpers.MutableRemnantStateInterface;
import net.scirave.advitam.helpers.RemnantComponentInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MutableRemnantState.class)
public abstract class MutableRemnantStateMixin implements MutableRemnantStateInterface {
    @Shadow
    @Final
    protected PlayerEntity player;

    @Shadow
    protected abstract void regenerateBody(LivingEntity body);

    @Override
    public void advitam$regenerateBody(LivingEntity body) {
        this.regenerateBody(body);
    }

    @Inject(method = "serverTick", at = @At("HEAD"), remap = false)
    public void advitam$resetSplitPrevention(CallbackInfo ci) {
        RemnantComponent component = RemnantComponent.get(player);
        ((RemnantComponentInterface) component).advitam$setPreventFracture(false);
    }
}
