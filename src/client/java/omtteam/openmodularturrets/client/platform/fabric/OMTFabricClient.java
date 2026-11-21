package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import omtteam.openmodularturrets.client.OMTClient;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.outline.TurretPlacementOutline;

import java.util.List;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

/**
 * Fabric client entrypoint: installs the client platform services, starts the client, registers the content of
 * {@link OMTClient} and wires Fabric's client callbacks to the shared code.
 */
public final class OMTFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OMTClient.init(plugins());
        OMTClient.registerScreens(MenuScreens::register);
        OMTClient.registerModelLayers((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
        OMTClient.registerBlockEntityRenderers(BlockEntityRenderers::register);
        OMTClient.registerEntityRenderers(EntityRendererRegistry::register);
        // no item renderers: the turret and crank items are special item models, whose types SpecialModelRenderersMixin
        // registers
        // NeoForge reads these from "render_type" in the block models; Fabric ignores that field
        OMTClient.cutoutBlocks().forEach(block -> BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT));
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> OMTClient.appendTooltip(stack, flag, lines));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> OMTClient.onDisconnect());
        ClientTickEvents.END_CLIENT_TICK.register(client -> RayRenderer.tick());
        // the end of the main pass: after the translucent blocks, and late enough for the label to be on top
        WorldRenderEvents.END_MAIN.register(context -> {
            Vec3 camera = context.worldState().cameraRenderState.pos;
            RayRenderer.render(context.matrices(), camera);
            TurretPlacementOutline.render(context.matrices(), camera);
            TurretPlacementOutline.renderLabel(context.matrices(), camera);
        });
    }

    /** Other mods' client turret plugins: the {@value OMTClientPlugin#ENTRYPOINT} entrypoints in their {@code fabric.mod.json}. */
    private static List<TurretModels.Plugin> plugins() {
        return FabricLoader.getInstance().getEntrypointContainers(OMTClientPlugin.ENTRYPOINT, OMTClientPlugin.class).stream()
                .map(container -> new TurretModels.Plugin(container.getProvider().getMetadata().getId(),
                        container.getEntrypoint()))
                .toList();
    }
}
