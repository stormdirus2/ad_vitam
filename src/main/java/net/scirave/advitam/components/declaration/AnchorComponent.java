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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.scirave.advitam.AdVitam;
import org.jetbrains.annotations.Contract;

public interface AnchorComponent extends AutoSyncedComponent, ServerTickingComponent {
    ComponentKey<AnchorComponent> KEY = ComponentRegistry.getOrCreate(new Identifier(AdVitam.MOD_ID, "anchor"), AnchorComponent.class);

    @Contract(pure = true)
    static AnchorComponent get(PlayerEntity player) {
        return KEY.get(player);
    }

    static BlockPos getAnchor(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.getAnchor();
        }

        return null;
    }

    static void messageAnchorPosition(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            r.messageAnchorPosition();
        }
    }

    static RegistryKey<World> getAnchorDimension(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.getAnchorDimension();
        }

        return null;
    }

    static boolean hasValidAnchor(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.hasValidAnchor();
        }

        return false;
    }

    static boolean shouldSetAnchor(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.shouldSetAnchor();
        }

        return false;
    }

    static void setAnchor(Entity entity, BlockPos pos, RegistryKey<World> world) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            r.setAnchor(pos, world);
        }
    }

    static boolean isWithinAnchor(Entity entity) {
        AnchorComponent r = KEY.getNullable(entity);
        if (r != null) {
            return r.isWithinAnchor();
        }

        return false;
    }

    BlockPos getAnchor();

    boolean hasValidAnchor();

    boolean shouldSetAnchor();

    void setAnchor(BlockPos pos, RegistryKey<World> dimension);

    RegistryKey<World> getAnchorDimension();

    boolean isWithinAnchor();

    double getMaxRange();

    void messageAnchorPosition();

}
