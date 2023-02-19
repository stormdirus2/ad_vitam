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

package net.scirave.advitam.components;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.components.declaration.MementoMoriComponent;
import net.scirave.advitam.components.definition.AnchorComponentImpl;
import net.scirave.advitam.components.definition.MementoMoriComponentImpl;

public final class AdVitamComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(AnchorComponent.KEY, AnchorComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(MementoMoriComponent.KEY, MementoMoriComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

}
