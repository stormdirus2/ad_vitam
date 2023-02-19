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

import ladysnake.requiem.api.v1.remnant.RemnantState;
import ladysnake.requiem.common.remnant.RemnantComponentImpl;
import net.minecraft.entity.mob.MobEntity;
import net.scirave.advitam.helpers.RemnantComponentInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RemnantComponentImpl.class)
public class RemnantComponentImplMixin implements RemnantComponentInterface {

    boolean preventFracture = false;

    @Shadow(remap = false)
    private RemnantState state;

    @Override
    public RemnantState advitam$getState() {
        return this.state;
    }

    @Override
    public void advitam$setPreventFracture(boolean prevent) {
        this.preventFracture = prevent;
    }

    @Inject(method = "canSplitPlayer", at = @At("HEAD"), cancellable = true, remap = false)
    public void advitam$preventSplit(boolean forced, CallbackInfoReturnable<Boolean> cir) {
        if (preventFracture) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canDissociateFrom", at = @At("HEAD"), cancellable = true, remap = false)
    public void advitam$preventDissociate(MobEntity possessed, CallbackInfoReturnable<Boolean> cir) {
        if (preventFracture) {
            cir.setReturnValue(false);
        }
    }

}
