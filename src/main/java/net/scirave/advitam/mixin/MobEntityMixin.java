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

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.scirave.advitam.helpers.MobEntityInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntityMixin implements MobEntityInterface {

    @Shadow
    @Final
    protected GoalSelector targetSelector;

    boolean deferredReset = false;

    @Inject(method = "tick", at = @At("TAIL"))
    public void advitam$deferredResetTargeting(CallbackInfo ci) {
        if (!this.world.isClient() && deferredReset) {
            deferredReset = false;
            this.resetTargeting();
        }
    }

    @Override
    public void resetTargeting() {
        Set<PrioritizedGoal> prioritizedGoalSet = this.targetSelector.getGoals();
        for (PrioritizedGoal entry : prioritizedGoalSet) {
            Goal goal = entry.getGoal();
            if (goal instanceof TrackTargetGoal trackTargetGoal) {
                trackTargetGoal.stop();
            }
        }
    }

    @Override
    public void resetTargetingDeferred() {
        deferredReset = true;
    }
}
