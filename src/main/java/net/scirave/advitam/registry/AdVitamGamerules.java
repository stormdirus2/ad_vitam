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

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.entity.Entity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.scirave.advitam.AdVitam;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.ModContainer;


public class AdVitamGamerules {

    public static final GameRules.Key<GameRules.IntRule> REANIMATION_ATTRITION_CAP =
            register("reanimationCap", GameRuleFactory.createIntRule(-1, -1), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.IntRule> REANIMATION_ATTRITION_COST =
            register("reanimationCost", GameRuleFactory.createIntRule(1, 0), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.IntRule> HOST_REBELLION_ATTRITION =
            register("hostRebellionAttrition", GameRuleFactory.createIntRule(4, -1), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.IntRule> MEMENTO_DURATION =
            register("mementoDuration", GameRuleFactory.createIntRule(20 * 60 * 20, 0), GameRules.Category.PLAYER);
    public static final GameRules.Key<DoubleRule> MEMENTO_CONVERSION_RATE =
            register("mementoConversion", GameRuleFactory.createDoubleRule(1.0D, 0), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.BooleanRule> MEMENTO_DURATION_SCALES =
            register("mementoScales", GameRuleFactory.createBooleanRule(false), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.IntRule> SECOND_WIND_DURATION =
            register("secondWindDuration", GameRuleFactory.createIntRule(30 * 20, 0), GameRules.Category.PLAYER);
    public static final GameRules.Key<GameRules.IntRule> ATTRITION_SOFTLOCK =
            register("attritionSoftlock", GameRuleFactory.createIntRule(-1, -1), GameRules.Category.PLAYER);


    public static final GameRules.Key<DoubleRule> PASSIVE_DETECTION_MAX_RANGE =
            register("passiveDetectionMaxRange", GameRuleFactory.createDoubleRule(16.0D, 0.0D), GameRules.Category.MOBS);
    public static final GameRules.Key<DoubleRule> PASSIVE_DETECTION_SAFETY_MULTIPLIER =
            register("passiveDetectionSafetyMultiplier", GameRuleFactory.createDoubleRule(2.0D, 1.0D), GameRules.Category.MOBS);

    public static void onInitialize(ModContainer modContainer) {
        // Empty, but necessary
    }

    public static @NotNull <T extends GameRules.Rule<T>> T getGamerule(GameRules.Key<T> gamerule, World world) {
        return world.getGameRules().get(gamerule);
    }

    public static @NotNull <T extends GameRules.Rule<T>> T getGamerule(GameRules.Key<T> gamerule, Entity entity) {
        return getGamerule(gamerule, entity.getWorld());
    }

    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Type<T> type, GameRules.Category category) {
        return GameRuleRegistry.register(AdVitam.MOD_ID + ":" + name, category, type);
    }
}
