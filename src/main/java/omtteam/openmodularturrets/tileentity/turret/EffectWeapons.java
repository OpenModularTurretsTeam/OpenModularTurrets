package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.entity.projectile.NormalDamageSource;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.turret.AreaWeapon;
import omtteam.openmodularturrets.turret.EffectWeapon;

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
    static void fireEffect(TurretTileEntity turret, TurretBase base, EffectWeapon weapon, EntityLivingBase target) {
        if (turret.getWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) turret.getWorld();
            weapon.effect().apply(world, turret, base, target);
            playSound(world, turret.getPos(), weapon.launchSound());
        }
    }

    /** Fires an {@link AreaWeapon} at everything in reach. Energy and ammo are already consumed. */
    static void fireArea(TurretTileEntity turret, TurretBase base, AreaWeapon weapon, List<EntityLivingBase> victims) {
        if (turret.getWorld() instanceof WorldServer) {
            WorldServer world = (WorldServer) turret.getWorld();
            weapon.strike().strike(world, turret, base, victims);
            playSound(world, turret.getPos(), weapon.launchSound());
        }
    }

    /** 1.12.2 {@code RelativisticTurretTileEntity}: slowness and weakness IV; slowed entities are not targeted again. */
    @SuppressWarnings("unused")
    public static void slow(WorldServer world, TurretTileEntity turret, TurretBase base, EntityLivingBase target) {
        target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
        target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, EFFECT_TICKS, EFFECT_AMPLIFIER, false, true));
    }

    /**
     * 1.12.2 {@code TeleporterTurretTileEntity}: pulls the target onto the block in front of the turret (1.12.2: on
     * top of it, which is the same for turrets on top of a base) and shows portal particles (1.12.2
     * {@code BlockTeleporterTurret.randomDisplayTick}, which only worked in single player).
     */
    @SuppressWarnings("unused")
    public static void teleport(WorldServer world, TurretTileEntity turret, TurretBase base, EntityLivingBase target) {
        BlockPos pos = turret.getPos();
        BlockPos destination = pos.offset(turret.getFacing());
        target.setPositionAndUpdate(destination.getX() + 0.5D, destination.getY(), destination.getZ() + 0.5D);
        world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                26, 1.0D, 1.0D, 1.0D, 0.1D);
    }

    /**
     * The melee turret's reach (1.20.1+, user decision): the turret and its base plus one block around them
     * horizontally, 3×2×3 blocks; the melee turret only mounts on top of or below its base.
     */
    public static AxisAlignedBB meleeArea(TurretTileEntity turret) {
        BlockPos pos = turret.getPos();
        BlockPos basePos = pos.offset(turret.getFacing().getOpposite());
        return new AxisAlignedBB(pos).union(new AxisAlignedBB(basePos)).grow(1.0D, 0.0D, 1.0D);
    }

    /**
     * 1.12.2 {@code MeleeTurretTileEntity}: damages every damageable living entity in reach with the turret's base
     * damage. The melee turret needs no target from the base; it strikes whenever something is in
     * {@link #meleeArea} (1.20.1+, user decision; 1.12.2 struck a 7×3×7 area once the base had a target). No damage
     * amp bonus, as in 1.12.2.
     */
    public static void strike(WorldServer world, TurretTileEntity turret, TurretBase base,
                              List<EntityLivingBase> victims) {
        float damage = ConfigLogicSettings.INSTANCE.turret(turret.getCoreType()).baseDamage();
        int fakeDrops = base.getLogic().getLootingLevel(3);
        for (EntityLivingBase entity : victims) {
            entity.attackEntityFrom(new NormalDamageSource("melee", fakeDrops, base, world, false), damage);
        }
    }

    private static void playSound(WorldServer world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, SOUND_VOLUME, 1.0F);
    }
}
