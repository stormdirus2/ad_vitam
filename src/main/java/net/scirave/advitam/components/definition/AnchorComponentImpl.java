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

import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.scirave.advitam.AdVitam;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.gameplay.effects.BolsteredStatusEffect;
import net.scirave.advitam.registry.AdVitamEffects;
import net.scirave.advitam.registry.AdVitamPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AnchorComponentImpl implements AnchorComponent {

    private final PlayerEntity player;
    int windUp = 0;
    int windUp2 = 0;
    private BlockPos anchor = null;
    private RegistryKey<World> dimension = null;

    public AnchorComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public BlockPos getAnchor() {
        return anchor;
    }

    @Override
    public boolean hasValidAnchor() {
        if (this.anchor != null && this.dimension != null) {
            MinecraftServer server = this.player.getServer();
            if (server != null) {
                ServerWorld potentialWorld = server.getWorld(dimension);
                return potentialWorld != null;
            }

        }

        return false;
    }

    @Override
    public boolean shouldSetAnchor() {
        return !this.hasValidAnchor() || (this.player.isAlive() && !player.isRegionUnloaded() && !player.hasStatusEffect(AdVitamEffects.RUINED) && !RemnantComponent.isIncorporeal(player));
    }

    @Override
    public void setAnchor(BlockPos pos, RegistryKey<World> dimension) {
        this.anchor = pos;
        this.dimension = dimension;
    }

    @Override
    public RegistryKey<World> getAnchorDimension() {
        return dimension;
    }

    @Override
    public boolean isWithinAnchor() {
        if (this.hasValidAnchor()) {
            if (this.player.world.getRegistryKey() == dimension) {
                return this.player.getBlockPos().isWithinDistance(this.anchor, this.getMaxRange());
            }
        }

        return false;
    }

    @Override
    public double getMaxRange() {
        return 32;
    }

    @Override
    public void messageAnchorPosition() {
        if (this.hasValidAnchor()) {
            player.sendSystemMessage(Text.translatable("advitam:spawn_changed", "[" + anchor.toShortString() + "]", "[" + dimension.getValue().getPath().toUpperCase() + "]"));
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtElement possiblePos = tag.get("anchor");
        if (possiblePos instanceof NbtCompound compound) {
            this.anchor = NbtHelper.toBlockPos(compound);
        }

        NbtElement possibleDimension = tag.get("dimension");
        if (possibleDimension != null) {
            Optional<RegistryKey<World>> potentialWorld = World.CODEC.parse(NbtOps.INSTANCE, possibleDimension).result();
            potentialWorld.ifPresentOrElse(worldRegistryKey -> this.dimension = worldRegistryKey, () -> {
                AdVitam.LOGGER.info("FAILED TO DECODE ANCHOR DIMENSION!");
            });
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        if (this.hasValidAnchor()) {
            tag.put("anchor", NbtHelper.fromBlockPos(anchor));
            Optional<NbtElement> potentialWorld = World.CODEC.encodeStart(NbtOps.INSTANCE, dimension).result();
            potentialWorld.ifPresentOrElse(worldRegistryKey -> tag.put("dimension", worldRegistryKey), () -> {
                AdVitam.LOGGER.info("FAILED TO ENCODE ANCHOR DIMENSION!");
            });
        } else if (player instanceof ServerPlayerEntity serverPlayer) {

            BlockPos writeAnchor = serverPlayer.getSpawnPointPosition();
            RegistryKey<World> writeDimension = serverPlayer.getSpawnPointDimension();

            if (writeAnchor == null) {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null) {
                    ServerWorld potentialWorld = server.getWorld(writeDimension);
                    if (potentialWorld != null) {
                        writeAnchor = potentialWorld.getSpawnPos();
                    }
                }
            }

            if (writeAnchor != null) {
                tag.put("anchor", NbtHelper.fromBlockPos(writeAnchor));
            }
            Optional<NbtElement> potentialWorld = World.CODEC.encodeStart(NbtOps.INSTANCE, writeDimension).result();
            potentialWorld.ifPresentOrElse(worldRegistryKey -> tag.put("dimension", worldRegistryKey), () -> {
                AdVitam.LOGGER.info("FAILED TO ENCODE BACKUP ANCHOR DIMENSION!");
            });
        }
    }

    @Override
    public void serverTick() {
        if (player.world == null) return;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (this.shouldSetAnchor()) {
                windUp2++;
                if (windUp2 >= 2) {
                    windUp2 = 0;
                    this.setAnchor(serverPlayer.getSpawnPointPosition(), serverPlayer.getSpawnPointDimension());
                }
            } else {
                windUp2 = 0;
            }

            windUp++;
            if (windUp >= 20) {
                windUp = 0;
                RemnantComponent component = RemnantComponent.get(player);
                if (component.getRemnantType() == AdVitamPlugin.WRAITH && this.isWithinAnchor()) {
                    BolsteredStatusEffect.apply(player);
                }
            }
        }
    }


}
