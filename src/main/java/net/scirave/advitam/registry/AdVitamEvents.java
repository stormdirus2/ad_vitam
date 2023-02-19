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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.quiltmc.qsl.base.api.event.Event;
import org.quiltmc.qsl.base.api.event.EventAwareListener;

public final class AdVitamEvents {
    public static final Event<CanReanimate> CAN_REANIMATE = Event.create(CanReanimate.class, callbacks -> player -> {
        for (CanReanimate callback : callbacks) {
            Allow state = callback.canReanimate(player);
            if (state != Allow.PASS) return state;
        }

        return Allow.DEFAULT;
    });
    public static final Event<AttemptReanimation> ATTEMPT_REANIMATION = Event.create(AttemptReanimation.class, callbacks -> player -> {
        for (AttemptReanimation callback : callbacks) {
            ReanimationAction action = callback.reanimate(player);
            if (action != ReanimationAction.PASS) return action;
        }

        return ReanimationAction.DEFAULT;
    });
    public static final Event<InteractAnchor> INTERACT_ANCHOR = Event.create(InteractAnchor.class, callbacks -> player -> {
        for (InteractAnchor callback : callbacks) {
            ActionResult state = callback.interact(player);
            if (state != ActionResult.PASS) return state;
        }

        return ActionResult.PASS;
    });

    public enum ReanimationAction {
        PASS,
        CANCEL,
        DEFAULT,
        HANDLED,
        REGENERATE
    }

    public enum Allow {
        PASS,
        DEFAULT,
        DENY,
        ALLOW
    }

    @FunctionalInterface
    public interface CanReanimate extends EventAwareListener {
        /**
         * Called to decide if a Player can attempt Reanimation
         *
         * @param player the player
         */
        Allow canReanimate(PlayerEntity player);

    }

    @FunctionalInterface
    public interface AttemptReanimation extends EventAwareListener {
        /**
         * Called when a player attempts to reanimate
         *
         * @param player the player
         */
        ReanimationAction reanimate(PlayerEntity player);
    }

    @FunctionalInterface
    public interface InteractAnchor extends EventAwareListener {
        /**
         * Called when a Player interacts with their own anchor
         */
        ActionResult interact(PlayerEntity player);

    }

}
