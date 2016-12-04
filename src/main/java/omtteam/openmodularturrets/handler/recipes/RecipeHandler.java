package omtteam.openmodularturrets.handler.recipes;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import omtteam.openmodularturrets.compatability.ModCompatibility;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;

public class RecipeHandler {
    public static ItemStack ammoBlazingClay;
    public static ItemStack ammoBullet;
    public static ItemStack ammoFerroSlug;
    public static ItemStack ammoGrenade;
    public static ItemStack ammoRocket;

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

    public static ItemStack addonConcealer;
    public static ItemStack addonDamageAmp;
    public static ItemStack addonPotentia;
    public static ItemStack addonRecycler;
    public static ItemStack addonRedstoneReactor;
    public static ItemStack addonSerialPort;
    public static ItemStack addonSolarPanel;

    public static ItemStack upgradeAccuracy;
    public static ItemStack upgradeEfficiency;
    public static ItemStack upgradeFireRate;
    public static ItemStack upgradeRange;
    public static ItemStack upgradeScatterShot;

    public static ItemStack ioBus;


    public static void initRecipes() {

        ammoBlazingClay = new ItemStack(ModItems.ammoMetaItem, 32, 0);
        ammoBullet = new ItemStack(ModItems.ammoMetaItem, 64, 1);
        ammoFerroSlug = new ItemStack(ModItems.ammoMetaItem, 16, 2);
        ammoGrenade = new ItemStack(ModItems.ammoMetaItem, 32, 3);
        ammoRocket = new ItemStack(ModItems.ammoMetaItem, 32, 4);
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
        addonConcealer = new ItemStack(ModItems.addonMetaItem, 1, 0);
        addonDamageAmp = new ItemStack(ModItems.addonMetaItem, 1, 1);
        addonPotentia = new ItemStack(ModItems.addonMetaItem, 1, 2);
        addonRecycler = new ItemStack(ModItems.addonMetaItem, 1, 3);
        addonRedstoneReactor = new ItemStack(ModItems.addonMetaItem, 1, 4);
        addonSerialPort = new ItemStack(ModItems.addonMetaItem, 1, 5);
        addonSolarPanel = new ItemStack(ModItems.addonMetaItem, 1, 6);
        upgradeAccuracy = new ItemStack(ModItems.upgradeMetaItem, 1, 0);
        upgradeEfficiency = new ItemStack(ModItems.upgradeMetaItem, 1, 1);
        upgradeFireRate = new ItemStack(ModItems.upgradeMetaItem, 1, 2);
        upgradeRange = new ItemStack(ModItems.upgradeMetaItem, 1, 3);
        upgradeScatterShot = new ItemStack(ModItems.upgradeMetaItem, 1, 4);
        ioBus = new ItemStack(ModItems.intermediateProductRegular, 1, 0);

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
                Blocks.COBBLESTONE, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 5), "AAA", " BC", "AAA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                Items.REDSTONE));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 0), " A ", "ABA", " A ", 'A',
                Items.REDSTONE, 'B', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 0), "ABA", "BCB", "ABA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                new ItemStack(ModItems.intermediateProductTiered, 1, 0)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(expanderPowerTierOne, "ABA", "DCD", "ADA", 'A',
                        Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                        Items.REDSTONE, 'D', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(expanderInvTierOne, "ABA", "DCD", "ADA", 'A',
                        Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                        Blocks.CHEST, 'D', ioBus));

        if (ConfigHandler.getDisposableTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.disposableItemTurret, 1), " A ", "CBC", "CDC", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                            Blocks.COBBLESTONE, 'D', Items.REDSTONE));
        }

        if (ConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.potatoCannonTurret, 1), "CAC", "CAC", "DBD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                            Blocks.COBBLESTONE, 'D', Items.REDSTONE));
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.leverBlock, 1), "AAA", "A  ", "A  ", 'A',
                Blocks.COBBLESTONE);

        // Addons
        GameRegistry.addRecipe(addonSolarPanel, "AAA", "CBC", "DED", 'A',
                Blocks.GLASS_PANE, 'B',
                Blocks.LAPIS_BLOCK, 'C',
                Items.REDSTONE, 'D', Items.IRON_INGOT, 'E',
                ioBus);

        GameRegistry.addRecipe(
                new ShapedOreRecipe(addonRedstoneReactor, "CAC", "ABD", "CAC", 'A', Items.IRON_INGOT,
                        'B', Items.ENDER_EYE, 'C', Items.QUARTZ, 'D',
                        ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(addonDamageAmp, "AAA", "B B", "AAA", 'A', Items.IRON_INGOT, 'B',
                        Items.ENDER_PEARL));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(addonRecycler, "ABA", "BCD", "ABA", 'A', "ingotGold", 'B',
                        Items.MAGMA_CREAM, 'C', Blocks.ENDER_CHEST,
                        'D', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(addonConcealer, "ABA", "BCD", "ABA", 'A', Items.IRON_INGOT, 'B',
                        Items.QUARTZ, 'C', Blocks.CHEST,
                        'D', ioBus));

        // Integration
        if (ModCompatibility.ThaumcraftLoaded && ConfigHandler.shouldDoThaumcraftIntegration) {
            //ThaumcraftRecipeHandler.init();
        }

        if ((ModCompatibility.ComputerCraftLoaded || ModCompatibility.OpenComputersLoaded) && ConfigHandler.shouldDoThaumcraftIntegration) {
            ComputerRecipeHandler.init();
        }

        // Upgrades
        GameRegistry.addRecipe(
                new ShapedOreRecipe(upgradeEfficiency, " A ", "ABA", " C ", 'A',
                        Items.QUARTZ, 'B', Items.ENDER_EYE, 'C',
                        ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(upgradeAccuracy, " A ", "ABA", " C ", 'A',
                        Items.QUARTZ, 'B', "ingotGold", 'C', ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(upgradeFireRate, " A ", "ABA", " C ", 'A',
                        Items.QUARTZ, 'B', Items.BLAZE_POWDER, 'C',
                        ioBus));

        GameRegistry.addRecipe(upgradeRange, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B',
                Items.DIAMOND, 'C', ioBus);

        GameRegistry.addRecipe(
                new ShapedOreRecipe(upgradeScatterShot, " A ", "ABA", " C ", 'A',
                        Items.QUARTZ, 'B', Items.FLINT, 'C',
                        ioBus));

        //Ammo
        GameRegistry.addRecipe(ammoBlazingClay, "BCB", "CAC", "BCB", 'A',
                Items.BLAZE_POWDER, 'B', Items.CLAY_BALL, 'C',
                Items.REDSTONE);

        //Other
        GameRegistry.addRecipe(
                new ShapedOreRecipe(ioBus, " A ", "BBB", " C ", 'A', "ingotGold", 'B',
                        Items.REDSTONE, 'C', Items.IRON_INGOT));

    }
}
