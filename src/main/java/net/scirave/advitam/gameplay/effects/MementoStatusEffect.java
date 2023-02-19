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

import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.StickyStatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.scirave.advitam.registry.AdVitamEffects;
import net.scirave.advitam.registry.AdVitamGamerules;
import org.jetbrains.annotations.NotNull;

public class MementoStatusEffect extends StatusEffect implements StickyStatusEffect {

    public MementoStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public static void apply(LivingEntity livingEntity, int level) {
        int duration = AdVitamGamerules.getGamerule(AdVitamGamerules.MEMENTO_DURATION, livingEntity).get();
        if (AdVitamGamerules.getGamerule(AdVitamGamerules.MEMENTO_DURATION_SCALES, livingEntity).get()) {
            duration *= 1 + level;
        }

        if (duration > 0) {
            livingEntity.addStatusEffect(new StatusEffectInstance(AdVitamEffects.MEMENTO, duration, level, false, false, true));
        }
    }

    @Override
    public boolean shouldStick(@NotNull LivingEntity entity) {
        return !RemnantComponent.isVagrant(entity);
    }

    @Override
    public boolean shouldFreezeDuration(@NotNull LivingEntity entity) {
        return false;
    }

}
