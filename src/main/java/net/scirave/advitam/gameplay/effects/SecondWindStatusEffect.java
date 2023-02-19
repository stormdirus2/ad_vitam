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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.scirave.advitam.registry.AdVitamEffects;
import net.scirave.advitam.registry.AdVitamGamerules;

public class SecondWindStatusEffect extends StatusEffect {

    public SecondWindStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public static void apply(LivingEntity livingEntity) {
        int duration = AdVitamGamerules.getGamerule(AdVitamGamerules.SECOND_WIND_DURATION, livingEntity).get();
        if (duration > 0) {
            livingEntity.addStatusEffect(new StatusEffectInstance(AdVitamEffects.SECOND_WIND, duration, 0, false, true, true));
        }
    }

}
