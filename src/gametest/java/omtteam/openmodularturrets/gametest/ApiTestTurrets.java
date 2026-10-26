package omtteam.openmodularturrets.gametest;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import omtteam.openmodularturrets.api.AmmoDefinition;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.api.OMTApi;
import omtteam.openmodularturrets.api.TurretRegistrar;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.SelfTargetingWeapon;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.RayProfile;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModSounds;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Turrets added the way another mod would add them ({@link OMTPlugin} entrypoint in the game test mod), one per
 * weapon kind. Each marks what its custom code got with its own effect:
 * <ul>
 *     <li>ray: on tier 2 bases, fires arrows as its own ammo type, its entity hit handler marks what it hits (luck)</li>
 *     <li>projectile: fires its own {@link ApiTestProjectile}, its hit handler marks what it hits (hunger)</li>
 *     <li>effect: marks the base's target (glowing)</li>
 *     <li>area: damages and marks everything within 2 blocks of the turret (fire resistance)</li>
 *     <li>self-targeting: picks every living entity it may damage around the base and marks it (weakness)</li>
 * </ul>
 * The other turrets use energy only.
 */
@OMTPlugin.Register
public final class ApiTestTurrets implements OMTPlugin {
    public static final String NAMESPACE = OMTGameTestMod.MOD_ID;
    public static final AmmoType ARROW_AMMO = AmmoType.of(NAMESPACE, "arrow");
    public static final TurretType API_RAY_TURRET = TurretType.builder(NAMESPACE, "api_ray_turret")
            .requiredBaseTier(2).ammo(ARROW_AMMO).build();
    public static final TurretStats API_RAY_TURRET_STATS = TurretStats.builder(12, 10, 4, 100).accuracyDeviation(1)
            .build();

    public static final TurretType API_EFFECT_TURRET = TurretType.builder(NAMESPACE, "api_effect_turret").build();
    public static final TurretType API_SELF_TARGETING_TURRET = TurretType.builder(NAMESPACE, "api_self_targeting_turret")
            .build();
    public static final TurretType API_PROJECTILE_TURRET = TurretType.builder(NAMESPACE, "api_projectile_turret").build();
    public static final TurretType API_AREA_TURRET = TurretType.builder(NAMESPACE, "api_area_turret").build();
    /** All turrets of this plugin. */
    public static final List<TurretType> ALL = List.of(API_RAY_TURRET, API_PROJECTILE_TURRET, API_EFFECT_TURRET,
            API_AREA_TURRET, API_SELF_TARGETING_TURRET);
    /** How often the self-targeting turret's picker ran. */
    public static final AtomicInteger SELF_TARGETING_PICKS = new AtomicInteger();
    /** Ticks the marking effects last. */
    private static final int MARK_TICKS = 200;
    /** Default stats of all turrets but the ray turret. */
    public static final TurretStats CUSTOM_LOGIC_STATS = TurretStats.builder(12, 10, 1, 100).accuracyDeviation(0)
            .build();

    @Override
    public void registerTurrets(TurretRegistrar registrar) {
        registrar.registerAmmo(AmmoDefinition.ofItem(ARROW_AMMO, () -> Items.ARROW));
        registrar.registerTurret(new TurretDefinition(API_RAY_TURRET,
                RayWeapon.builder(ModSounds.LASER_LAUNCH, RayProfile.LASER)
                        .color(RayWeapon.argb(0F, 1F, 0F, 0.5F))
                        .damageTypes(ModDamageTypes.LASER, null)
                        .onEntityHit((level, turret, base, hit) -> mark(hit,
                                new MobEffectInstance(MobEffects.LUCK, MARK_TICKS)))
                        .build(),
                API_RAY_TURRET_STATS));
        registrar.registerTurret(new TurretDefinition(API_EFFECT_TURRET,
                new EffectWeapon(ModSounds.SLOWING_LAUNCH, (level, turret, base, target) -> mark(target,
                        new MobEffectInstance(MobEffects.GLOWING, MARK_TICKS))),
                CUSTOM_LOGIC_STATS));
        registrar.registerTurret(new TurretDefinition(API_SELF_TARGETING_TURRET,
                new SelfTargetingWeapon(ModSounds.SLOWING_LAUNCH, true,
                        (level, turret, base) -> {
                            SELF_TARGETING_PICKS.incrementAndGet();
                            AABB area = new AABB(base.getBlockPos())
                                    .inflate(base.getLogic().getRange(turret.getTurretType()));
                            return List.copyOf(level.getEntitiesOfClass(LivingEntity.class, area,
                                    entity -> entity.isAlive() && OMTApi.canDamage(base, entity)));
                        },
                        (level, turret, base, targets) -> targets.forEach(
                                target -> mark(target, new MobEffectInstance(MobEffects.WEAKNESS, MARK_TICKS)))),
                CUSTOM_LOGIC_STATS));
        registrar.registerTurret(new TurretDefinition(API_PROJECTILE_TURRET,
                ProjectileWeapon.of(ModSounds.MACHINE_GUN_LAUNCH,
                                (level, ammo, target) -> VersionCompat.createEntity(ApiTestEntities.PROJECTILE, level))
                        .onHit((level, projectile, base, hit) -> mark(hit,
                                new MobEffectInstance(MobEffects.HUNGER, MARK_TICKS))),
                CUSTOM_LOGIC_STATS));
        registrar.registerTurret(new TurretDefinition(API_AREA_TURRET,
                new AreaWeapon(ModSounds.SLOWING_LAUNCH, turret -> new AABB(turret.getBlockPos()).inflate(2.0D),
                        (level, turret, base, victims) -> victims.forEach(victim -> {
                            OMTApi.hurt(base, victim, ModDamageTypes.MELEE, 1.0F, null);
                            mark(victim, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MARK_TICKS));
                        })),
                CUSTOM_LOGIC_STATS));
    }

    private static void mark(Entity entity, MobEffectInstance effect) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(effect);
        }
    }
}
