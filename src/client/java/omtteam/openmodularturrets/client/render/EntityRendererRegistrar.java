package omtteam.openmodularturrets.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Registers an entity renderer through the loader: Fabric passes {@code EntityRendererRegistry::register}, NeoForge
 * {@code EntityRenderersEvent.RegisterRenderers::registerEntityRenderer}. A generic method, so it takes method
 * references, not lambdas.
 */
public interface EntityRendererRegistrar {
    <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider);
}
