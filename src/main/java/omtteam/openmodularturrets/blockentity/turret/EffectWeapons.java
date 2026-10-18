package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.registry.ModDamageTypes;

import java.util.List;

/**
 * Turrets that act without aiming (1.12.2 {@code TurretHead} subclasses using {@code updateChecks}): slowing and
 * teleporter act on the base's target ({@link EffectWeapon}), the melee turret strikes everything in reach
 * ({@link AreaWeapon}).
 */
public final class EffectWeapons {
    /** Slowness IV and weakness IV for 10 seconds. */
    private static final int EFFECT_TICKS = 200;
    private static final int EFFECT_AMPLIFIER = 3;
    /** 1.12.2 played these turrets' sounds at volume 0.6 and pitch 1. */
    private static final float SOUND_VOLUME = 0.6F;

    private EffectWeapons() {
    }

    /** Fires an {@link EffectWeapon} at the base's target. Energy and ammo are already consumed. */
    static void fireEffect(TurretBlockEntity turret, TurretBaseBlockEntity base, EffectWeapon weapon, Entity target) {
        if (turret.getLevel() instanceof ServerLevel level) {
            weapon.effect().apply(level, turret, base, target);
            playSound(level, turret.getBlockPos(), weapon.launchSound());
        }
    }

    /** Fires an {@link AreaWeapon} at everything in reach. Energy and ammo are already consumed. */
    static void fireArea(TurretBlockEntity turret, TurretBaseBlockEntity base, AreaWeapon weapon,
                         List<LivingEntity> victims) {
        if (turret.getLevel() instanceof ServerLevel level) {
            weapon.strike().strike(level, turret, base, victims);
            playSound(level, turret.getBlockPos(), weapon.launchSound());
        }
    }

    /** 1.12.2 {@code RelativisticTurretTileEntity}: slowness and weakness IV; slowed entities are not targeted again. */
    public static void slow(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Entity target) {
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
        }
    }

    /**
     * 1.12.2 {@code TeleporterTurretTileEntity}: pulls the target onto the block in front of the turret (1.12.2: on
     * top of it, which is the same for turrets on top of a base) and shows portal particles (1.12.2
     * {@code BlockTeleporterTurret.randomDisplayTick}).
     */
    public static void teleport(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Entity target) {
        Direction facing = turret.getFacing();
        BlockPos destination = turret.getBlockPos().relative(facing);
        target.teleportTo(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D);
        BlockPos pos = turret.getBlockPos();
        level.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                26, 1.0D, 1.0D, 1.0D, 0.1D);
    }

    /**
     * The melee turret's reach: the turret and its base plus one block around them horizontally, 3×2×3 blocks (user
     * decision; the melee turret only mounts on top of or below its base).
     */
    public static AABB meleeArea(TurretBlockEntity turret) {
        return new AABB(turret.getBlockPos()).minmax(new AABB(turret.getBasePos())).inflate(1.0D, 0.0D, 1.0D);
    }

    /**
     * 1.12.2 {@code MeleeTurretTileEntity}: damages every damageable living entity in reach with the turret's base
     * damage. The melee turret needs no target from the base; it strikes whenever something is in {@link #meleeArea}
     * (user decision; 1.12.2 struck a 7×3×7 area once the base had a target). No damage amp bonus, as in 1.12.2.
     */
    public static void strike(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base,
                              List<LivingEntity> victims) {
        float damage = OMTConfig.get().turrets.get(turret.getTurretType()).baseDamage;
        int looting = base.getLogic().getLootingLevel(VersionCompat.maxLootingLevel(level));
        for (LivingEntity entity : victims) {
            TurretDamage.hurt(level, entity, ModDamageTypes.MELEE, damage, null, base.getBlockPos(), looting);
        }
    }

    private static void playSound(ServerLevel level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, SOUND_VOLUME, 1.0F);
    }
}
