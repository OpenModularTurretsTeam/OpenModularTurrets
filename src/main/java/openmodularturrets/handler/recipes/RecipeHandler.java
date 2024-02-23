package openmodularturrets.handler.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

public class RecipeHandler {

    public static void initRecipes() {
        boolean recipesDone = false;
        // Recipes

        if (ModCompatibility.ThermalExpansionLoaded && ConfigHandler.recipes.equals("thermalexpansion")) {
            ThermalExpansionRecipeHandler.init();
            recipesDone = true;
        } else if (ModCompatibility.EnderIOLoaded && ConfigHandler.recipes.equals("enderio")) {
            EnderIORecipeHandler.init();
            recipesDone = true;
        } else if (ModCompatibility.MekanismLoaded && ConfigHandler.recipes.equals("mekanism")) {
            MekanismRecipeHandler.init();
            recipesDone = true;
        } else if (ConfigHandler.recipes.equals("vanilla")) {
            VanillaRecipeHandler.init();
            recipesDone = true;
        } else if (ConfigHandler.recipes.equals("auto")) {
            if (ModCompatibility.EnderIOLoaded) {
                EnderIORecipeHandler.init();
            } else if (ModCompatibility.ThermalExpansionLoaded) {
                ThermalExpansionRecipeHandler.init();
            } else if (ModCompatibility.MekanismLoaded) {
                MekanismRecipeHandler.init();
            } else {
                VanillaRecipeHandler.init();
            }
            recipesDone = true;
        }

        // Only do vanilla if setting was invalid (recipes chosen but mod not available)
        if (!recipesDone) {
            VanillaRecipeHandler.init();
        }

        // RECIPES THAT DON'T CHANGE BASED ON MODS LOADED:
        // Tier 1 static recipes (Because they shouldn't use expensive mod items, only redstone, cobblestone and planks)
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierOneItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        net.minecraft.init.Blocks.cobblestone,
                        'B',
                        "plankWood"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierOneItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        net.minecraft.init.Blocks.cobblestone,
                        'B',
                        "plankWood",
                        'C',
                        net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierOneItem, 1),
                        " A ",
                        "ABA",
                        " A ",
                        'A',
                        net.minecraft.init.Items.redstone,
                        'B',
                        "plankWood"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierOne, 1),
                        "ABA",
                        "BCB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.cobblestone,
                        'B',
                        "plankWood",
                        'C',
                        Items.sensorTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierOne, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.cobblestone,
                        'B',
                        "plankWood",
                        'C',
                        net.minecraft.init.Items.redstone,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierOne, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.cobblestone,
                        'B',
                        "plankWood",
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        Items.ioBus));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.disposableItemTurret, 1),
                            " A ",
                            "CBC",
                            "CDC",
                            'A',
                            Items.barrelTierOneItem,
                            'B',
                            Items.chamberTierOneItem,
                            'C',
                            net.minecraft.init.Blocks.cobblestone,
                            'D',
                            net.minecraft.init.Items.redstone));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.potatoCannonTurret, 1),
                            "CAC",
                            "CAC",
                            "DBD",
                            'A',
                            Items.barrelTierOneItem,
                            'B',
                            Items.chamberTierOneItem,
                            'C',
                            net.minecraft.init.Blocks.cobblestone,
                            'D',
                            net.minecraft.init.Items.redstone));
        }

        GameRegistry.addRecipe(
                new ItemStack(Blocks.leverBlock, 1),
                "AAA",
                "A  ",
                "A  ",
                'A',
                net.minecraft.init.Blocks.cobblestone);

        // Addons
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.solarPanelAddon, 1),
                        "AAA",
                        "CBC",
                        "DED",
                        'A',
                        net.minecraft.init.Blocks.glass_pane,
                        'B',
                        net.minecraft.init.Blocks.lapis_block,
                        'C',
                        net.minecraft.init.Items.redstone,
                        'D',
                        "ingotIron",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.redstoneReactorAddon, 1),
                        "CAC",
                        "ABD",
                        "CAC",
                        'A',
                        "ingotIron",
                        'B',
                        net.minecraft.init.Items.ender_eye,
                        'C',
                        net.minecraft.init.Items.quartz,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.damageAmpAddon, 1),
                        "AAA",
                        "B B",
                        "AAA",
                        'A',
                        "ingotIron",
                        'B',
                        net.minecraft.init.Items.ender_pearl));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.recyclerAddon, 1),
                        "ABA",
                        "BCD",
                        "ABA",
                        'A',
                        "ingotGold",
                        'B',
                        net.minecraft.init.Items.magma_cream,
                        'C',
                        net.minecraft.init.Blocks.ender_chest,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.concealerAddon, 1),
                        "ABA",
                        "BCD",
                        "ABA",
                        'A',
                        "ingotIron",
                        'B',
                        net.minecraft.init.Items.quartz,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        Items.ioBus));

        // Fences
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.fenceTierOne, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.iron_bars,
                        'B',
                        net.minecraft.init.Blocks.cobblestone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.fenceTierTwo, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.iron_bars,
                        'B',
                        "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.fenceTierThree, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.iron_bars,
                        'B',
                        "ingotGold"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.fenceTierFour, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.iron_bars,
                        'B',
                        net.minecraft.init.Items.diamond));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.fenceTierFive, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.iron_bars,
                        'B',
                        net.minecraft.init.Blocks.obsidian));

        // Hard Walls
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.hardWallTierOne, 16),
                        "ABA",
                        "BCB",
                        "ABA",
                        'A',
                        net.minecraft.init.Blocks.gravel,
                        'B',
                        net.minecraft.init.Blocks.cobblestone,
                        'C',
                        net.minecraft.init.Blocks.sand));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.hardWallTierTwo, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        Blocks.hardWallTierOne,
                        'B',
                        net.minecraft.init.Blocks.stone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.hardWallTierThree, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        Blocks.hardWallTierTwo,
                        'B',
                        net.minecraft.init.Blocks.brick_block));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.hardWallTierFour, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        Blocks.hardWallTierThree,
                        'B',
                        net.minecraft.init.Blocks.nether_brick));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.hardWallTierFive, 16),
                        "ABA",
                        "BAB",
                        "ABA",
                        'A',
                        Blocks.hardWallTierThree,
                        'B',
                        net.minecraft.init.Blocks.obsidian));

        // Integration
        if (ModCompatibility.ThaumcraftLoaded && ConfigHandler.shouldDoThaumcraftIntegration) {
            ThaumcraftRecipeHandler.init();
        }

        if ((ModCompatibility.ComputercraftLoaded || ModCompatibility.OpenComputersLoaded)
                && ConfigHandler.shouldDoThaumcraftIntegration) {
            ComputerRecipeHandler.init();
        }

        // Upgrades
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.efficiencyUpgradeItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        net.minecraft.init.Items.ender_eye,
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.accuraccyUpgradeItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        "ingotGold",
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.fireRateUpgradeItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        net.minecraft.init.Items.blaze_powder,
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.rangeUpgradeItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        net.minecraft.init.Items.diamond,
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.scattershotUpgradeItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        net.minecraft.init.Items.quartz,
                        'B',
                        net.minecraft.init.Items.flint,
                        'C',
                        Items.ioBus));

        // Ammo
        GameRegistry.addRecipe(
                new ItemStack(Items.blazingClayCraftable, 32),
                "BCB",
                "CAC",
                "BCB",
                'A',
                net.minecraft.init.Items.blaze_powder,
                'B',
                net.minecraft.init.Items.clay_ball,
                'C',
                net.minecraft.init.Items.redstone);

        // Other
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.ioBus, 1),
                        " A ",
                        "BBB",
                        " C ",
                        'A',
                        "ingotGold",
                        'B',
                        net.minecraft.init.Items.redstone,
                        'C',
                        "ingotIron"));
    }
}
