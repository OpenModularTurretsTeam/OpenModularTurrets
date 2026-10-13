package omtteam.openmodularturrets.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.network.ClientPacketHandlers;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.blockentity.CamoTrapRenderer;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.item.LeverItemRenderer;
import omtteam.openmodularturrets.client.render.item.TurretItemRenderer;
import omtteam.openmodularturrets.client.render.entity.ProjectileRenderers;
import omtteam.openmodularturrets.client.screen.GeneratorScreen;
import omtteam.openmodularturrets.client.screen.TurretBaseScreen;
import omtteam.openmodularturrets.client.tooltip.AmmoTooltip;
import omtteam.openmodularturrets.client.tooltip.GeneratorTooltip;
import omtteam.openmodularturrets.client.tooltip.LeverTooltip;
import omtteam.openmodularturrets.client.tooltip.ModuleTooltip;
import omtteam.openmodularturrets.client.tooltip.TurretBaseTooltip;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModMenus;

/**
 * Client entrypoint of Open Modular Turrets (renderers, screens, client networking).
 */
public class OMTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPacketHandlers.init();
        MenuScreens.register(ModMenus.TURRET_BASE, TurretBaseScreen::new);
        MenuScreens.register(ModMenus.GENERATOR, GeneratorScreen::new);
        TurretModels.registerLayers();
        RayRenderer.init();
        BlockEntityRendererRegistry.register(ModBlockEntities.TURRET, TurretRenderer::new);
        ModelLayerRegistry.registerModelLayer(LeverRenderer.LAYER, LeverRenderer::createLayer);
        BlockEntityRendererRegistry.register(ModBlockEntities.LEVER, LeverRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.CAMO_TRAP, CamoTrapRenderer::new);
        // 3D item models, referenced from assets/openmodularturrets/items/*.json
        SpecialModelRenderers.ID_MAPPER.put(OMT.id("turret"), TurretItemRenderer.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(OMT.id("lever"), LeverItemRenderer.Unbaked.MAP_CODEC);
        ProjectileRenderers.register();
        ClientCompat.registerTooltip(TurretBaseTooltip::append);
        ClientCompat.registerTooltip(AmmoTooltip::append);
        ClientCompat.registerTooltip(ModuleTooltip::append);
        ClientCompat.registerTooltip(LeverTooltip::append);
        ClientCompat.registerTooltip(GeneratorTooltip::append);
    }
}
