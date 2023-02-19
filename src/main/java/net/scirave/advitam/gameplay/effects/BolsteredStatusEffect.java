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

public class BolsteredStatusEffect extends StatusEffect {

    public BolsteredStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public static void apply(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(AdVitamEffects.BOLSTERED, 60, 0, true, false, true));
    }
}
