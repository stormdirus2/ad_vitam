package net.fenrir.advitam.mixin;

import ladysnake.requiem.api.v1.possession.Possessable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpiderEntity.class)
public abstract class SpiderEntityMixin extends LivingEntityMixin {

    @Override
    public DamageSource makeSpidersDropEyes(DamageSource deathCause) {
        Entity attacker = deathCause.getAttacker();
        if (attacker != null) {
            if (attacker instanceof Possessable possessed) {
                PlayerEntity possessor = possessed.getPossessor();
                if (possessor != null) {
                    this.playerHitTimer = 100;
                    this.attackingPlayer = possessor;
                    if (deathCause instanceof ProjectileDamageSource) {
                        return new ProjectileDamageSource(deathCause.getName(), deathCause.getSource(), possessor);
                    } else if (deathCause instanceof EntityDamageSource) {
                        return new EntityDamageSource(deathCause.getName(), possessor);
                    }
                }
            }
        }

        return deathCause;
    }

}
