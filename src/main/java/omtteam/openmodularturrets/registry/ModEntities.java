package omtteam.openmodularturrets.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import omtteam.openmodularturrets.entity.projectile.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectile.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectile.BulletProjectile;
import omtteam.openmodularturrets.entity.projectile.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectile.RocketProjectile;
import omtteam.openmodularturrets.reference.Reference;

public class ModEntities {
    /**
     * Network ids must be unique within the mod (the client looks the spawned entity up by them; they are not saved in
     * worlds). The disposable and potato projectiles are not registered: they are never sent to clients, the item they
     * carry is a separate {@code EntityItem}.
     */
    public static void registerProjectiles(Object mod) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":rocketProjectile"), RocketProjectile.class, Reference.MOD_ID + ":rocketProjectile", 1, mod, 64, 64, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":grenadeProjectile"), GrenadeProjectile.class, Reference.MOD_ID + ":grenadeProjectile", 2, mod, 64, 64, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":bulletProjectile"), BulletProjectile.class, Reference.MOD_ID + ":bulletProjectile", 3, mod, 64, 64, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":blazingClayProjectile"), BlazingClayProjectile.class, Reference.MOD_ID + ":blazingClayProjectile", 5, mod, 64, 64, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":plasmaProjectile"), PlasmaProjectile.class, Reference.MOD_ID + ":plasmaProjectile", 6, mod, 64, 64, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":arrowProjectile"), ArrowProjectile.class, Reference.MOD_ID + ":arrowProjectile", 7, mod, 64, 64, true);
    }
}
