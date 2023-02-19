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

import ladysnake.requiem.common.VanillaRequiemPlugin;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.ActionResult;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.registry.AdVitamEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaRequiemPlugin.class)
public class VanillaRequiemPluginMixin {

    @Inject(method = "registerEtherealEventHandlers", at = @At("HEAD"), remap = false)
    public void advitam$PreventRequiemAuthority(CallbackInfo ci) {
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
            if (world.getRegistryKey().equals(AnchorComponent.getAnchorDimension(player)) && blockHitResult.getBlockPos().equals(AnchorComponent.getAnchor(player))) {
                return AdVitamEvents.INTERACT_ANCHOR.invoker().interact(player);
            }

            return ActionResult.PASS;
        });
    }


}
