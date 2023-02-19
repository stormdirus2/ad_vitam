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

import ladysnake.requiem.api.v1.RequiemPlugin;
import ladysnake.requiem.api.v1.event.requiem.InitiateFractureCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionEvents;
import ladysnake.requiem.api.v1.event.requiem.PossessionStartCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionStateChangeCallback;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.RemnantState;
import ladysnake.requiem.api.v1.remnant.RemnantType;
import ladysnake.requiem.api.v1.remnant.SoulbindingRegistry;
import ladysnake.requiem.common.entity.effect.AttritionStatusEffect;
import ladysnake.requiem.common.entity.effect.RequiemStatusEffects;
import ladysnake.requiem.common.network.RequiemNetworking;
import ladysnake.requiem.common.remnant.SimpleRemnantType;
import ladysnake.requiem.common.sound.RequiemSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.scirave.advitam.AdVitam;
import net.scirave.advitam.AdVitamClient;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.components.declaration.MementoMoriComponent;
import net.scirave.advitam.gameplay.WraithRemnantState;
import net.scirave.advitam.gameplay.effects.RuinedStatusEffect;
import net.scirave.advitam.gameplay.effects.SecondWindStatusEffect;
import net.scirave.advitam.helpers.MutableRemnantStateInterface;
import net.scirave.advitam.helpers.RemnantComponentInterface;
import org.jetbrains.annotations.NotNull;

public class AdVitamPlugin implements RequiemPlugin {

    public static final RemnantType WRAITH = new SimpleRemnantType(WraithRemnantState::new, true, AdVitam.MOD_ID + ":" + "opus.wraith_sentence", () -> AdVitamItems.ENIGMATIC_SOUL_VESSEL);

    private static boolean isSuitableBlock(BlockState state) {
        Material material = state.getMaterial();
        return material.blocksMovement() && material.blocksLight() && (!state.isToolRequired() || Items.WOODEN_PICKAXE.isSuitableFor(state));
    }

    public static boolean canReanimate(PlayerEntity player) {
        RemnantComponent component = RemnantComponent.get(player);
        if (player.world instanceof ServerWorld world && component.isIncorporeal()) {
            AdVitamEvents.Allow state = AdVitamEvents.CAN_REANIMATE.invoker().canReanimate(player);
            if (state != AdVitamEvents.Allow.DEFAULT) return state == AdVitamEvents.Allow.ALLOW;

            int reanimationCap = AdVitamGamerules.getGamerule(AdVitamGamerules.REANIMATION_ATTRITION_CAP, player).get();

            return !player.hasStatusEffect(RequiemStatusEffects.PENANCE)
                    && (reanimationCap < 0 || MementoMoriComponent.getAttrition(player) < reanimationCap)
                    && isSuitableBlock(world.getBlockState(new BlockPos(player.getEyePos())))
                    && isSuitableBlock(world.getBlockState(new BlockPos(player.getEyePos()).down()));
        }

        return false;
    }

    public static boolean attemptReanimation(PlayerEntity player, RemnantComponentInterface remnantInterface) {
        ServerWorld world = (ServerWorld) player.getWorld();

        AdVitamEvents.ReanimationAction action = AdVitamEvents.ATTEMPT_REANIMATION.invoker().reanimate(player);
        if (action == AdVitamEvents.ReanimationAction.CANCEL) return false;
        if (action == AdVitamEvents.ReanimationAction.HANDLED) return true;

        SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, world);
        skeleton.setPos(player.getX(), player.getY(), player.getZ());
        skeleton.world = player.world;

        if (action == AdVitamEvents.ReanimationAction.DEFAULT) {
            PossessionComponent component = PossessionComponent.get(player);
            world.spawnEntity(skeleton);
            component.startPossessing(skeleton);

            return true;
        } else if (action == AdVitamEvents.ReanimationAction.REGENERATE) {
            RemnantState state = remnantInterface.advitam$getState();
            if (state instanceof MutableRemnantStateInterface mutableInterface) {
                mutableInterface.advitam$regenerateBody(skeleton);

                return true;
            }
        }

        return false;
    }

    public static boolean isPosAlongPathToPos(Entity watcher, Vec3d between, Vec3d end) {
        Vec3d start = watcher.getCameraPosVec(1.0F);
        return Box.from(between).raycast(start, end).isPresent();
    }

    public static void becomeRemnantType(ServerPlayerEntity player, RemnantType type) {
        World world = player.getWorld();
        RemnantComponent component = RemnantComponent.get(player);

        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                RequiemSoundEvents.ITEM_OPUS_USE,
                player.getSoundCategory(), 1.0F, 0.1F
        );
        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                type.isDemon() ? RequiemSoundEvents.EFFECT_BECOME_REMNANT : RequiemSoundEvents.EFFECT_BECOME_MORTAL,
                player.getSoundCategory(), 1.4F, 0.1F
        );

        RequiemNetworking.sendTo(player, RequiemNetworking.createOpusUsePacket(type, true));

        component.become(type, true);
    }


    @Override
    public void onRequiemInitialize() {

        InitiateFractureCallback.EVENT.register((plr) -> {
            if (canReanimate(plr)) {
                RemnantComponent component = RemnantComponent.get(plr);
                int originalAttrition = MementoMoriComponent.getAttrition(plr);
                if (component instanceof RemnantComponentInterface remnantInterface) {
                    if (attemptReanimation(plr, remnantInterface)) {
                        remnantInterface.advitam$setPreventFracture(true);
                        if (component.isVagrant()) {
                            AdVitamCriteria.PLAYER_REANIMATED.handle(plr, PossessionComponent.getHost(plr));
                        }
                        // This is to reduce the initial health of a regenerated Player for balancing reasons
                        int additionalAttrition = 0;
                        if (!component.isVagrant() && component.canRegenerateBody()) {
                            additionalAttrition += originalAttrition;
                        }

                        AttritionStatusEffect.apply(plr, AdVitamGamerules.getGamerule(AdVitamGamerules.REANIMATION_ATTRITION_COST, plr).get() + additionalAttrition);
                        SecondWindStatusEffect.apply(plr);

                        return true;
                    }
                }
            }
            return false;
        });

        PossessionStartCallback.EVENT.register(new Identifier(AdVitam.MOD_ID, "bulwark_prevent"), (host, plr, simulated) -> {
            int softlockLevel = AdVitamGamerules.getGamerule(AdVitamGamerules.ATTRITION_SOFTLOCK, host).get();
            if (host.hasStatusEffect(AdVitamEffects.BULWARK) || (softlockLevel >= 0 && MementoMoriComponent.getAttrition(plr) >= softlockLevel)) {
                return PossessionStartCallback.Result.DENY;
            }

            return PossessionStartCallback.Result.PASS;
        });

        PossessionStateChangeCallback.EVENT.register(new Identifier(AdVitam.MOD_ID, "second_wind_application"), (plr, host) -> {
            if (plr instanceof ServerPlayerEntity && plr.age > 20 && host != null) {
                SecondWindStatusEffect.apply(host);
            }
        });

        PossessionEvents.HOST_DEATH.register(new Identifier(AdVitam.MOD_ID, "remind_anchor"), (plr, host, cause) -> {
            RemnantComponent component = RemnantComponent.get(plr);
            if (component.getRemnantType() == WRAITH) {
                AnchorComponent.messageAnchorPosition(plr);
            }
        });

        AdVitamEvents.ATTEMPT_REANIMATION.register((plr) -> {
            RemnantComponent component = RemnantComponent.get(plr);
            if (component.getRemnantType() == WRAITH && WraithRemnantState.shouldReanimateNewBody(plr)) {
                return AdVitamEvents.ReanimationAction.REGENERATE;
            }

            return AdVitamEvents.ReanimationAction.PASS;
        });

        AdVitamEvents.INTERACT_ANCHOR.register(new Identifier(AdVitam.MOD_ID, "anchor_cursor"), (player) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                RemnantComponent component = RemnantComponent.get(serverPlayer);
                if (component.getRemnantType() != AdVitamPlugin.WRAITH && component.isIncorporeal()) {
                    becomeRemnantType(serverPlayer, WRAITH);
                    RuinedStatusEffect.apply(player);
                    serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0, true, false, false));
                    AdVitamCriteria.ANCHOR_ATTUNEMENT.handle(serverPlayer);

                    return ActionResult.FAIL;
                }
            } else if (RemnantComponent.isIncorporeal(player) && RemnantComponent.get(player).getRemnantType() != WRAITH) {
                AdVitamClient.FX.zoomAnimation(20, player);
                player.world.playSound(player, player.getX(), player.getY(), player.getZ(), RequiemSoundEvents.EFFECT_POSSESSION_ATTEMPT, SoundCategory.PLAYERS, 2, 0.6f);

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

    }

    @Override
    public void registerRemnantStates(@NotNull Registry<RemnantType> registry) {
        Registry.register(registry, new Identifier(AdVitam.MOD_ID, "wraith"), WRAITH);
    }

    @Override
    public void registerSoulBindings(@NotNull SoulbindingRegistry registry) {
        SoulbindingRegistry.instance().registerSoulbound(AdVitamEffects.RUINED);
        SoulbindingRegistry.instance().registerSoulbound(AdVitamEffects.MEMENTO);
        SoulbindingRegistry.instance().registerSoulbound(AdVitamEffects.BOLSTERED);
    }

}
