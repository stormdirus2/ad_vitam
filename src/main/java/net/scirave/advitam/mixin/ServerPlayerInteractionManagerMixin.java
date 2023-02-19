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

import ladysnake.requiem.api.v1.possession.PossessionComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow
    protected ServerWorld world;
    @Final
    @Shadow
    protected ServerPlayerEntity player;
    private BlockState lastBlockState;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"))
    private void advitam$captureBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        lastBlockState = world.getBlockState(pos);
    }

    @ModifyVariable(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"), ordinal = 1)
    private boolean advitam$modifyEffectiveTool(boolean original) {
        if (PossessionComponent.get(player).getHost() != null) {
            if (Items.WOODEN_PICKAXE.isSuitableFor(lastBlockState)) {
                return true;
            }
        }
        return original;
    }

}
