package net.fenrir.advitam;

import ladysnake.requiem.api.v1.event.requiem.InitiateFractureCallback;
import ladysnake.requiem.api.v1.event.requiem.PossessionEvents;
import ladysnake.requiem.api.v1.event.requiem.PossessionStateChangeCallback;
import ladysnake.requiem.api.v1.possession.PossessionComponent;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.api.v1.remnant.SoulbindingRegistry;
import ladysnake.requiem.common.entity.effect.AttritionStatusEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fenrir.advitam.gameplay.RetributionStatusEffect;
import net.fenrir.advitam.helpers.Consumable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AdVitam implements ModInitializer {

    public static final String MOD_ID = "advitam";

    public static final FoodComponent TAINTED_MEAT = new FoodComponent.Builder().alwaysEdible().meat().hunger(1).statusEffect(
            new StatusEffectInstance(
                    StatusEffects.WEAKNESS,
                    600,
                    0), 1).build();

    public static final Item ROASTED_SPIDER_EYE = new Consumable(new Item.Settings().food(TAINTED_MEAT).group(ItemGroup.FOOD));
    public static final Tag<Item> WEAKNESS_GIVING = TagRegistry.item(new Identifier(MOD_ID, "weakness_giving"));

    public static final StatusEffect RETRIBUTION = new RetributionStatusEffect(StatusEffectType.HARMFUL, 0x8b00af);

    public boolean isSuitableBlock(BlockState state) {
        Material material = state.getMaterial();
        return material.blocksMovement() && material.blocksLight() && (!state.isToolRequired() || Items.WOODEN_PICKAXE.isSuitableFor(state));
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "roasted_spider_eye"), ROASTED_SPIDER_EYE);

        Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, "retribution"), RETRIBUTION);

        SoulbindingRegistry.instance().registerSoulbound(RETRIBUTION);

        InitiateFractureCallback.EVENT.register((plr) -> {
            if (plr.world instanceof ServerWorld world) {
                if (RemnantComponent.get(plr).isIncorporeal() && isSuitableBlock(world.getBlockState(plr.getCameraBlockPos())) && isSuitableBlock(world.getBlockState(plr.getCameraBlockPos().down()))) {
                    SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, world);
                    skeleton.setPos(plr.getX(), plr.getY(), plr.getZ());
                    world.spawnEntity(skeleton);
                    PossessionComponent.get(plr).startPossessing(skeleton);
                    AttritionStatusEffect.apply(plr);
                }
            }
            return false;
        });
        PossessionStateChangeCallback.EVENT.register((plr, host) -> {
            if (plr instanceof ServerPlayerEntity player) {
                if (RemnantComponent.KEY.maybeGet(player).map(rc -> !rc.isVagrant() && rc.canRegenerateBody()).orElse(false)) {
                    RetributionStatusEffect.convertTo(player);
                }
            }
        });

        PossessionEvents.HOST_DEATH.register((plr, host, damageSource) -> {
            RetributionStatusEffect.convertFrom(plr);
        });
    }
}
