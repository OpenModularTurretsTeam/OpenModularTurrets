package openmodularturrets.handler.recipes;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.init.ModItems;

public class RecipeHandler {
    public static ItemStack expanderInvTierOne;
    public static ItemStack expanderInvTierTwo;
    public static ItemStack expanderInvTierThree;
    public static ItemStack expanderInvTierFour;
    public static ItemStack expanderInvTierFive;
    public static ItemStack expanderPowerTierOne;
    public static ItemStack expanderPowerTierTwo;
    public static ItemStack expanderPowerTierThree;
    public static ItemStack expanderPowerTierFour;
    public static ItemStack expanderPowerTierFive;
    public static ItemStack concealerAddon;
    public static ItemStack damageAmpAddon;
    public static ItemStack potentiaAddon;
    public static ItemStack recyclerAddon;
    public static ItemStack redReactorAddon;
    public static ItemStack serialPortAddon;
    public static ItemStack solarPanelAddon;
    public static ItemStack accuraccyUpgrade;
    public static ItemStack efficiencyUpgrade;
    public static ItemStack fireRateUpgrade;
    public static ItemStack rangeUpgrade;
    public static ItemStack scattershotUpgrade;
    public static ItemStack containmentChamber;
    public static ItemStack energeticBarrel;
    public static ItemStack ioBus;


    public static void initRecipes() {

        expanderInvTierOne = new ItemStack(ModBlocks.expander, 1, 0);
        expanderInvTierTwo = new ItemStack(ModBlocks.expander, 1, 1);
        expanderInvTierThree = new ItemStack(ModBlocks.expander, 1, 2);
        expanderInvTierFour = new ItemStack(ModBlocks.expander, 1, 3);
        expanderInvTierFive = new ItemStack(ModBlocks.expander, 1, 4);
        expanderPowerTierOne = new ItemStack(ModBlocks.expander, 1, 5);
        expanderPowerTierTwo = new ItemStack(ModBlocks.expander, 1, 6);
        expanderPowerTierThree = new ItemStack(ModBlocks.expander, 1, 7);
        expanderPowerTierFour = new ItemStack(ModBlocks.expander, 1, 8);
        expanderPowerTierFive = new ItemStack(ModBlocks.expander, 1, 9);
        concealerAddon = new ItemStack(ModItems.addonMetaItem, 1, 0);
        damageAmpAddon = new ItemStack(ModItems.addonMetaItem, 1, 1);
        potentiaAddon = new ItemStack(ModItems.addonMetaItem, 1, 2);
        recyclerAddon = new ItemStack(ModItems.addonMetaItem, 1, 3);
        redReactorAddon = new ItemStack(ModItems.addonMetaItem, 1, 4);
        serialPortAddon = new ItemStack(ModItems.addonMetaItem, 1, 5);
        solarPanelAddon = new ItemStack(ModItems.addonMetaItem, 1, 6);
        accuraccyUpgrade = new ItemStack(ModItems.upgradeMetaItem, 1, 0);
        efficiencyUpgrade = new ItemStack(ModItems.upgradeMetaItem, 1, 1);
        fireRateUpgrade = new ItemStack(ModItems.upgradeMetaItem, 1, 2);
        rangeUpgrade = new ItemStack(ModItems.upgradeMetaItem, 1, 3);
        scattershotUpgrade = new ItemStack(ModItems.upgradeMetaItem, 1, 4);
        containmentChamber = new ItemStack(ModItems.intermediateProductRegular, 1, 0);
        energeticBarrel = new ItemStack(ModItems.intermediateProductRegular, 1, 1);
        ioBus = new ItemStack(ModItems.intermediateProductRegular, 1, 2);

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 10), "AAA", " B ", "AAA", 'A',
                Blocks.cobblestone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 5), "AAA", " BC", "AAA", 'A',
                Blocks.cobblestone, 'B', "plankWood", 'C',
                Items.redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 0), " A ", "ABA", " A ", 'A',
                Items.redstone, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 1), "ABA", "BCB", "ABA", 'A',
                Blocks.cobblestone, 'B', "plankWood", 'C',
                new ItemStack(ModItems.intermediateProductTiered, 1, 0)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(expanderPowerTierOne, "ABA", "DCD", "ADA", 'A',
                        Blocks.cobblestone, 'B', "plankWood", 'C',
                        Items.redstone, 'D', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(expanderInvTierOne, "ABA", "DCD", "ADA", 'A',
                        Blocks.cobblestone, 'B', "plankWood", 'C',
                        Blocks.chest, 'D', ioBus));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.disposableItemTurret, 1), " A ", "CBC", "CDC", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                            Blocks.cobblestone, 'D', Items.redstone));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.potatoCannonTurret, 1), "CAC", "CAC", "DBD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                            Blocks.cobblestone, 'D', Items.redstone));
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.leverBlock, 1), "AAA", "A  ", "A  ", 'A',
                Blocks.cobblestone);

        // Addons
        GameRegistry.addRecipe(solarPanelAddon, "AAA", "CBC", "DED", 'A',
                Blocks.glass_pane, 'B',
                Blocks.lapis_block, 'C',
                Items.redstone, 'D', Items.iron_ingot, 'E',
                ioBus);

        GameRegistry.addRecipe(
                new ShapedOreRecipe(redReactorAddon, "CAC", "ABD", "CAC", 'A', Items.iron_ingot,
                        'B', Items.ender_eye, 'C', Items.quartz, 'D',
                        ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(damageAmpAddon, "AAA", "B B", "AAA", 'A', Items.iron_ingot, 'B',
                        Items.ender_pearl));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(recyclerAddon, "ABA", "BCD", "ABA", 'A', "ingotGold", 'B',
                        Items.magma_cream, 'C', Blocks.ender_chest,
                        'D', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(concealerAddon, "ABA", "BCD", "ABA", 'A', Items.iron_ingot, 'B',
                        Items.quartz, 'C', Blocks.chest,
                        'D', ioBus));

        /*//Fences
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierOne, 16), "ABA", "BAB", "ABA", 'A',
                Blocks.iron_bars, 'B',
                Blocks.cobblestone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                Blocks.iron_bars, 'B', Items.iron_ingot));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierThree, 16), "ABA", "BAB", "ABA", 'A',
                Blocks.iron_bars, 'B', "ingotGold"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFour, 16), "ABA", "BAB", "ABA", 'A',
                Blocks.iron_bars, 'B',
                Items.diamond));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.fenceTierFive, 16), "ABA", "BAB", "ABA", 'A',
                Blocks.iron_bars, 'B',
                Blocks.obsidian));

        //Hard Walls
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierOne, 16), "ABA", "BCB", "ABA", 'A',
                Blocks.gravel, 'B',
                Blocks.cobblestone, 'C',
                Blocks.sand));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierTwo, 16), "ABA", "BAB", "ABA", 'A',
                ModBlocks.hardWallTierOne, 'B', Blocks.stone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierThree, 16), "ABA", "BAB", "ABA", 'A',
                        ModBlocks.hardWallTierTwo, 'B', Blocks.brick_block));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFour, 16), "ABA", "BAB", "ABA", 'A',
                ModBlocks.hardWallTierThree, 'B',
                Blocks.nether_brick));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.hardWallTierFive, 16), "ABA", "BAB", "ABA", 'A',
                ModBlocks.hardWallTierThree, 'B', Blocks.obsidian));
                   */
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
                        Items.quartz, 'B', Items.ender_eye, 'C',
                        ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.accuracyUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                        Items.quartz, 'B', "ingotGold", 'C', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.fireRateUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                        Items.quartz, 'B', Items.blaze_powder, 'C',
                        ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.rangeUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                Items.quartz, 'B',
                Items.diamond, 'C', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.scattershotUpgradeItem, 1), " A ", "ABA", " C ", 'A',
                        Items.quartz, 'B', Items.flint, 'C',
                        ioBus));

        //Ammo
        GameRegistry.addRecipe(new ItemStack(ModItems.blazingClayCraftable, 32), "BCB", "CAC", "BCB", 'A',
                Items.blaze_powder, 'B', Items.clay_ball, 'C',
                Items.redstone);

        //Other
        GameRegistry.addRecipe(
                new ShapedOreRecipe(ioBus, " A ", "BBB", " C ", 'A', "ingotGold", 'B',
                        Items.redstone, 'C', Items.iron_ingot));

    }
}
