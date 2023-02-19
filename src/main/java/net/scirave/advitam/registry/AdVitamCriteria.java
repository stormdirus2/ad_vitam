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

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;
import net.scirave.advitam.AdVitam;
import net.scirave.advitam.gameplay.criteria.HostCriteria;
import net.scirave.advitam.gameplay.criteria.PlayerCriteria;
import org.quiltmc.loader.api.ModContainer;

public class AdVitamCriteria {

    public static final HostCriteria PLAYER_REANIMATED = new HostCriteria(new Identifier(AdVitam.MOD_ID, "player_reanimated"));
    public static final HostCriteria HOST_REBELLED = new HostCriteria(new Identifier(AdVitam.MOD_ID, "host_rebelled"));
    public static final PlayerCriteria MEMENTO_CONVERTED = new PlayerCriteria(new Identifier(AdVitam.MOD_ID, "memento_converted"));
    public static final PlayerCriteria ANCHOR_ATTUNEMENT = new PlayerCriteria(new Identifier(AdVitam.MOD_ID, "anchor_attunement"));


    public static void onInitialize(ModContainer modContainer) {
        Criteria.register(PLAYER_REANIMATED);
        Criteria.register(HOST_REBELLED);
        Criteria.register(MEMENTO_CONVERTED);
        Criteria.register(ANCHOR_ATTUNEMENT);
    }
}
