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

import ladysnake.requiem.common.item.DemonSoulVesselItem;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.scirave.advitam.AdVitam;
import net.scirave.advitam.helpers.Consumable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.tag.api.QuiltTagKey;
import org.quiltmc.qsl.tag.api.TagType;

public class AdVitamItems {
    public static final TagKey<Item> WEAKNESS_GIVING = QuiltTagKey.of(Registry.ITEM_KEY, new Identifier(AdVitam.MOD_ID, "weakness_giving"), TagType.NORMAL);

    public static final FoodComponent TAINTED_MEAT = new FoodComponent.Builder().alwaysEdible().meat().hunger(1).statusEffect(
            new StatusEffectInstance(
                    StatusEffects.WEAKNESS,
                    600,
                    0), 1).build();

    public static final Item ROASTED_SPIDER_EYE = new Consumable(new Item.Settings().food(TAINTED_MEAT).group(ItemGroup.FOOD));
    public static DemonSoulVesselItem ENIGMATIC_SOUL_VESSEL = new DemonSoulVesselItem(AdVitamPlugin.WRAITH, Formatting.OBFUSCATED, new Item.Settings().group(ItemGroup.MISC).maxCount(1).rarity(Rarity.EPIC), AdVitam.MOD_ID + ":" + "enigmatic_soul_tooltip");


    public static void onInitialize(ModContainer modContainer) {
        Registry.register(Registry.ITEM, new Identifier(AdVitam.MOD_ID, "roasted_spider_eye"), ROASTED_SPIDER_EYE);
        Registry.register(Registry.ITEM, new Identifier(AdVitam.MOD_ID, "enigmatic_soul_vessel"), ENIGMATIC_SOUL_VESSEL);
    }
}
