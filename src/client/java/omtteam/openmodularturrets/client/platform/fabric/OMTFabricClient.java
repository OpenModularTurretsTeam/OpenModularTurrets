package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import omtteam.openmodularturrets.client.OMTClient;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.platform.ClientPlatform;
import omtteam.openmodularturrets.client.platform.ClientPlatformServices;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.outline.TurretPlacementOutline;

import java.util.List;

/**
 * Fabric client entrypoint: installs the client platform services, starts the client, registers the content of
 * {@link OMTClient} and wires Fabric's client callbacks to the shared code.
 */
public final class OMTFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlatform.install(new ClientPlatformServices(new FabricClientNetwork()));
        OMTClient.init(plugins());
        OMTClient.registerScreens(MenuScreens::register);
        OMTClient.registerModelLayers((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
        OMTClient.registerBlockEntityRenderers(BlockEntityRenderers::register);
        OMTClient.registerEntityRenderers(EntityRendererRegistry::register);
        OMTClient.registerItemRenderers((item, renderer) -> BuiltinItemRendererRegistry.INSTANCE.register(item, renderer::render));
        // NeoForge reads these from "render_type" in the block models; Fabric ignores that field
        OMTClient.cutoutBlocks().forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
        FabricCamoModel.register();
        //? if >=1.21 {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> OMTClient.appendTooltip(stack, flag, lines));
        //?} else {
        /*ItemTooltipCallback.EVENT.register(OMTClient::appendTooltip);
        *///?}
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> OMTClient.onDisconnect());
        ClientTickEvents.END_CLIENT_TICK.register(client -> RayRenderer.tick());
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            RayRenderer.render(context.matrixStack(), context.camera().getPosition());
            TurretPlacementOutline.render(context.matrixStack(), context.camera().getPosition());
        });
        WorldRenderEvents.LAST.register(context ->
                TurretPlacementOutline.renderLabel(context.matrixStack(), context.camera().getPosition()));
    }

    /** Other mods' client turret plugins: the {@value OMTClientPlugin#ENTRYPOINT} entrypoints in their {@code fabric.mod.json}. */
    private static List<TurretModels.Plugin> plugins() {
        return FabricLoader.getInstance().getEntrypointContainers(OMTClientPlugin.ENTRYPOINT, OMTClientPlugin.class).stream()
                .map(container -> new TurretModels.Plugin(container.getProvider().getMetadata().getId(),
                        container.getEntrypoint()))
                .toList();
    }
}
