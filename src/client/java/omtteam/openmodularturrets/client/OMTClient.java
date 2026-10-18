package omtteam.openmodularturrets.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.camo.CamoModels;
import omtteam.openmodularturrets.client.network.ClientPacketHandlers;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.item.LeverItemRenderer;
import omtteam.openmodularturrets.client.render.item.TurretItemRenderer;
import omtteam.openmodularturrets.client.render.entity.ProjectileRenderers;
import omtteam.openmodularturrets.client.screen.CamoScreen;
import omtteam.openmodularturrets.client.screen.GeneratorScreen;
import omtteam.openmodularturrets.client.screen.TurretBaseScreen;
import omtteam.openmodularturrets.client.tooltip.AmmoTooltip;
import omtteam.openmodularturrets.client.tooltip.GeneratorTooltip;
import omtteam.openmodularturrets.client.tooltip.LeverTooltip;
import omtteam.openmodularturrets.client.tooltip.MemoryCardTooltip;
import omtteam.openmodularturrets.client.tooltip.ModuleTooltip;
import omtteam.openmodularturrets.client.tooltip.TurretBaseTooltip;
import omtteam.openmodularturrets.client.tooltip.TurretTooltip;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
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
        MenuScreens.register(ModMenus.CAMO, CamoScreen::new);
        CamoModels.register();
        TurretModels.init();
        RayRenderer.init();
        BlockEntityRenderers.register(ModBlockEntities.TURRET, TurretRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(LeverRenderer.LAYER, LeverRenderer::createLayer);
        BlockEntityRenderers.register(ModBlockEntities.LEVER, LeverRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.LEVER, new LeverItemRenderer());
        // OMPD fences have transparent parts
        ModBlocks.FENCES.forEach(fence -> BlockRenderLayerMap.INSTANCE.putBlock(fence, RenderType.cutout()));
        TurretItemRenderer.register(ModItems.TURRETS);
        ProjectileRenderers.register();
        ClientCompat.registerTooltip(TurretBaseTooltip::append);
        ClientCompat.registerTooltip(TurretTooltip::append);
        ClientCompat.registerTooltip(AmmoTooltip::append);
        ClientCompat.registerTooltip(ModuleTooltip::append);
        ClientCompat.registerTooltip(LeverTooltip::append);
        ClientCompat.registerTooltip(GeneratorTooltip::append);
        ClientCompat.registerTooltip(MemoryCardTooltip::append);
    }
}
