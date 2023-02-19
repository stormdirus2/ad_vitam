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

package net.scirave.advitam.gameplay;

import ladysnake.requiem.common.entity.effect.AttritionStatusEffect;
import ladysnake.requiem.common.remnant.DemonRemnantState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.gameplay.effects.RuinedStatusEffect;
import net.scirave.advitam.gameplay.effects.SecondWindStatusEffect;
import net.scirave.advitam.registry.AdVitamEffects;
import org.jetbrains.annotations.NotNull;

public class WraithRemnantState extends DemonRemnantState {
    public WraithRemnantState(PlayerEntity player) {
        super(player);
    }

    public static boolean shouldReanimateNewBody(PlayerEntity player) {
        return player.hasStatusEffect(AdVitamEffects.BOLSTERED) && player.hasStatusEffect(AdVitamEffects.RUINED);
    }

    @Override
    public void regenerateBody(@NotNull LivingEntity body) {
        super.regenerateBody(body);
        player.removeStatusEffect(AdVitamEffects.RUINED);
        SecondWindStatusEffect.apply(player);
    }

    @Override
    public void curePossessed(@NotNull LivingEntity body) {
        this.regenerateBody(body);
    }

    @Override
    public boolean canCurePossessed(@NotNull LivingEntity body) {
        if (player.hasStatusEffect(AdVitamEffects.RUINED) && !player.hasStatusEffect(AdVitamEffects.BOLSTERED)) {
            return false;
        }
        return super.canCurePossessed(body);
    }

    @Override
    public boolean canSplit(boolean forced) {
        return super.canSplit(forced) || player.hasStatusEffect(AdVitamEffects.BOLSTERED);
    }

    @Override
    public boolean canDissociateFrom(@NotNull MobEntity possessed) {
        return super.canDissociateFrom(possessed) || player.hasStatusEffect(AdVitamEffects.BOLSTERED);
    }

    @Override
    protected void onRespawnAfterDeath() {
        AttritionStatusEffect.apply(player);
        RuinedStatusEffect.apply(player);
        AnchorComponent.messageAnchorPosition(player);
    }
}
