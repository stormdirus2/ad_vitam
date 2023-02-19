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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Shadow
    protected abstract Block asBlock();

    @Inject(at = @At("RETURN"), method = "calcBlockBreakingDelta", cancellable = true)
    private void advitam$modifyBlockBreakSpeed(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        float base = info.getReturnValue();
        BlockState defaultState = asBlock().getDefaultState();
        if (defaultState.isToolRequired() && Items.WOODEN_PICKAXE.isSuitableFor(defaultState) && PossessionComponent.get(player).getHost() != null) {
            ItemStack stack = player.getMainHandStack();
            if (stack != null) {
                Item item = stack.getItem();
                Block block = Block.getBlockFromItem(item);
                if (block != null && block.getDefaultState().isToolRequired()) {
                    info.setReturnValue(base * 12);
                }
            }
        }
    }

}
