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

package net.scirave.advitam.gameplay.effects;

import ladysnake.requiem.api.v1.remnant.StickyStatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.scirave.advitam.registry.AdVitamEffects;
import org.jetbrains.annotations.NotNull;

public class BulwarkStatusEffect extends StatusEffect implements StickyStatusEffect {

    public BulwarkStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public static void applyInfinite(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(AdVitamEffects.BULWARK, 60 * 60 * 20, 0, false, true, true));
    }

    @Override
    public boolean shouldStick(@NotNull LivingEntity entity) {
        return false;
    }

    @Override
    public boolean shouldFreezeDuration(@NotNull LivingEntity entity) {
        StatusEffectInstance instance = entity.getStatusEffect(AdVitamEffects.BULWARK);
        return instance != null && instance.getDuration() >= 60 * 60 * 20;
    }

}
