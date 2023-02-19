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

import ladysnake.requiem.client.RequiemClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public final class AdVitamFx implements ClientTickEvents.End {
    private int zoomDuration = 0;

    public void onInitialize(ModContainer container) {
        ClientTickEvents.END.register(this);
    }

    public void zoomAnimation(int zoomDuration, Entity target) {
        RequiemClient.instance().fxRenderer().beginFishEyeAnimation(target);
        this.zoomDuration = zoomDuration;
    }

    @Override
    public void endClientTick(MinecraftClient client) {
        if (zoomDuration > 1) {
            this.zoomDuration--;
        } else if (zoomDuration > 0) {
            this.zoomDuration = 0;
            RequiemClient.instance().fxRenderer().onPossessionAck();
        }
    }
}

