package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.entity.projectile.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectile.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectile.BulletProjectile;
import omtteam.openmodularturrets.entity.projectile.DisposableProjectile;
import omtteam.openmodularturrets.entity.projectile.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectile.PotatoProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;

import java.util.List;

/**
 * Projectile entity types (oldmod {@code ModEntities}; potato and disposable projectiles were not registered there).
 */
public final class ModEntities {
    public static final EntityType<BulletProjectile> BULLET = register("bullet", EntityType.Builder.of(BulletProjectile::new, MobCategory.MISC));
    public static final EntityType<BlazingClayProjectile> BLAZING_CLAY = register("blazing_clay", EntityType.Builder.of(BlazingClayProjectile::new, MobCategory.MISC));
    public static final EntityType<GrenadeProjectile> GRENADE = register("grenade", EntityType.Builder.of(GrenadeProjectile::new, MobCategory.MISC));
    public static final EntityType<RocketProjectile> ROCKET = register("rocket", EntityType.Builder.of(RocketProjectile::new, MobCategory.MISC));
    public static final EntityType<ArrowProjectile> ARROW = register("arrow", EntityType.Builder.of(ArrowProjectile::new, MobCategory.MISC));
    public static final EntityType<PotatoProjectile> POTATO = register("potato", EntityType.Builder.of(PotatoProjectile::new, MobCategory.MISC));
    public static final EntityType<DisposableProjectile> DISPOSABLE = register("disposable", EntityType.Builder.of(DisposableProjectile::new, MobCategory.MISC));
    public static final EntityType<PlasmaProjectile> PLASMA = register("plasma", EntityType.Builder.of(PlasmaProjectile::new, MobCategory.MISC));

    /** All projectile types (for renderer registration). */
    public static final List<EntityType<? extends Entity>> PROJECTILES = List.of(BULLET, BLAZING_CLAY, GRENADE, ROCKET,
            ARROW, POTATO, DISPOSABLE, PLASMA);

    private ModEntities() {
    }

    /** oldmod tracking range 64 blocks (4 chunks); small, fast projectiles. */
    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        // noSave: projectiles are never saved (TurretProjectile.shouldBeSaved), so no data fixer is needed
        EntityType<T> type = builder.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).noSave()
                .build(OMT.id(name).toString());
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, OMT.id(name), type);
    }

    /** Loads the class, registering all entity types. */
    public static void init() {
    }
}
