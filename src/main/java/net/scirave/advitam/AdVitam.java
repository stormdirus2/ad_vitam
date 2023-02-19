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

package net.scirave.advitam;

import net.scirave.advitam.registry.AdVitamCriteria;
import net.scirave.advitam.registry.AdVitamEffects;
import net.scirave.advitam.registry.AdVitamGamerules;
import net.scirave.advitam.registry.AdVitamItems;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import java.util.logging.Logger;

public class AdVitam implements ModInitializer {
    public static final String MOD_ID = "advitam";
    public static Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitialize(ModContainer modContainer) {
        AdVitamGamerules.onInitialize(modContainer);
        AdVitamCriteria.onInitialize(modContainer);
        AdVitamItems.onInitialize(modContainer);
        AdVitamEffects.onInitialize(modContainer);
    }
}
