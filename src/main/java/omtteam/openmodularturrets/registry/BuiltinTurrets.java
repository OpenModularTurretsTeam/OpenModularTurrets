package omtteam.openmodularturrets.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.TurretRegistrar;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.blockentity.turret.EffectWeapons;
import omtteam.openmodularturrets.blockentity.turret.RayWeapons;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.RayProfile;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.ItemCarryingProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.inventory.AmmoRules;
import org.jetbrains.annotations.Nullable;

import static omtteam.openmodularturrets.api.weapon.RayWeapon.argb;

/**
 * The turrets and ammo of this mod, registered through the same API as those of other mods. Their default stats are
 * the defaults of their config sections ({@link ConfigData.Turrets}).
 */
final class BuiltinTurrets {
    private BuiltinTurrets() {
    }

    static void register(TurretRegistrar registrar) {
        registerAmmo(registrar);
        ConfigData.Turrets defaults = new ConfigData.Turrets();
        for (TurretType type : TurretType.values()) {
            if (type.isBuiltIn()) {
                registrar.registerTurret(new TurretDefinition(type, weapon(type), defaults.get(type).toStats()));
            }
        }
    }

    private static void registerAmmo(TurretRegistrar registrar) {
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.BLAZING_CLAY, () -> ModItems.AMMO_BLAZING_CLAY));
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.BOLT, () -> ModItems.AMMO_BOLT));
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.BULLET, () -> ModItems.AMMO_BULLET));
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.FERRO_SLUG, () -> ModItems.AMMO_FERRO_SLUG));
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.GRENADE, () -> ModItems.AMMO_GRENADE));
        registrar.registerAmmo(AmmoDefinition.ofItem(AmmoType.ROCKET, () -> ModItems.AMMO_ROCKET));
        registrar.registerAmmo(new AmmoDefinition(AmmoType.POTATO,
                stack -> stack.is(Items.POTATO) || stack.is(Items.POISONOUS_POTATO),
                () -> Component.translatable("tooltip.openmodularturrets.turret.ammo.potato"),
                () -> new ItemStack(Items.POTATO)));
        registrar.registerAmmo(new AmmoDefinition(AmmoType.DISPOSABLE,
                stack -> AmmoRules.getDisposableAmmoDamageBonus(stack) > 0,
                () -> Component.translatable("tooltip.openmodularturrets.turret.ammo.disposable"),
                () -> new ItemStack(Items.COBBLESTONE)));
    }

    private static TurretWeapon weapon(TurretType type) {
        if (type == TurretType.DISPOSABLE_TURRET) return ProjectileWeapon.of(ModSounds.DISPOSABLE_LAUNCH,
                (level, ammo, target) -> withItem(VersionCompat.createEntity(ModEntities.DISPOSABLE, level), ammo, Items.COBBLESTONE));
        if (type == TurretType.POTATO_CANNON_TURRET) return ProjectileWeapon.of(ModSounds.POTATO_LAUNCH,
                (level, ammo, target) -> withItem(VersionCompat.createEntity(ModEntities.POTATO, level), ammo, Items.POTATO));
        if (type == TurretType.CROSSBOW_TURRET) return ProjectileWeapon.of(ModSounds.POTATO_LAUNCH,
                (level, ammo, target) -> VersionCompat.createEntity(ModEntities.ARROW, level));
        if (type == TurretType.MACHINE_GUN_TURRET) return ProjectileWeapon.of(ModSounds.MACHINE_GUN_LAUNCH,
                (level, ammo, target) -> VersionCompat.createEntity(ModEntities.BULLET, level));
        if (type == TurretType.INCENDIARY_TURRET) return ProjectileWeapon.of(ModSounds.INCENDIARY_LAUNCH,
                (level, ammo, target) -> VersionCompat.createEntity(ModEntities.BLAZING_CLAY, level));
        if (type == TurretType.GRENADE_TURRET) return ProjectileWeapon.of(ModSounds.GRENADE_LAUNCH,
                (level, ammo, target) -> VersionCompat.createEntity(ModEntities.GRENADE, level));
        if (type == TurretType.ROCKET_TURRET) return ProjectileWeapon.of(ModSounds.ROCKET_LAUNCH,
                (level, ammo, target) -> {
                    RocketProjectile rocket = VersionCompat.createEntity(ModEntities.ROCKET, level);
                    if (rocket != null) rocket.setTarget(target);
                    return rocket;
                });
        if (type == TurretType.PLASMA_TURRET) return ProjectileWeapon.of(ModSounds.PLASMA_LAUNCH,
                (level, ammo, target) -> VersionCompat.createEntity(ModEntities.PLASMA, level));
        if (type == TurretType.MELEE_TURRET) return new AreaWeapon(ModSounds.SLOWING_LAUNCH, EffectWeapons::meleeArea,
                EffectWeapons::strike);
        if (type == TurretType.SLOWING_TURRET) return new EffectWeapon(ModSounds.SLOWING_LAUNCH, EffectWeapons::slow);
        if (type == TurretType.TELEPORTER_TURRET) return new EffectWeapon(ModSounds.TELEPORT_LAUNCH, EffectWeapons::teleport);
        // ray colours from 1.12.2 ColorOM(r, g, b, a); the arc lightning (no 1.12.2 colour) is light blue
        if (type == TurretType.ARC_TURRET) return RayWeapon.builder(ModSounds.ARC_LAUNCH, RayProfile.ARC)
                .color(argb(0.6F, 0.8F, 1F, 0.6F)).duration(3).lightning().drawFromTip()
                .damageTypes(ModDamageTypes.ARC, ModDamageTypes.ARC_ARMOR_BYPASS).build();
        if (type == TurretType.LASER_TURRET) return RayWeapon.builder(ModSounds.LASER_LAUNCH, RayProfile.LASER)
                .color(argb(1F, 0.1F, 0F, 0.38F)).duration(5).hitSound(ModSounds.LASER_HIT)
                .damageTypes(ModDamageTypes.LASER, null).build();
        if (type == TurretType.RAILGUN_TURRET) return RayWeapon.builder(ModSounds.RAIL_GUN_LAUNCH, RayProfile.RAILGUN)
                .color(argb(1F, 0.5F, 0F, 0.2F)).duration(3).hitSound(ModSounds.RAIL_GUN_HIT)
                .damageTypes(null, ModDamageTypes.RAILGUN).onBlockHit(RayWeapons::railgunBlockHit).build();
        throw new IllegalArgumentException("No built-in weapon for " + type);
    }

    /**
     * Shows the fired item on the projectile; without drawn ammo (creative upgrade, ammo disabled in the config)
     * {@code fallback} is shown so the projectile is not invisible.
     */
    private static TurretProjectile withItem(@Nullable ItemCarryingProjectile projectile, @Nullable AmmoDraw ammo,
                                             Item fallback) {
        if (projectile != null) {
            projectile.setItem(ammo != null && ammo.item() instanceof ItemStack stack ? stack : new ItemStack(fallback));
        }
        return projectile;
    }
}
