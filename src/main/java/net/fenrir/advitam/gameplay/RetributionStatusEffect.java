package net.fenrir.advitam.gameplay;

import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.StickyStatusEffect;
import ladysnake.requiem.common.entity.effect.AttritionStatusEffect;
import ladysnake.requiem.common.entity.effect.RequiemStatusEffects;
import net.fenrir.advitam.AdVitam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class RetributionStatusEffect extends StatusEffect implements StickyStatusEffect {

    public RetributionStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    @Override
    public boolean shouldStick(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean shouldFreezeDuration(LivingEntity entity) {
        return entity instanceof PlayerEntity player && RemnantComponent.get(player).isIncorporeal();
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            if (RemnantComponent.get(player).isIncorporeal()) {
                AttritionStatusEffect.apply(player, amplifier + 1);
            }
        }
        super.onApplied(entity, attributes, amplifier);
    }

    public static void convertTo(ServerPlayerEntity player) {
        StatusEffectInstance attrition = player.getStatusEffect(RequiemStatusEffects.ATTRITION);
        if (attrition != null) {
            player.addStatusEffect(new StatusEffectInstance(
                    AdVitam.RETRIBUTION,
                    12000,
                    attrition.getAmplifier(),
                    false,
                    false,
                    true
            ));
        }
    }

    public static void convertFrom(ServerPlayerEntity player) {
        StatusEffectInstance blight = player.getStatusEffect(AdVitam.RETRIBUTION);
        if (blight != null) {
            AttritionStatusEffect.apply(player, blight.getAmplifier() + 1);
        }
    }

}
