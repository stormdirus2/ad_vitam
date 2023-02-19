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

package net.scirave.advitam.components.definition;

import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.common.entity.effect.AttritionStatusEffect;
import ladysnake.requiem.common.entity.effect.RequiemStatusEffects;
import ladysnake.requiem.core.util.DetectionHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.scirave.advitam.components.declaration.MementoMoriComponent;
import net.scirave.advitam.gameplay.effects.BulwarkStatusEffect;
import net.scirave.advitam.gameplay.effects.MementoStatusEffect;
import net.scirave.advitam.helpers.MobEntityInterface;
import net.scirave.advitam.registry.AdVitamCriteria;
import net.scirave.advitam.registry.AdVitamEffects;
import net.scirave.advitam.registry.AdVitamGamerules;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MementoMoriComponentImpl implements MementoMoriComponent {

    private final PlayerEntity player;
    int lastAttrition = 0;
    int lastMemento = 0;
    int updateTick = 0;
    int aggroTick = 0;
    private Boolean wasVagrant = null;

    public MementoMoriComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    public static void pacifyMob(MobEntity mob) {
        mob.setTarget(null);
        mob.setAttacker(null);
        mob.setAttacking(false);

        if (mob instanceof Angerable angerable) {
            angerable.stopAnger();
        }

        mob.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        mob.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE);
        mob.getBrain().forget(MemoryModuleType.ANGRY_AT);

        ((MobEntityInterface) mob).resetTargetingDeferred(); //Just in case that weird CME comes back .-.
    }

    public static boolean isAngryAt(MobEntity aggressor, LivingEntity target) {
        return aggressor.getTarget() == target || aggressor.getAttacker() == target ||
                (aggressor instanceof Angerable angerable && angerable.getAngryAt() == target.getUuid());
    }

    @Override
    public boolean shouldNotHeal() {
        return player.hasStatusEffect(AdVitamEffects.RUINED);
    }

    private boolean shouldKickOut() {
        int cap = AdVitamGamerules.getGamerule(AdVitamGamerules.HOST_REBELLION_ATTRITION, player).get();
        return cap >= 0 && (this.lastAttrition >= cap || this.getAttrition() >= cap);
    }

    @Override
    public boolean attemptKickOut(MobEntity host) {
        if (shouldKickOut()) {
            PossessionComponent component = PossessionComponent.get(player);
            component.stopPossessing(true);
            host.clearStatusEffects();
            host.setHealth(host.getMaxHealth());
            BulwarkStatusEffect.applyInfinite(host);

            pacifyMob(host);

            host.getWorld().getEntitiesByClass(MobEntity.class, host.getBoundingBox().expand(64), (mob) -> isAngryAt(mob, host) || isAngryAt(mob, player)).forEach(MementoMoriComponentImpl::pacifyMob);

            if (!host.isCustomNameVisible()) {
                host.setCustomName(Text.literal(player.getGameProfile().getName()).formatted(Formatting.ITALIC, Formatting.STRIKETHROUGH, Formatting.DARK_GRAY));
                host.setCustomNameVisible(true);
            }

            AdVitamCriteria.HOST_REBELLED.handle((ServerPlayerEntity) player, host);

            return true;
        }

        return false;
    }

    public int getAttrition() {
        StatusEffectInstance instance = player.getStatusEffect(RequiemStatusEffects.ATTRITION);
        if (instance != null) {
            return instance.getAmplifier() + 1;
        }

        return 0;
    }

    public int getMemento() {
        StatusEffectInstance instance = player.getStatusEffect(AdVitamEffects.MEMENTO);
        if (instance != null) {
            return instance.getAmplifier() + 1;
        }

        return 0;
    }

    private void updateEffects() {
        RemnantComponent remnant = RemnantComponent.get(player);
        if (wasVagrant == null) {
            wasVagrant = remnant.isVagrant();
        }
        if (remnant.isVagrant()) {
            if (!wasVagrant) {
                updateTick = 0;
                wasVagrant = true;
                if (this.lastMemento > 0) {
                    AdVitamCriteria.MEMENTO_CONVERTED.handle((ServerPlayerEntity) player);
                    AttritionStatusEffect.apply(player, this.lastMemento);
                    player.removeStatusEffect(AdVitamEffects.MEMENTO);
                }
                lastMemento = 0;
            } else {
                int attrition = getAttrition();
                if (attrition != lastAttrition) {
                    updateTick++;
                    if (updateTick >= 2) {
                        lastAttrition = attrition;
                        updateTick = 0;
                    }
                } else {
                    updateTick = 0;
                }
            }
        } else {
            if (wasVagrant) {
                updateTick = 0;
                wasVagrant = false;
                if (this.lastAttrition > 0) {
                    MementoStatusEffect.apply(player,
                            MathHelper.floor(this.lastAttrition * AdVitamGamerules.getGamerule(AdVitamGamerules.MEMENTO_CONVERSION_RATE, player).get()) - 1);
                    player.removeStatusEffect(RequiemStatusEffects.ATTRITION);
                }
                lastAttrition = 0;
            } else {
                int memento = getMemento();
                if (memento != lastMemento) {
                    updateTick++;
                    if (updateTick >= 2) {
                        lastMemento = memento;
                        updateTick = 0;
                    }
                } else {
                    updateTick = 0;
                }
            }
        }
    }

    private void updateAggro() {
        PossessionComponent possession = PossessionComponent.get(player);
        MobEntity host = possession.getHost();
        if (host != null && DetectionHelper.canBeDetected(host)) {
            aggroTick++;
            if (aggroTick >= 20) {
                aggroTick = 0;

                double maxRange = AdVitamGamerules.getGamerule(AdVitamGamerules.PASSIVE_DETECTION_MAX_RANGE, player).get();
                double safeRange = maxRange * AdVitamGamerules.getGamerule(AdVitamGamerules.PASSIVE_DETECTION_SAFETY_MULTIPLIER, player).get();
                if (maxRange == 0) return;

                boolean noAggro = player.getWorld().getEntitiesByClass(MobEntity.class, Box.from(host.getPos()).expand(safeRange), (mob) -> mob != host && DetectionHelper.isValidEnemy(mob) && mob.getTarget() == host).isEmpty();
                if (noAggro) {
                    List<MobEntity> canAggro = player.getWorld().getEntitiesByClass(MobEntity.class, Box.from(host.getPos()).expand(maxRange), (mob) -> mob != host && DetectionHelper.isValidEnemy(mob));

                    MobEntity toAggro = null;
                    double lastDistance = 0;
                    for (MobEntity possibleAggro : canAggro) {
                        double distance = possibleAggro.squaredDistanceTo(host);
                        if (toAggro == null) {
                            toAggro = possibleAggro;
                            lastDistance = distance;
                        }

                        if (distance < lastDistance) {
                            lastDistance = distance;
                            toAggro = possibleAggro;
                        }
                    }

                    if (toAggro != null) {
                        DetectionHelper.inciteMob(host, toAggro);
                    }
                }
            }
        }
    }

    @Override
    public void serverTick() {
        if (player.world == null) return;
        updateEffects();
        updateAggro();
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        this.lastAttrition = tag.getInt("attrition");
        this.lastMemento = tag.getInt("memento");
        this.wasVagrant = tag.getBoolean("vagrant");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        tag.putInt("attrition", this.lastAttrition);
        tag.putInt("memento", this.lastMemento);
        RemnantComponent remnant = RemnantComponent.get(player);
        tag.putBoolean("vagrant", Objects.requireNonNullElseGet(this.wasVagrant, remnant::isVagrant));
    }
}
