package omtteam.openmodularturrets.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import omtteam.omlib.api.render.ColorOM;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.DisposableAmmo;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.RayProfile;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectile.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectile.BulletProjectile;
import omtteam.openmodularturrets.entity.projectile.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectile.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectile.PotatoProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.sound.ModSounds;
import omtteam.openmodularturrets.tileentity.turret.EffectWeapons;
import omtteam.openmodularturrets.tileentity.turret.RayWeapons;

import java.util.Arrays;
import java.util.List;

/**
 * The turrets and ammo of this mod, registered through the turret API like the ones of other mods: weapon and ambient
 * particles per turret type (ported from the 3.x turret tile entity, block and item block classes), default stats
 * from the defaults of the config sections.
 */
final class BuiltinTurrets {
    private static final String AMMO = "text.openmodularturrets.turret.ammo.";

    private BuiltinTurrets() {
    }

    private static TurretDefinition turret(TurretType type, TurretWeapon weapon) {
        return new TurretDefinition(type, weapon, ConfigLogicSettings.defaultStats(type));
    }

    private static TurretDefinition.Builder builder(TurretType type, TurretWeapon weapon) {
        return TurretDefinition.builder(type, weapon, ConfigLogicSettings.defaultStats(type));
    }

    /** The ammo of the built-in turrets: the {@code AmmoMetaItem} variants, crossbow bolts, potatoes and disposable ammo. */
    static List<AmmoDefinition> ammo() {
        return Arrays.asList(
                AmmoDefinition.ofItem(AmmoType.BLAZING_CLAY, () -> ModItems.ammoMetaItem, 0),
                AmmoDefinition.ofItem(AmmoType.BULLET, () -> ModItems.ammoMetaItem, 1),
                AmmoDefinition.ofItem(AmmoType.FERRO_SLUG, () -> ModItems.ammoMetaItem, 2),
                AmmoDefinition.ofItem(AmmoType.GRENADE, () -> ModItems.ammoMetaItem, 3),
                AmmoDefinition.ofItem(AmmoType.ROCKET, () -> ModItems.ammoMetaItem, 4),
                new AmmoDefinition(AmmoType.POTATO,
                        stack -> stack.getItem() == Items.POTATO || stack.getItem() == Items.POISONOUS_POTATO, AMMO + "6",
                        () -> new ItemStack(Items.POTATO)),
                AmmoDefinition.ofItem(AmmoType.BOLT, () -> ModItems.ammoBolt, 0),
                new AmmoDefinition(AmmoType.DISPOSABLE, stack -> DisposableAmmo.getDamage(stack) > 0, AMMO + "0",
                        () -> new ItemStack(Blocks.COBBLESTONE)));
    }

    static List<TurretDefinition> definitions() {
        return Arrays.asList(
                turret(TurretType.DISPOSABLE_TURRET,
                        ProjectileWeapon.of(ModSounds.disposableLaunchSound,
                                (world, ammo, target, base) -> new DisposableTurretProjectile(world, ammo, base))),
                turret(TurretType.POTATO_CANNON_TURRET,
                        ProjectileWeapon.of(ModSounds.potatoLaunchSound,
                                (world, ammo, target, base) -> new PotatoProjectile(world, ammo, base))),
                turret(TurretType.CROSSBOW_TURRET,
                        ProjectileWeapon.of(ModSounds.potatoLaunchSound,
                                (world, ammo, target, base) -> new ArrowProjectile(world, ammo, base))),
                turret(TurretType.MELEE_TURRET,
                        new AreaWeapon(ModSounds.slowingLaunchSound, EffectWeapons::meleeArea, EffectWeapons::strike)),
                turret(TurretType.MACHINE_GUN_TURRET,
                        ProjectileWeapon.of(ModSounds.machinegunLaunchSound,
                                (world, ammo, target, base) -> new BulletProjectile(world, ammo, base))),
                turret(TurretType.INCENDIARY_TURRET,
                        ProjectileWeapon.of(ModSounds.incendiaryLaunchSound,
                                (world, ammo, target, base) -> new BlazingClayProjectile(world, ammo, base))),
                turret(TurretType.GRENADE_TURRET,
                        ProjectileWeapon.of(ModSounds.grenadeLaunchSound,
                                (world, ammo, target, base) -> new GrenadeProjectile(world, ammo, base))),
                builder(TurretType.SLOWING_TURRET,
                        new EffectWeapon(ModSounds.slowingLaunchSound, EffectWeapons::slow))
                        .ambientEffect(BlockTurret.dust(0.8F, 0.8F, 0.8F, false)).build(),
                turret(TurretType.ROCKET_TURRET,
                        ProjectileWeapon.of(ModSounds.rocketLaunchSound,
                                (world, ammo, target, base) -> new RocketProjectile(world, target, ammo, base))),
                turret(TurretType.TELEPORTER_TURRET,
                        new EffectWeapon(ModSounds.teleportLaunchSound, EffectWeapons::teleport)),
                // ray colours from 1.12.2 ColorOM(r, g, b, a); the arc lightning is light blue, as on 1.20.1+
                turret(TurretType.ARC_TURRET,
                        RayWeapon.builder(ModSounds.arcLaunchSound, RayProfile.ARC)
                                .color(new ColorOM(0.6F, 0.8F, 1F, 0.6F)).duration(3).lightning().drawFromTip()
                                .build()),
                turret(TurretType.LASER_TURRET,
                        RayWeapon.builder(ModSounds.laserLaunchSound, RayProfile.LASER)
                                .color(new ColorOM(1F, 0.1F, 0F, 0.38F)).duration(5).hitSound(ModSounds.laserHitSound)
                                .build()),
                builder(TurretType.RAILGUN_TURRET,
                        RayWeapon.builder(ModSounds.railgunLaunchSound, RayProfile.RAILGUN)
                                .color(new ColorOM(1F, 0.5F, 0F, 0.2F)).duration(3).hitSound(ModSounds.railGunHitSound)
                                .onBlockHit(RayWeapons::railgunBlockHit).build())
                        .ambientEffect(BlockTurret.dust(0.1F, 0.2F, 0.8F, true)).build(),
                turret(TurretType.PLASMA_TURRET,
                        ProjectileWeapon.of(ModSounds.plasmaLaunchSound,
                                (world, ammo, target, base) -> new PlasmaProjectile(world, base))));
    }
}
