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

package net.scirave.advitam.components.declaration;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import ladysnake.requiem.api.v1.possession.Possessable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.scirave.advitam.AdVitam;
import org.jetbrains.annotations.Contract;

public interface MementoMoriComponent extends AutoSyncedComponent, ServerTickingComponent {
    ComponentKey<MementoMoriComponent> KEY = ComponentRegistry.getOrCreate(new Identifier(AdVitam.MOD_ID, "mementomori"), MementoMoriComponent.class);

    @Contract(pure = true)
    static MementoMoriComponent get(PlayerEntity player) {
        return KEY.get(player);
    }

    static int getAttrition(Entity entity) {
        MementoMoriComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.getAttrition();
        }

        return 0;
    }

    static int getMemento(Entity entity) {
        MementoMoriComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.getMemento();
        }

        return 0;
    }

    static boolean shouldNotHeal(Entity entity) {
        MementoMoriComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.shouldNotHeal();
        }

        return false;
    }

    static boolean attemptKickOutFrom(LivingEntity entity) {
        if (entity instanceof Possessable possessable && entity instanceof MobEntity mob) {
            PlayerEntity player = possessable.getPossessor();
            if (player != null) {
                MementoMoriComponent r = KEY.getNullable(player);
                if (r != null) {
                    return r.attemptKickOut(mob);
                }
            }
        }

        return false;
    }

    boolean shouldNotHeal();

    boolean attemptKickOut(MobEntity host);

    int getAttrition();

    int getMemento();

}
