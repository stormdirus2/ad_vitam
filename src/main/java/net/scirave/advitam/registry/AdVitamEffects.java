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

package net.scirave.advitam.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.scirave.advitam.AdVitam;
import net.scirave.advitam.gameplay.effects.BolsteredStatusEffect;
import net.scirave.advitam.gameplay.effects.MementoStatusEffect;
import net.scirave.advitam.gameplay.effects.RuinedStatusEffect;
import net.scirave.advitam.gameplay.effects.SecondWindStatusEffect;
import org.quiltmc.loader.api.ModContainer;

public class AdVitamEffects {
    public static final StatusEffect SECOND_WIND = new SecondWindStatusEffect(StatusEffectType.BENEFICIAL, 0x00dbff);
    public static final StatusEffect RUINED = new RuinedStatusEffect(StatusEffectType.HARMFUL, 0x000000);
    public static final StatusEffect MEMENTO = new MementoStatusEffect(StatusEffectType.HARMFUL, 0x000000);
    public static final StatusEffect BOLSTERED = new BolsteredStatusEffect(StatusEffectType.BENEFICIAL, 0xFFFFFF);
    public static final StatusEffect BULWARK = new BolsteredStatusEffect(StatusEffectType.NEUTRAL, 0x194b4b);

    public static void onInitialize(ModContainer modContainer) {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(AdVitam.MOD_ID, "second_wind"), SECOND_WIND);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(AdVitam.MOD_ID, "ruined"), RUINED);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(AdVitam.MOD_ID, "memento"), MEMENTO);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(AdVitam.MOD_ID, "bolstered"), BOLSTERED);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(AdVitam.MOD_ID, "bulwark"), BULWARK);
    }
}
