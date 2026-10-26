package omtteam.openmodularturrets.client.platform.neoforge;

import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//? if >=1.21.11 {
/*import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
*///?} else {
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
//?}
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.OMTClient;
import omtteam.openmodularturrets.client.api.OMTClientPlugin;
import omtteam.openmodularturrets.client.camo.CamoModels;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.platform.ClientPlatform;
import omtteam.openmodularturrets.client.platform.ClientPlatformServices;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.outline.TurretPlacementOutline;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.neoforge.NeoForgeNetwork;
import omtteam.openmodularturrets.platform.neoforge.NeoForgePlugins;
//? if >=1.21.11 {
/*import omtteam.openmodularturrets.camo.CamoBlock;
*///?}

/**
 * NeoForge client entrypoint: installs the client platform services, starts the client, registers the content of
 * {@link OMTClient} in NeoForge's events and wires NeoForge's client events to the shared code. FML constructs it after
 * the common {@code OMTNeoForge} (entrypoints for more sides first), so {@link Platform} and the turret types are
 * already there. Render types come from "render_type" in the block models ({@link OMTClient#cutoutBlocks}).
 */
@Mod(value = OMT.MOD_ID, dist = Dist.CLIENT)
public final class OMTNeoForgeClient {
    public OMTNeoForgeClient(IEventBus modBus) {
        ClientPlatform.install(new ClientPlatformServices(new NeoForgeClientNetwork((NeoForgeNetwork) Platform.network())));
        OMTClient.init(NeoForgePlugins.find(OMTClientPlugin.Register.class, OMTClientPlugin.class, TurretModels.Plugin::new));
        modBus.addListener((RegisterMenuScreensEvent event) -> OMTClient.registerScreens(event::register));
        modBus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) ->
                OMTClient.registerModelLayers(event::registerLayerDefinition));
        modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
            OMTClient.registerBlockEntityRenderers(event::registerBlockEntityRenderer);
            OMTClient.registerEntityRenderers(event::registerEntityRenderer);
        });
        //? if >=1.21.11 {
        /*// the turret and crank items are special item models
        modBus.addListener((RegisterSpecialModelRendererEvent event) -> OMTClient.registerSpecialItemModels(event::register));
        *///?} else {
        modBus.addListener((RegisterClientExtensionsEvent event) -> OMTClient.registerItemRenderers((item, renderer) ->
                event.registerItem(NeoForgeItemRenderer.extensions(renderer), item)));
        //?}
        // the disguise model around the models of the camouflage hosts (NeoForgeCamoModel)
        //? if >=1.21.11 {
        /*modBus.addListener((ModelEvent.ModifyBakingResult event) -> event.getBakingResult().blockStateModels()
                .replaceAll((state, model) -> state.getBlock() instanceof CamoBlock ? new NeoForgeCamoModel(model) : model));
        *///?} else {
        modBus.addListener((ModelEvent.ModifyBakingResult event) -> event.getModels().replaceAll((id, model) ->
                CamoModels.isCamoModel(id.id()) ? new NeoForgeCamoModel(model) : model));
        //?}
        modBus.addListener((RegisterColorHandlersEvent.Block event) ->
                event.register(NeoForgeCamoModel::color, CamoModels.camoBlocks().toArray(Block[]::new)));
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) ->
                OMTClient.appendTooltip(event.getItemStack(), event.getFlags(), event.getToolTip()));
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> OMTClient.onDisconnect());
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> RayRenderer.tick());
        //? if >=1.21.11 {
        /*NeoForge.EVENT_BUS.addListener(OMTNeoForgeClient::onAfterTranslucentBlocks);
        NeoForge.EVENT_BUS.addListener(OMTNeoForgeClient::onAfterParticles);
        *///?} else {
        NeoForge.EVENT_BUS.addListener(OMTNeoForgeClient::onRenderLevelStage);
        //?}
    }

    //? if >=1.21.11 {
    /*private static void onAfterTranslucentBlocks(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
        RayRenderer.render(event.getPoseStack(), camera);
        TurretPlacementOutline.render(event.getPoseStack(), camera);
    }

    // AfterLevel comes from GameRenderer after the level, without a pose stack
    private static void onAfterParticles(RenderLevelStageEvent.AfterParticles event) {
        TurretPlacementOutline.renderLabel(event.getPoseStack(), event.getLevelRenderState().cameraRenderState.pos);
    }
    *///?} else {
    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            RayRenderer.render(event.getPoseStack(), event.getCamera().getPosition());
            TurretPlacementOutline.render(event.getPoseStack(), event.getCamera().getPosition());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            // AFTER_LEVEL comes from GameRenderer after the level, without a pose stack
            TurretPlacementOutline.renderLabel(event.getPoseStack(), event.getCamera().getPosition());
        }
    }
    //?}
}
