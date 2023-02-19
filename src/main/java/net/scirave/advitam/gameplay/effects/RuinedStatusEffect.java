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
import net.minecraft.entity.player.PlayerEntity;
import net.scirave.advitam.registry.AdVitamEffects;
import org.jetbrains.annotations.NotNull;

public class RuinedStatusEffect extends StatusEffect implements StickyStatusEffect {

    public RuinedStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    public static void apply(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(AdVitamEffects.RUINED, 300, 0, false, false, true));
    }

    @Override
    public boolean shouldStick(@NotNull LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            RemnantComponent component = RemnantComponent.get(player);
            return component.isVagrant();
        }
        return false;
    }
}
