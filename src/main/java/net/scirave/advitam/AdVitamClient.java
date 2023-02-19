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

package net.scirave.advitam;

import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.requiem.api.v1.event.minecraft.client.CrosshairRenderCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionStateChangeCallback;
import ladysnake.requiem.api.v1.event.requiem.RemnantStateChangeCallback;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.client.FractureKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.scirave.advitam.components.declaration.AnchorComponent;
import net.scirave.advitam.registry.AdVitamFx;
import net.scirave.advitam.registry.AdVitamPlugin;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class AdVitamClient implements ClientModInitializer {

    public static AdVitamFx FX = new AdVitamFx();
    public MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawCrosshairIcon(MatrixStack matrices, int scaledWidth, int scaledHeight, Identifier abilityIcon, float progress) {
        int x = (scaledWidth - 32) / 2 + 8;
        int y = (scaledHeight - 16) / 2 + 16;
        RenderSystem.setShaderTexture(0, abilityIcon);
        int height = (int) (progress * 8.0F);
        DrawableHelper.drawTexture(matrices, x, y, 16, 8, 0, 0, 16, 8, 16, 16);
        DrawableHelper.drawTexture(matrices, x, y + 8 - height, 16, height, 0, 16 - height, 16, height, 16, 16);
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
    }

    public static boolean isLookingAtAnchor() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        ClientPlayerEntity player = client.player;
        BlockPos blockPos = AnchorComponent.getAnchor(player);
        if (player != null && interactionManager != null && blockPos != null && client.crosshairTarget instanceof BlockHitResult bhr) {
            Vec3d pos = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            BlockPos otherBlockPos = bhr.getBlockPos();
            Vec3d otherPos = new Vec3d(otherBlockPos.getX(), otherBlockPos.getY(), otherBlockPos.getZ());

            float reach = interactionManager.getReachDistance() * interactionManager.getReachDistance();

            return player.squaredDistanceTo(otherPos) <= reach && pos.equals(otherPos);
        }

        return false;
    }

    private void notifyMaterialize() {
        mc.inGameHud.setOverlayMessage(Text.translatable("advitam:materialize", Text.translatable(FractureKeyBinding.etherealFractureKey.getKeyTranslationKey())), false);
    }

    @Override
    public void onInitializeClient(ModContainer modContainer) {

        RemnantStateChangeCallback.EVENT.register((plr, remnant, type) -> {
            if (remnant.isIncorporeal()) {
                if (plr == mc.player) {
                    notifyMaterialize();
                }
            }
        });


        PossessionStateChangeCallback.EVENT.register((plr, host) -> {
            if (plr == mc.player) {
                if (RemnantComponent.isIncorporeal(plr)) {
                    notifyMaterialize();
                }
            }
        });


        CrosshairRenderCallback.EVENT.register(new Identifier(AdVitam.MOD_ID, "anchor_cursor"), (MatrixStack matrices, int scaledWidth, int scaledHeight) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                RemnantComponent component = RemnantComponent.get(player);
                if (component.isIncorporeal() && component.getRemnantType() != AdVitamPlugin.WRAITH && isLookingAtAnchor()) {
                    drawCrosshairIcon(matrices, scaledWidth, scaledHeight, new Identifier(AdVitam.MOD_ID, "textures/gui/anchor_cursor.png"), 1);
                }
            }
        });

        FX.onInitialize(modContainer);

    }
}
