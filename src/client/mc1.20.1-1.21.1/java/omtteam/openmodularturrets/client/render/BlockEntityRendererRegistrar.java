package omtteam.openmodularturrets.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registers a block entity renderer through the loader: Fabric passes {@code BlockEntityRenderers::register},
 * NeoForge {@code EntityRenderersEvent.RegisterRenderers::registerBlockEntityRenderer}. A generic method, so it takes
 * method references, not lambdas.
 */
public interface BlockEntityRendererRegistrar {
    <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
}
