package net.fenrir.advitam;

import ladysnake.requiem.api.v1.event.requiem.PossessionStateChangeCallback;
import ladysnake.requiem.api.v1.event.requiem.RemnantStateChangeCallback;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.client.FractureKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class AdVitamClient implements ClientModInitializer {

    public MinecraftClient mc = MinecraftClient.getInstance();

    private void notifyMaterialize() {
        mc.inGameHud.setOverlayMessage(new TranslatableText("advitam:materialize", FractureKeyBinding.etherealFractureKey.getBoundKeyLocalizedText()), false);
    }

    @Override
    public void onInitializeClient() {
        RemnantStateChangeCallback.EVENT.register((plr, remnant) -> {
            if (remnant.isIncorporeal()) {
                if (plr == mc.player) {
                    notifyMaterialize();
                }
            }
        });

        PossessionStateChangeCallback.EVENT.register((plr, host) -> {
            if (plr == mc.player) {
                if (RemnantComponent.get(plr).isIncorporeal()) {
                    notifyMaterialize();
                }
            }
        });
    }
}
