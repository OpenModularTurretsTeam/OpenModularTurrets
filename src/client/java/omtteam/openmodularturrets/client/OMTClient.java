package omtteam.openmodularturrets.client;

import omtteam.omlib.client.ScreenRegistrar;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.BlockEntityRendererRegistrar;
import omtteam.openmodularturrets.client.render.EntityRendererRegistrar;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.client.render.effect.RayRenderer;
import omtteam.openmodularturrets.client.render.entity.PlasmaRenderer;
import omtteam.openmodularturrets.client.render.entity.ProjectileRenderers;
import omtteam.omlib.client.screen.CamoScreenExtras;
import omtteam.openmodularturrets.client.screen.GeneratorScreen;
import omtteam.openmodularturrets.client.screen.TurretBaseScreen;
import omtteam.openmodularturrets.client.screen.TrapCamoExtras;
import omtteam.openmodularturrets.client.tooltip.AmmoTooltip;
import omtteam.openmodularturrets.client.tooltip.GeneratorTooltip;
import omtteam.openmodularturrets.client.tooltip.InventoryExpanderTooltip;
import omtteam.openmodularturrets.client.tooltip.LeverTooltip;
import omtteam.openmodularturrets.client.tooltip.MemoryCardTooltip;
import omtteam.openmodularturrets.client.tooltip.ModuleTooltip;
import omtteam.openmodularturrets.client.tooltip.OwnershipWandImmunityTooltip;
import omtteam.omlib.client.tooltip.OwnershipWandTooltip;
import omtteam.openmodularturrets.client.tooltip.TurretBaseTooltip;
import omtteam.openmodularturrets.client.tooltip.TurretTooltip;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.ModMenus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
//? if >=1.21.11 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.render.item.LeverSpecialRenderer;
import omtteam.openmodularturrets.client.render.item.TurretSpecialRenderer;
*///?} else {
import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.client.render.item.ItemStackRenderer;
import omtteam.openmodularturrets.client.render.item.LeverItemRenderer;
import omtteam.openmodularturrets.client.render.item.TurretItemRenderer;
//?}

/**
 * Shared client start-up, the client counterpart of {@code OMT}. The loader client entrypoints
 * ({@code client/platform/<loader>/}) install the client platform services, call {@link #init} and register the
 * content listed here with their loader (screens, model layers, renderers, item renderers, render layers, tooltips).
 */
public final class OMTClient {
    @FunctionalInterface
    private interface TooltipAppender {
        void append(ItemStack stack, TooltipFlag flag, List<Component> lines);
    }

    private static final List<TooltipAppender> TOOLTIPS = List.of(TurretBaseTooltip::append, TurretTooltip::append,
            AmmoTooltip::append, ModuleTooltip::append, LeverTooltip::append, GeneratorTooltip::append,
            InventoryExpanderTooltip::append, MemoryCardTooltip::append, OwnershipWandTooltip::append,
            OwnershipWandImmunityTooltip::append);

    private OMTClient() {
    }

    /**
     * Packet handlers and the turret models; called first thing after the client platform services are installed.
     *
     * @param plugins other mods' client turret plugins, as the loader found them
     */
    public static void init(List<TurretModels.Plugin> plugins) {
        CamoScreenExtras.register(TrapCamoExtras::addWidgets);
        RayRenderer.init();
        TurretModels.init(plugins);
    }

    /**
     * All menu screens of the mod, through the loader's registration.
     */
    public static void registerScreens(ScreenRegistrar registrar) {
        registrar.register(ModMenus.TURRET_BASE, TurretBaseScreen::new);
        registrar.register(ModMenus.GENERATOR, GeneratorScreen::new);
    }

    /**
     * All model layers: the turret models (after {@link #init}), the crank and the plasma shot.
     */
    public static void registerModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
        TurretModels.registerLayers(registrar);
        registrar.accept(LeverRenderer.LAYER, LeverRenderer::createLayer);
        registrar.accept(PlasmaRenderer.LAYER, PlasmaRenderer::createLayer);
    }

    public static void registerBlockEntityRenderers(BlockEntityRendererRegistrar registrar) {
        registrar.register(ModBlockEntities.TURRET, TurretRenderer::new);
        registrar.register(ModBlockEntities.LEVER, LeverRenderer::new);
    }

    public static void registerEntityRenderers(EntityRendererRegistrar registrar) {
        ProjectileRenderers.register(registrar);
    }

    //? if >=1.21.11 {
    /*/^**
     * The types of the special item models of the turret and crank items ({@code minecraft:special} with
     * {@code "type": "openmodularturrets:turret"} / {@code "openmodularturrets:lever"} in their item model
     * definitions), through the loader's registration.
     ^/
    public static void registerSpecialItemModels(
            BiConsumer<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> registrar) {
        registrar.accept(OMT.id("turret"), TurretSpecialRenderer.Unbaked.CODEC);
        registrar.accept(OMT.id("lever"), LeverSpecialRenderer.Unbaked.CODEC);
    }
    *///?} else {
    /**
     * Items drawn by a renderer instead of a baked model (their item models have {@code parent: builtin/entity}).
     */
    public static void registerItemRenderers(BiConsumer<Item, ItemStackRenderer> registrar) {
        registrar.accept(ModItems.LEVER, new LeverItemRenderer());
        TurretItemRenderer.register(ModItems.TURRETS, registrar);
    }
    //?}

    /**
     * Blocks drawn in the cutout layer (OMPD fences have transparent parts). Fabric registers them; NeoForge reads
     * {@code "render_type"} in their block models, which must stay in sync with this list.
     */
    public static List<? extends Block> cutoutBlocks() {
        return ModBlocks.FENCES;
    }

    /**
     * Lines added to item tooltips (called for every item).
     */
    public static void appendTooltip(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        for (TooltipAppender appender : TOOLTIPS) {
            appender.append(stack, flag, lines);
        }
    }

    /**
     * After leaving a server: back to the local config, and no rays left over.
     */
    public static void onDisconnect() {
        RayRenderer.clear();
    }
}
