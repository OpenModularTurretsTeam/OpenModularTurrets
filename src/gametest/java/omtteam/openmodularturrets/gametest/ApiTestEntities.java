package omtteam.openmodularturrets.gametest;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.Objects;

/**
 * Entity types of the game test mod, registered by its own initializer like another mod would (the turret API leaves
 * projectile entity types to the mod that adds the turret).
 */
public final class ApiTestEntities implements ModInitializer {
    public static final EntityType<ApiTestProjectile> PROJECTILE = register("api_projectile",
            EntityType.Builder.of(ApiTestProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).noSave());

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceLocation id = Objects.requireNonNull(ResourceLocation.tryParse(ApiTestTurrets.NAMESPACE + ":" + name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id.toString()));
    }

    /** Loads the class, registering the entity types. */
    @Override
    public void onInitialize() {
    }
}
