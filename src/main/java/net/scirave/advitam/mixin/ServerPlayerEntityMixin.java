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

package net.scirave.advitam.mixin;

import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.registry.AdVitamPlugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    public abstract ServerWorld getWorld();


    @Shadow
    @Nullable
    public abstract BlockPos getSpawnPointPosition();

    @Shadow
    public abstract RegistryKey<World> getSpawnPointDimension();

    @Inject(method = "setSpawnPoint", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendSystemMessage(Lnet/minecraft/text/Text;)V"))
    public void advitam$alertAnchor(RegistryKey<World> dimension, BlockPos pos, float angle, boolean spawnPointSet, boolean sendMessage, CallbackInfo ci) {
        RemnantComponent component = RemnantComponent.get((ServerPlayerEntity) (Object) this);
        if (component.getRemnantType() == AdVitamPlugin.WRAITH) {
            AnchorComponent anchorComponent = AnchorComponent.get((ServerPlayerEntity) (Object) this);
            anchorComponent.messageAnchorPosition();
            if (anchorComponent.shouldSetAnchor()) {
                anchorComponent.setAnchor(this.getSpawnPointPosition(), this.getSpawnPointDimension());
            }
        }
    }

}
