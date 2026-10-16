package omtteam.openmodularturrets.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import omtteam.openmodularturrets.compat.CommonTags;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.List;
import java.util.function.Consumer;
//? if >=1.21 {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;

import java.util.concurrent.CompletableFuture;
//?} else {
/*import net.minecraft.data.recipes.FinishedRecipe;
*///?}

/**
 * Crafting recipes, ported from the 1.12.2 {@code *_vanilla.json} recipes (ore dictionary entries become {@code c:}
 * tags). The EnderIO/Mekanism variants are not ported.
 */
public class OMTRecipeProvider extends FabricRecipeProvider {
    //? if >=1.21 {
    public OMTRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        generate(builder -> builder.save(output));
    }
    //?} else {
    /*public OMTRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> output) {
        generate(builder -> builder.save(output));
    }
    *///?}

    private void generate(Consumer<ShapedRecipeBuilder> save) {
        ammo(save);
        intermediates(save);
        tieredIntermediates(save);
        turretBases(save);
        turrets(save);
        misc(save);
        passiveDefense(save);
        upgrades(save);
        addons(save);
    }

    /** 1.12.2 {@code intermediate_regular_0_vanilla.json}. */
    private void intermediates(Consumer<ShapedRecipeBuilder> save) {
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IO_BUS)
                .pattern(" G ")
                .pattern("RRR")
                .pattern(" I ")
                .define('G', CommonTags.GOLD_INGOTS)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .define('I', CommonTags.IRON_INGOTS)
                .unlockedBy("has_redstone", has(CommonTags.REDSTONE_DUSTS)));
        // 1.12.2 usable_meta_2_vanilla.json
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEMORY_CARD)
                .pattern("RGR")
                .pattern("IPI")
                .pattern("LBL")
                .define('R', CommonTags.REDSTONE_DUSTS)
                .define('G', CommonTags.GOLD_INGOTS)
                .define('I', CommonTags.IRON_INGOTS)
                .define('P', Items.PAPER)
                .define('L', Items.LAPIS_LAZULI)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
    }

    /**
     * 1.12.2 {@code intermediate_tiered_0..14_vanilla.json}: sensors, chambers and barrels, each tier crafted from
     * the previous one. Ore dictionary {@code cobblestone} and {@code obsidian} have no common tag on 1.20.1, the
     * vanilla items are used on both versions.
     */
    private void tieredIntermediates(Consumer<ShapedRecipeBuilder> save) {
        List<Item> sensors = ModItems.SENSORS;
        List<Item> chambers = ModItems.CHAMBERS;
        List<Item> barrels = ModItems.BARRELS;

        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sensors.get(0))
                .pattern(" R ").pattern("RPR").pattern(" R ")
                .define('R', CommonTags.REDSTONE_DUSTS).define('P', ItemTags.PLANKS)
                .unlockedBy("has_redstone", has(CommonTags.REDSTONE_DUSTS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sensors.get(1))
                .pattern(" I ").pattern("ISI").pattern(" B ")
                .define('I', CommonTags.IRON_INGOTS).define('S', sensors.get(0)).define('B', ModItems.IO_BUS)
                .unlockedBy("has_sensor", has(sensors.get(0))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sensors.get(2))
                .pattern(" B ").pattern("GSG").pattern(" B ")
                .define('G', CommonTags.GOLD_INGOTS).define('S', sensors.get(1)).define('B', ModItems.IO_BUS)
                .unlockedBy("has_sensor", has(sensors.get(1))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sensors.get(3))
                .pattern("GDG").pattern("BSB").pattern("QDQ")
                .define('G', CommonTags.GOLD_INGOTS).define('D', CommonTags.DIAMOND_GEMS).define('S', sensors.get(2))
                .define('B', ModItems.IO_BUS).define('Q', CommonTags.QUARTZ_GEMS)
                .unlockedBy("has_sensor", has(sensors.get(2))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sensors.get(4))
                .pattern("GLG").pattern("BSB").pattern("OLO")
                .define('G', CommonTags.GOLD_INGOTS).define('L', Items.GLOWSTONE_DUST).define('S', sensors.get(3))
                .define('B', ModItems.IO_BUS).define('O', Items.OBSIDIAN)
                .unlockedBy("has_sensor", has(sensors.get(3))));

        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chambers.get(0))
                .pattern("CCC").pattern(" PR").pattern("CCC")
                .define('C', Items.COBBLESTONE).define('P', ItemTags.PLANKS).define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_redstone", has(CommonTags.REDSTONE_DUSTS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chambers.get(1))
                .pattern("III").pattern(" CR").pattern("III")
                .define('I', CommonTags.IRON_INGOTS).define('C', chambers.get(0)).define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_chamber", has(chambers.get(0))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chambers.get(2))
                .pattern("GGG").pattern(" CB").pattern("GGG")
                .define('G', CommonTags.GOLD_INGOTS).define('C', chambers.get(1)).define('B', ModItems.IO_BUS)
                .unlockedBy("has_chamber", has(chambers.get(1))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chambers.get(3))
                .pattern("QDQ").pattern(" CB").pattern("QDQ")
                .define('D', CommonTags.DIAMOND_GEMS).define('C', chambers.get(2)).define('B', ModItems.IO_BUS)
                .define('Q', CommonTags.QUARTZ_GEMS)
                .unlockedBy("has_chamber", has(chambers.get(2))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, chambers.get(4))
                .pattern("OQO").pattern(" CB").pattern("OQO")
                .define('O', Items.OBSIDIAN).define('C', chambers.get(3)).define('B', ModItems.IO_BUS)
                .define('Q', CommonTags.QUARTZ_GEMS)
                .unlockedBy("has_chamber", has(chambers.get(3))));

        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, barrels.get(0))
                .pattern("CCC").pattern(" P ").pattern("CCC")
                .define('C', Items.COBBLESTONE).define('P', ItemTags.PLANKS)
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, barrels.get(1))
                .pattern("III").pattern(" B ").pattern("III")
                .define('I', CommonTags.IRON_INGOTS).define('B', barrels.get(0))
                .unlockedBy("has_barrel", has(barrels.get(0))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, barrels.get(2))
                .pattern("GGG").pattern(" B ").pattern("GGG")
                .define('G', CommonTags.GOLD_INGOTS).define('B', barrels.get(1))
                .unlockedBy("has_barrel", has(barrels.get(1))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, barrels.get(3))
                .pattern("QDQ").pattern(" B ").pattern("QDQ")
                .define('D', CommonTags.DIAMOND_GEMS).define('B', barrels.get(2)).define('Q', CommonTags.QUARTZ_GEMS)
                .unlockedBy("has_barrel", has(barrels.get(2))));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, barrels.get(4))
                .pattern("OOO").pattern("LBL").pattern("OOO")
                .define('O', Items.OBSIDIAN).define('B', barrels.get(3)).define('L', Items.GLOWSTONE_DUST)
                .unlockedBy("has_barrel", has(barrels.get(3))));
    }

    /** 1.12.2 {@code turret_base_0..4_vanilla.json}: each tier upgrades the previous base with a sensor. */
    private void turretBases(Consumer<ShapedRecipeBuilder> save) {
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.TURRET_BASE_TIER_ONE)
                .pattern("CPC").pattern("PSP").pattern("CPC")
                .define('C', Items.COBBLESTONE).define('P', ItemTags.PLANKS).define('S', ModItems.SENSORS.get(0))
                .unlockedBy("has_sensor", has(ModItems.SENSORS.get(0))));
        save.accept(upgradedBase(ModItems.TURRET_BASE_TIER_TWO, ModItems.TURRET_BASE_TIER_ONE, 1)
                .define('M', CommonTags.IRON_INGOTS));
        save.accept(upgradedBase(ModItems.TURRET_BASE_TIER_THREE, ModItems.TURRET_BASE_TIER_TWO, 2)
                .define('M', CommonTags.GOLD_INGOTS));
        save.accept(upgradedBase(ModItems.TURRET_BASE_TIER_FOUR, ModItems.TURRET_BASE_TIER_THREE, 3)
                .define('M', CommonTags.DIAMOND_GEMS));
        save.accept(upgradedBase(ModItems.TURRET_BASE_TIER_FIVE, ModItems.TURRET_BASE_TIER_FOUR, 4)
                .define('M', Items.OBSIDIAN));
    }

    /**
     * 1.12.2 {@code *_turret_vanilla.json}. Plasma, arc, melee and crossbow turrets had no recipe in 1.12.2; theirs
     * are new (user choice), scaled to their base tier. Chambers and barrels are indexed by tier - 1.
     */
    private void turrets(Consumer<ShapedRecipeBuilder> save) {
        List<Item> c = ModItems.CHAMBERS;
        List<Item> b = ModItems.BARRELS;
        save.accept(turret(TurretType.DISPOSABLE_TURRET, " B ", "XCX", "XRX")
                .define('B', b.get(0)).define('C', c.get(0)).define('X', Items.COBBLESTONE).define('R', CommonTags.REDSTONE_DUSTS));
        save.accept(turret(TurretType.POTATO_CANNON_TURRET, "XBX", "XBX", "RCR")
                .define('B', b.get(0)).define('C', c.get(0)).define('X', Items.COBBLESTONE).define('R', CommonTags.REDSTONE_DUSTS));
        save.accept(turret(TurretType.MACHINE_GUN_TURRET, " B ", "XBX", "ICI")
                .define('B', b.get(1)).define('C', c.get(1)).define('X', CommonTags.IRON_INGOTS).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.INCENDIARY_TURRET, "B B", "CXC", "IXI")
                .define('B', b.get(1)).define('C', c.get(1)).define('X', CommonTags.IRON_INGOTS).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.GRENADE_TURRET, " B ", "XCX", "XIX")
                .define('B', b.get(2)).define('C', c.get(2)).define('X', CommonTags.GOLD_INGOTS).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.SLOWING_TURRET, "XPX", "PSP", "XIX")
                .define('P', Items.ENDER_PEARL).define('S', ModItems.SENSORS.get(2)).define('X', CommonTags.GOLD_INGOTS)
                .define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.ROCKET_TURRET, "QBQ", "BCB", "DID")
                .define('B', b.get(3)).define('C', c.get(3)).define('Q', CommonTags.QUARTZ_GEMS)
                .define('D', CommonTags.DIAMOND_GEMS).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.TELEPORTER_TURRET, "EQE", "DSD", "EIE")
                .define('E', Items.ENDER_EYE).define('Q', CommonTags.QUARTZ_GEMS).define('D', CommonTags.DIAMOND_GEMS)
                .define('S', ModItems.SENSORS.get(3)).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.LASER_TURRET, " B ", "OCO", "IOI")
                .define('B', b.get(4)).define('C', c.get(4)).define('O', Items.OBSIDIAN).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.RAILGUN_TURRET, "OBO", "OBO", "ICI")
                .define('B', b.get(4)).define('C', c.get(4)).define('O', Items.OBSIDIAN).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.MELEE_TURRET, " I ", "ISI", "CRC")
                .define('I', CommonTags.IRON_INGOTS).define('S', ModItems.SENSORS.get(0)).define('C', Items.COBBLESTONE)
                .define('R', CommonTags.REDSTONE_DUSTS));
        save.accept(turret(TurretType.CROSSBOW_TURRET, "SBS", "PCP", "PRP")
                .define('S', Items.STRING).define('B', b.get(0)).define('C', c.get(0)).define('P', ItemTags.PLANKS)
                .define('R', CommonTags.REDSTONE_DUSTS));
        save.accept(turret(TurretType.ARC_TURRET, " L ", "RCR", "DID")
                .define('L', Items.LIGHTNING_ROD).define('R', Items.REDSTONE_BLOCK).define('C', c.get(3))
                .define('D', CommonTags.DIAMOND_GEMS).define('I', ModItems.IO_BUS));
        save.accept(turret(TurretType.PLASMA_TURRET, " B ", "MCM", "DID")
                .define('B', b.get(4)).define('C', c.get(4)).define('M', Items.MAGMA_CREAM)
                .define('D', CommonTags.DIAMOND_GEMS).define('I', ModItems.IO_BUS));
    }

    /** 1.12.2 {@code lever_block_vanilla.json}: its pattern had lost its spaces; restored as a crank (user decision). */
    private void misc(Consumer<ShapedRecipeBuilder> save) {
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LEVER)
                .pattern("CCC").pattern("C  ").pattern("C  ")
                .define('C', Items.COBBLESTONE)
                .unlockedBy("has_turret_base", has(ModItems.TURRET_BASE_TIER_ONE)));
        // New block (not in 1.12.2): a furnace in an iron casing with an I/O bus
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GENERATOR)
                .pattern("CIC").pattern("IFI").pattern("CBC")
                .define('C', Items.COBBLESTONE).define('I', CommonTags.IRON_INGOTS).define('F', Items.FURNACE)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
    }

    /** OMPD recipes (1.12.2 {@code recipes/*.json}), 1.12 block names updated. */
    private void passiveDefense(Consumer<ShapedRecipeBuilder> save) {
        List<BlockItem> hardened = ModItems.HARDENED;
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, hardened.get(0), 16)
                .pattern("GCG").pattern("CSC").pattern("GCG")
                .define('G', Items.GRAVEL).define('C', Items.COBBLESTONE).define('S', Items.SAND)
                .unlockedBy("has_gravel", has(Items.GRAVEL)));
        ItemLike[] upgrades = {Items.STONE, Items.BRICKS, Items.NETHER_BRICKS, Items.OBSIDIAN};
        for (int tier = 1; tier < 5; tier++) {
            save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, hardened.get(tier), 16)
                    .pattern("HMH").pattern("MHM").pattern("HMH")
                    .define('H', hardened.get(tier - 1)).define('M', upgrades[tier - 1])
                    .unlockedBy("has_previous_tier", has(hardened.get(tier - 1))));
        }
        for (int tier = 0; tier < 5; tier++) {
            save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.WALLS.get(tier), 6)
                    .pattern("HHH").pattern("HHH")
                    .define('H', hardened.get(tier))
                    .unlockedBy("has_hardened", has(hardened.get(tier))));
        }
        fence(save, 0, Items.COBBLESTONE);
        fence(save, 1, CommonTags.IRON_INGOTS);
        fence(save, 2, CommonTags.GOLD_INGOTS);
        fence(save, 3, CommonTags.DIAMOND_GEMS);
        fence(save, 4, Items.OBSIDIAN);
        // OMPD had no recipe for the trap; proposed recipe (user request), adjust as needed
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CAMO_TRAP, 4)
                .pattern("HPH").pattern("PSP").pattern("HPH")
                .define('H', hardened.get(0)).define('P', ItemTags.PLANKS).define('S', ModItems.SENSORS.get(0))
                .unlockedBy("has_hardened", has(hardened.get(0))));
    }

    private static void fence(Consumer<ShapedRecipeBuilder> save, int tier, ItemLike material) {
        save.accept(fenceBuilder(tier).define('M', material));
    }

    private static void fence(Consumer<ShapedRecipeBuilder> save, int tier, TagKey<Item> material) {
        save.accept(fenceBuilder(tier).define('M', material));
    }

    private static ShapedRecipeBuilder fenceBuilder(int tier) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.FENCES.get(tier), 16)
                .pattern("BMB").pattern("MBM").pattern("BMB")
                .define('B', Items.IRON_BARS)
                .unlockedBy("has_iron_bars", has(Items.IRON_BARS));
    }

    private static ShapedRecipeBuilder turret(TurretType type, String row1, String row2, String row3) {
        Item turret = ModItems.TURRETS.get(type);
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, turret)
                .pattern(row1).pattern(row2).pattern(row3)
                .unlockedBy("has_turret_base", has(ModItems.TURRET_BASE_TIER_ONE));
    }

    private static ShapedRecipeBuilder upgradedBase(ItemLike result, ItemLike previous, int sensorIndex) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, result)
                .pattern("MPM").pattern("BSB").pattern("MBM")
                .define('P', previous).define('S', ModItems.SENSORS.get(sensorIndex)).define('B', ModItems.IO_BUS)
                .unlockedBy("has_previous_base", has(previous));
    }

    /** 1.12.2 {@code upgrade_meta_0..4_vanilla.json}: quartz around a core item, IO bus below. */
    private void upgrades(Consumer<ShapedRecipeBuilder> save) {
        save.accept(upgrade(ModItems.UPGRADE_ACCURACY).define('C', CommonTags.GOLD_INGOTS));
        save.accept(upgrade(ModItems.UPGRADE_EFFICIENCY).define('C', Items.ENDER_EYE));
        save.accept(upgrade(ModItems.UPGRADE_FIRE_RATE).define('C', Items.BLAZE_POWDER));
        save.accept(upgrade(ModItems.UPGRADE_RANGE).define('C', CommonTags.DIAMOND_GEMS));
        save.accept(upgrade(ModItems.UPGRADE_SCATTER_SHOT).define('C', Items.FLINT));
    }

    private static ShapedRecipeBuilder upgrade(ItemLike result) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern(" Q ")
                .pattern("QCQ")
                .pattern(" B ")
                .define('Q', CommonTags.QUARTZ_GEMS)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS));
    }

    /** 1.12.2 {@code addon_meta_0,1,3,4,6,7_vanilla.json}. */
    private void addons(Consumer<ShapedRecipeBuilder> save) {
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_CONCEALER)
                .pattern("IQI")
                .pattern("QCB")
                .pattern("IQI")
                .define('I', CommonTags.IRON_INGOTS)
                .define('Q', CommonTags.QUARTZ_GEMS)
                .define('C', Items.CHEST)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_DAMAGE_AMP)
                .pattern("III")
                .pattern("ERE")
                .pattern("III")
                .define('I', CommonTags.IRON_INGOTS)
                .define('E', Items.ENDER_EYE)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy("has_ender_eye", has(Items.ENDER_EYE)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_RECYCLER)
                .pattern("GMG")
                .pattern("MCB")
                .pattern("GMG")
                .define('G', CommonTags.GOLD_INGOTS)
                .define('M', Items.MAGMA_CREAM)
                .define('C', Items.ENDER_CHEST)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_REDSTONE_REACTOR)
                .pattern("QRQ")
                .pattern("REB")
                .pattern("QRQ")
                .define('Q', CommonTags.QUARTZ_GEMS)
                .define('R', Items.REDSTONE_BLOCK)
                .define('E', Items.ENDER_EYE)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_SOLAR_PANEL)
                .pattern("PPP")
                .pattern("RLR")
                .pattern("IBI")
                .define('P', CommonTags.GLASS_PANES)
                .define('L', Items.LAPIS_BLOCK)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .define('I', CommonTags.IRON_INGOTS)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADDON_FAKE_DROPS)
                .pattern("QLQ")
                .pattern("LEB")
                .pattern("QLQ")
                .define('Q', CommonTags.QUARTZ_GEMS)
                .define('L', Items.LAPIS_BLOCK)
                .define('E', Items.ENDER_EYE)
                .define('B', ModItems.IO_BUS)
                .unlockedBy("has_io_bus", has(ModItems.IO_BUS)));
    }

    /**
     * 1.12.2 {@code ammo_meta_0..4_vanilla.json}. The old bullet, ferro slug, grenade and rocket patterns had lost
     * their spaces; the shapes are restored from the EnderIO/Mekanism variants (bullet: gunpowder centred).
     */
    private void ammo(Consumer<ShapedRecipeBuilder> save) {
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_BLAZING_CLAY, 32)
                .pattern("CRC")
                .pattern("RBR")
                .pattern("CRC")
                .define('B', Items.BLAZE_POWDER)
                .define('C', Items.CLAY_BALL)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_BOLT, 16)
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" F ")
                .define('I', CommonTags.IRON_INGOTS)
                .define('S', Items.STICK)
                .define('F', Items.FEATHER)
                .unlockedBy("has_feather", has(Items.FEATHER)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_BULLET, 64)
                .pattern(" I ")
                .pattern(" GR")
                .pattern(" I ")
                .define('I', CommonTags.IRON_INGOTS)
                .define('G', Items.GUNPOWDER)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_FERRO_SLUG, 16)
                .pattern(" R ")
                .pattern("RFR")
                .pattern(" I ")
                .define('I', CommonTags.IRON_INGOTS)
                .define('F', Items.FLINT)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_flint", has(Items.FLINT)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_GRENADE, 32)
                .pattern(" R ")
                .pattern("IGI")
                .pattern(" I ")
                .define('I', CommonTags.IRON_INGOTS)
                .define('G', Items.GUNPOWDER)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER)));
        save.accept(ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.AMMO_ROCKET, 32)
                .pattern(" I ")
                .pattern("ITI")
                .pattern("IRI")
                .define('I', CommonTags.IRON_INGOTS)
                .define('T', Items.TNT)
                .define('R', CommonTags.REDSTONE_DUSTS)
                .unlockedBy("has_tnt", has(Items.TNT)));
    }
}
