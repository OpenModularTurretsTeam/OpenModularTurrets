package openmodularturrets.handler.recipes;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.ModBlocks;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.ModItems;

public class RecipeHandler {
    public static void initRecipes() {
        boolean recipesDone = false;
        // Recipes

        if (ModCompatibility.ThermalExpansionLoaded && ConfigHandler.recipes.equals("thermalexpansion")) {
            //ThermalExpansionRecipeHandler.init();
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
                //ThermalExpansionRecipeHandler.init();
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

        //RECIPES THAT DON'T CHANGE BASED ON MODS LOADED:
        //Tier 1 static recipes (Because they shouldn't use expensive mod items, only redstone, cobblestone and planks)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierOneItem, 1), "AAA", " B ", "AAA", 'A',
                                                   net.minecraft.init.Blocks.cobblestone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.chamberTierOneItem, 1), "AAA", " BC", "AAA", 'A',
                                                   net.minecraft.init.Blocks.cobblestone, 'B', "plankWood", 'C',
                                                   net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierOneItem, 1), " A ", "ABA", " A ", 'A',
                                                   net.minecraft.init.Items.redstone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,1), "ABA", "BCB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.cobblestone, 'B', "plankWood", 'C',
                                                   ModItems.sensorTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierOne, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Blocks.cobblestone, 'B', "plankWood", 'C',
                                    net.minecraft.init.Items.redstone, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierOne, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Blocks.cobblestone, 'B', "plankWood", 'C',
                                    net.minecraft.init.Blocks.chest, 'D', ModItems.ioBus));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.disposableItemTurret, 1), " A ", "CBC", "CDC", 'A',
                                        ModItems.barrelTierOneItem, 'B', ModItems.chamberTierOneItem, 'C',
                                        net.minecraft.init.Blocks.cobblestone, 'D', net.minecraft.init.Items.redstone));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.potatoCannonTurret, 1), "CAC", "CAC", "DBD", 'A',
                                        ModItems.barrelTierOneItem, 'B', ModItems.chamberTierOneItem, 'C',
                                        net.minecraft.init.Blocks.cobblestone, 'D', net.minecraft.init.Items.redstone));
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.leverBlock, 1), "AAA", "A  ", "A  ", 'A',
                               net.minecraft.init.Blocks.cobblestone);

        // Addons
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.solarPanelAddon, 1), "AAA", "CBC", "DED", 'A',
                                                   net.minecraft.init.Blocks.glass_pane, 'B',
                                                   net.minecraft.init.Blocks.lapis_block, 'C',
                                                   net.minecraft.init.Items.redstone, 'D', "ingotIron", 'E',
                                                   ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.redstoneReactorAddon, 1), "CAC", "ABD", "CAC", 'A', "ingotIron",
                                    'B', net.minecraft.init.Items.ender_eye, 'C', net.minecraft.init.Items.quartz, 'D',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.damageAmpAddon, 1), "AAA", "B B", "AAA", 'A', "ingotIron", 'B',
                                    net.minecraft.init.Items.ender_pearl));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.recyclerAddon, 1), "ABA", "BCD", "ABA", 'A', "ingotGold", 'B',
                                    net.minecraft.init.Items.magma_cream, 'C', net.minecraft.init.Blocks.ender_chest,
                                    'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.concealerAddon, 1), "ABA", "BCD", "ABA", 'A', "ingotIron", 'B',
                                    net.minecraft.init.Items.quartz, 'C', net.minecraft.init.Blocks.chest,
                                    'D', ModItems.ioBus));

        //Fences
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierOne, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.iron_bars, 'B',
                                                   net.minecraft.init.Blocks.cobblestone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.iron_bars, 'B', "ingotIron"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierThree, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.iron_bars, 'B', "ingotGold"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFour, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.iron_bars, 'B',
                                                   net.minecraft.init.Items.diamond));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFive, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.iron_bars, 'B',
                                                   net.minecraft.init.Blocks.obsidian));

        //Hard Walls
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierOne, 16), "ABA", "BCB", "ABA", 'A',
                                                   net.minecraft.init.Blocks.gravel, 'B',
                                                   net.minecraft.init.Blocks.cobblestone, 'C',
                                                   net.minecraft.init.Blocks.sand));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierOne, 'B', net.minecraft.init.Blocks.stone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierThree, 16), "ABA", "BAB", "ABA", 'A',
                                    ModBlocks.hardWallTierTwo, 'B', net.minecraft.init.Blocks.brick_block));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFour, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierThree, 'B',
                                                   net.minecraft.init.Blocks.nether_brick));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFive, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierThree, 'B', net.minecraft.init.Blocks.obsidian));

        // Integration
        if (ModCompatibility.ThaumcraftLoaded && ConfigHandler.shouldDoThaumcraftIntegration) {
            //ThaumcraftRecipeHandler.init();
        }

        if ((ModCompatibility.ComputercraftLoaded || ModCompatibility.OpenComputersLoaded) && ConfigHandler.shouldDoThaumcraftIntegration) {
            ComputerRecipeHandler.init();
        }

        // Upgrades
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.efficiencyUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.init.Items.quartz, 'B', net.minecraft.init.Items.ender_eye, 'C',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.accuraccyUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.init.Items.quartz, 'B', "ingotGold", 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.fireRateUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.init.Items.quartz, 'B', net.minecraft.init.Items.blaze_powder, 'C',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.rangeUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                                   net.minecraft.init.Items.quartz, 'B',
                                                   net.minecraft.init.Items.diamond, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.scattershotUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.init.Items.quartz, 'B', net.minecraft.init.Items.flint, 'C',
                                    ModItems.ioBus));

        //Ammo
        GameRegistry.addRecipe(new ItemStack(ModItems.blazingClayCraftable, 32), "BCB", "CAC", "BCB", 'A',
                               net.minecraft.init.Items.blaze_powder, 'B', net.minecraft.init.Items.clay_ball, 'C',
                               net.minecraft.init.Items.redstone);

        //Other
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ioBus, 1), " A ", "BBB", " C ", 'A', "ingotGold", 'B',
                                    net.minecraft.init.Items.redstone, 'C', "ingotIron"));
    }
}
