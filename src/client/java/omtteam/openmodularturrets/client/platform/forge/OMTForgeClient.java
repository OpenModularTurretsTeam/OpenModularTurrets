package omtteam.openmodularturrets.client.platform.forge;

import omtteam.omlib.client.platform.forge.ForgeCamoModel;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import omtteam.openmodularturrets.client.OMTClient;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.omlib.client.camo.CamoModels;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.DisabledModuleOverlay;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.outline.TurretPlacementOutline;
import omtteam.omlib.platform.Platform;
import omtteam.openmodularturrets.platform.forge.ForgePlugins;

/**
 * Forge client entrypoint: installs the client platform services, starts the client, registers the content of
 * {@link OMTClient} in Forge's events and wires Forge's client events to the shared code. Forge 1.20.1 has no
 * client-only {@code @Mod} classes: the common {@code OMTForge} constructs this one by name on a client, after
 * installing {@link Platform} and loading the turret types. Render types come from "render_type" in the block models
 * ({@link OMTClient#cutoutBlocks}).
 */
public final class OMTForgeClient {
    public OMTForgeClient(IEventBus modBus) {
        OMTClient.init(ForgePlugins.find(OMTClientPlugin.Register.class, OMTClientPlugin.class, TurretModels.Plugin::new));
        // no screen or item extension events on Forge 1.20.1: both on the main thread during client setup
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() -> {
            OMTClient.registerScreens(MenuScreens::register);
            OMTClient.registerItemRenderers(ForgeItemRenderer::register);
        }));
        modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) ->
                OMTClient.registerModelLayers(event::registerLayerDefinition));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            OMTClient.registerBlockEntityRenderers(event::registerBlockEntityRenderer);
            OMTClient.registerEntityRenderers(event::registerEntityRenderer);
        });
        modBus.addListener((RegisterItemDecorationsEvent event) -> DisabledModuleOverlay.moduleItems().forEach(item ->
                event.register(item, (graphics, font, stack, x, y) -> {
                    DisabledModuleOverlay.renderItemOverlay(graphics, stack, x, y);
                    return false;
                })));
        modBus.addListener((RegisterColorHandlersEvent.Block event) ->
                event.register(ForgeCamoModel::color, CamoModels.camoBlocks().toArray(Block[]::new)));
        MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent event) ->
                OMTClient.appendTooltip(event.getItemStack(), event.getFlags(), event.getToolTip()));
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> OMTClient.onDisconnect());
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) RayRenderer.tick();
        });
        MinecraftForge.EVENT_BUS.addListener(OMTForgeClient::onRenderLevelStage);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RayRenderer.render(event.getPoseStack(), event.getCamera().getPosition());
            TurretPlacementOutline.render(event.getPoseStack(), event.getCamera().getPosition());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            // the last stage with the level's pose and model-view: AFTER_WEATHER runs with the level pose already on
            // the model-view stack (rotated twice), AFTER_LEVEL with GameRenderer's own pose stack (not rotated)
            TurretPlacementOutline.renderLabel(event.getPoseStack(), event.getCamera().getPosition());
        }
    }
}
