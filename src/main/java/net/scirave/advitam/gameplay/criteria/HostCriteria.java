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
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class HostCriteria extends AbstractAdVitamCriteria<HostCriteria.Conditions> {

    public HostCriteria(Identifier id) {
        super(id);
    }

    public void handle(ServerPlayerEntity player, Entity body) {
        this.trigger(player, conditions -> conditions.test(player, body));
    }

    @Override
    public HostCriteria.Conditions conditionsFromJson(JsonObject json, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer deserializer) {
        return new HostCriteria.Conditions(this.getId(), playerPredicate, EntityPredicate.fromJson(json.get("body")));
    }

    static class Conditions extends AbstractCriterionConditions {
        private final EntityPredicate entity;

        public Conditions(Identifier id, EntityPredicate.Extended playerPredicate, EntityPredicate entity) {
            super(id, playerPredicate);
            this.entity = entity;
        }

        public boolean test(ServerPlayerEntity player, @NotNull Entity body) {
            return this.entity.test(player, body);
        }
    }

}
