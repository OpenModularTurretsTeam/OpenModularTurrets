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
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Turrets that act on their target without aiming (oldmod {@code TurretHead} subclasses using {@code updateChecks}):
 * relativistic, teleporter and melee.
 */
public final class EffectWeapons {
    /** oldmod: slowness IV and weakness IV for 10 seconds. */
    private static final int EFFECT_TICKS = 200;
    private static final int EFFECT_AMPLIFIER = 3;
    /** oldmod played these turrets' sounds at volume 0.6 and pitch 1. */
    private static final float SOUND_VOLUME = 0.6F;

    private EffectWeapons() {
    }

    public static void fire(TurretBlockEntity turret, TurretBaseBlockEntity base, Entity target) {
        ServerLevel level = (ServerLevel) turret.getLevel();
        TurretType type = turret.getTurretType();
        switch (type) {
            case RELATIVISTIC_TURRET -> slow(target);
            case TELEPORTER_TURRET -> teleport(level, turret, target);
            case MELEE_TURRET -> strike(level, turret, base);
            default -> throw new IllegalArgumentException(type + " is not an effect turret");
        }
        playSound(level, turret.getBlockPos(), TurretWeapon.of(type).launchSound());
    }

    /** oldmod {@code RelativisticTurretTileEntity}: slowness and weakness IV; slowed entities are not targeted again. */
    private static void slow(Entity target) {
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
        }
    }

    /**
     * oldmod {@code TeleporterTurretTileEntity}: pulls the target onto the block in front of the turret (oldmod: on
     * top of it, which is the same for turrets on top of a base) and shows portal particles (oldmod
     * {@code BlockTeleporterTurret.randomDisplayTick}).
     */
    private static void teleport(ServerLevel level, TurretBlockEntity turret, Entity target) {
        Direction facing = turret.getFacing();
        BlockPos destination = turret.getBlockPos().relative(facing);
        target.teleportTo(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D);
        BlockPos pos = turret.getBlockPos();
        level.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                26, 1.0D, 1.0D, 1.0D, 0.1D);
    }

    /**
     * oldmod {@code MeleeTurretTileEntity}: damages every damageable living entity within 3 blocks horizontally and
     * 1 block vertically of the turret block. oldmod's {@code expand(3, 1, 3)} only grew towards +x/+y/+z; the box is
     * symmetric here (user decision). No damage amp bonus, like oldmod.
     */
    private static void strike(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base) {
        float damage = OMTConfig.get().turrets.get(TurretType.MELEE_TURRET).baseDamage;
        int looting = base.getLogic().getLootingLevel(VersionCompat.maxLootingLevel(level));
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(turret.getBlockPos()).inflate(3, 1, 3))) {
            if (TurretDamage.canDamage(level, base.getBlockPos(), entity)) {
                TurretDamage.hurt(level, entity, ModDamageTypes.MELEE, damage, null, base.getBlockPos(), looting);
            }
        }
    }

    private static void playSound(ServerLevel level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, SOUND_VOLUME, 1.0F);
    }
}
