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

package net.scirave.advitam.gameplay.criteria;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerCriteria extends AbstractAdVitamCriteria<PlayerCriteria.Conditions> {

    public PlayerCriteria(Identifier id) {
        super(id);
    }

    public void handle(ServerPlayerEntity player) {
        this.trigger(player, conditions -> conditions.test(player));
    }

    @Override
    public PlayerCriteria.Conditions conditionsFromJson(JsonObject json, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer deserializer) {
        return new PlayerCriteria.Conditions(this.getId(), playerPredicate, EntityPredicate.fromJson(json.get("player")));
    }

    static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate entity;

        public Conditions(Identifier id, EntityPredicate.Extended playerPredicate, EntityPredicate entity) {
            super(id, playerPredicate);
            this.entity = entity;
        }

        public boolean test(ServerPlayerEntity player) {
            return this.entity.test(player, player);
        }
    }

}
