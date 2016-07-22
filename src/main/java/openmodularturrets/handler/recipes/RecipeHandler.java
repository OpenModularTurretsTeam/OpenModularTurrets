package openmodularturrets.handler.recipes;


import net.minecraft.item.ItemStack;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModBlocks;

public class RecipeHandler {
    public static ItemStack expanderInvTierOne = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderInvTierTwo = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderInvTierThree = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderInvTierFour = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderInvTierFive = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderPowerTierOne = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderPowerTierTwo = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderPowerTierThree = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderPowerTierFour = new ItemStack(ModBlocks.expander,1 ,0);
    public static ItemStack expanderPowerTierFive = new ItemStack(ModBlocks.expander,1 ,0);
    public static void initRecipes() {
        boolean recipesDone = false;
        // Recipes

        if (ModCompatibility.ThermalExpansionLoaded && ConfigHandler.recipes.equals("thermalexpansion")) {
            //ThermalExpansionRecipeHandler.initBlocks();
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
                //ThermalExpansionRecipeHandler.initBlocks();
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
        /*GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,10), "AAA", " B ", "AAA", 'A',
                                                   net.minecraft.initBlocks.Blocks.cobblestone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,5), "AAA", " BC", "AAA", 'A',
                                                   net.minecraft.initBlocks.Blocks.cobblestone, 'B', "plankWood", 'C',
                                                   net.minecraft.initBlocks.Items.redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,0), " A ", "ABA", " A ", 'A',
                                                   net.minecraft.initBlocks.Items.redstone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,1), "ABA", "BCB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.cobblestone, 'B', "plankWood", 'C',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,0)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierOne, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.cobblestone, 'B', "plankWood", 'C',
                                    net.minecraft.initBlocks.Items.redstone, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierOne, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.cobblestone, 'B', "plankWood", 'C',
                                    net.minecraft.initBlocks.Blocks.chest, 'D', ModItems.ioBus));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.disposableItemTurret, 1), " A ", "CBC", "CDC", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C',
                                        net.minecraft.initBlocks.Blocks.cobblestone, 'D', net.minecraft.initBlocks.Items.redstone));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.potatoCannonTurret, 1), "CAC", "CAC", "DBD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C',
                                        net.minecraft.initBlocks.Blocks.cobblestone, 'D', net.minecraft.initBlocks.Items.redstone));
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.leverBlock, 1), "AAA", "A  ", "A  ", 'A',
                               net.minecraft.initBlocks.Blocks.cobblestone);

        // Addons
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.solarPanelAddon, 1), "AAA", "CBC", "DED", 'A',
                                                   net.minecraft.initBlocks.Blocks.glass_pane, 'B',
                                                   net.minecraft.initBlocks.Blocks.lapis_block, 'C',
                                                   net.minecraft.initBlocks.Items.redstone, 'D', "ingotIron", 'E',
                                                   ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.redstoneReactorAddon, 1), "CAC", "ABD", "CAC", 'A', "ingotIron",
                                    'B', net.minecraft.initBlocks.Items.ender_eye, 'C', net.minecraft.initBlocks.Items.quartz, 'D',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.damageAmpAddon, 1), "AAA", "B B", "AAA", 'A', "ingotIron", 'B',
                                    net.minecraft.initBlocks.Items.ender_pearl));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.recyclerAddon, 1), "ABA", "BCD", "ABA", 'A', "ingotGold", 'B',
                                    net.minecraft.initBlocks.Items.magma_cream, 'C', net.minecraft.initBlocks.Blocks.ender_chest,
                                    'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.concealerAddon, 1), "ABA", "BCD", "ABA", 'A', "ingotIron", 'B',
                                    net.minecraft.initBlocks.Items.quartz, 'C', net.minecraft.initBlocks.Blocks.chest,
                                    'D', ModItems.ioBus));

        //Fences
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierOne, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.iron_bars, 'B',
                                                   net.minecraft.initBlocks.Blocks.cobblestone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.iron_bars, 'B', "ingotIron"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierThree, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.iron_bars, 'B', "ingotGold"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFour, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.iron_bars, 'B',
                                                   net.minecraft.initBlocks.Items.diamond));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFive, 16), "ABA", "BAB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.iron_bars, 'B',
                                                   net.minecraft.initBlocks.Blocks.obsidian));

        //Hard Walls
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierOne, 16), "ABA", "BCB", "ABA", 'A',
                                                   net.minecraft.initBlocks.Blocks.gravel, 'B',
                                                   net.minecraft.initBlocks.Blocks.cobblestone, 'C',
                                                   net.minecraft.initBlocks.Blocks.sand));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierOne, 'B', net.minecraft.initBlocks.Blocks.stone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierThree, 16), "ABA", "BAB", "ABA", 'A',
                                    ModBlocks.hardWallTierTwo, 'B', net.minecraft.initBlocks.Blocks.brick_block));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFour, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierThree, 'B',
                                                   net.minecraft.initBlocks.Blocks.nether_brick));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFive, 16), "ABA", "BAB", "ABA", 'A',
                                                   ModBlocks.hardWallTierThree, 'B', net.minecraft.initBlocks.Blocks.obsidian));

        // Integration
        if (ModCompatibility.ThaumcraftLoaded && ConfigHandler.shouldDoThaumcraftIntegration) {
            //ThaumcraftRecipeHandler.initBlocks();
        }

        if ((ModCompatibility.ComputercraftLoaded || ModCompatibility.OpenComputersLoaded) && ConfigHandler.shouldDoThaumcraftIntegration) {
            ComputerRecipeHandler.initBlocks();
        }

        // Upgrades
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.efficiencyUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.initBlocks.Items.quartz, 'B', net.minecraft.initBlocks.Items.ender_eye, 'C',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.accuracyUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.initBlocks.Items.quartz, 'B', "ingotGold", 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.fireRateUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.initBlocks.Items.quartz, 'B', net.minecraft.initBlocks.Items.blaze_powder, 'C',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.rangeUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                                   net.minecraft.initBlocks.Items.quartz, 'B',
                                                   net.minecraft.initBlocks.Items.diamond, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.scattershotUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                                    net.minecraft.initBlocks.Items.quartz, 'B', net.minecraft.initBlocks.Items.flint, 'C',
                                    ModItems.ioBus));

        //Ammo
        GameRegistry.addRecipe(new ItemStack(ModItems.blazingClayCraftable, 32), "BCB", "CAC", "BCB", 'A',
                               net.minecraft.initBlocks.Items.blaze_powder, 'B', net.minecraft.initBlocks.Items.clay_ball, 'C',
                               net.minecraft.initBlocks.Items.redstone);

        //Other
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ioBus, 1), " A ", "BBB", " C ", 'A', "ingotGold", 'B',
                                    net.minecraft.initBlocks.Items.redstone, 'C', "ingotIron"));
                                    */
    }
}
