package omtteam.openmodularturrets.handler.recipes;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import omtteam.omlib.util.JSONRecipeBuilder;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;

import static omtteam.omlib.compatibility.ModCompatibility.ComputerCraftLoaded;
import static omtteam.omlib.compatibility.ModCompatibility.OpenComputersLoaded;

public class OMTRecipeHandler {
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

    @SuppressWarnings("unused")
    public static ItemStack addonPotentia;
    public static ItemStack addonSerialPort;

    public static ItemStack ioBus;


    @SuppressWarnings("StatementWithEmptyBody")
    public static void initRecipes() {

        ItemStack ammoBlazingClay = new ItemStack(ModItems.ammoMetaItem, 32, 0);
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
        ItemStack addonConcealer = new ItemStack(ModItems.addonMetaItem, 1, 0);
        ItemStack addonDamageAmp = new ItemStack(ModItems.addonMetaItem, 1, 1);
        addonPotentia = new ItemStack(ModItems.addonMetaItem, 1, 2);
        ItemStack addonRecycler = new ItemStack(ModItems.addonMetaItem, 1, 3);
        ItemStack addonRedstoneReactor = new ItemStack(ModItems.addonMetaItem, 1, 4);
        addonSerialPort = new ItemStack(ModItems.addonMetaItem, 1, 5);
        ItemStack addonSolarPanel = new ItemStack(ModItems.addonMetaItem, 1, 6);
        ItemStack addonFakeDrops = new ItemStack(ModItems.addonMetaItem, 1, 7);
        ItemStack upgradeAccuracy = new ItemStack(ModItems.upgradeMetaItem, 1, 0);
        ItemStack upgradeEfficiency = new ItemStack(ModItems.upgradeMetaItem, 1, 1);
        ItemStack upgradeFireRate = new ItemStack(ModItems.upgradeMetaItem, 1, 2);
        ItemStack upgradeRange = new ItemStack(ModItems.upgradeMetaItem, 1, 3);
        ItemStack upgradeScatterShot = new ItemStack(ModItems.upgradeMetaItem, 1, 4);
        ioBus = new ItemStack(ModItems.intermediateProductRegular, 1, 0);
        ItemStack memoryCard = new ItemStack(ModItems.usableMetaItem, 1, 2);


        // Recipes
        JSONRecipeBuilder.setupDir(OMTConfigHandler.config);

        //EnderIORecipeHandler.init();

        MekanismRecipeHandler.init();

        VanillaRecipeHandler.init();


        // Only do vanilla if setting was invalid (recipes chosen but mod not available)


        //RECIPES THAT DON'T CHANGE BASED ON MODS LOADED:
        //Tier 1 static recipes (Because they shouldn't use expensive mod items, only redstone, cobblestone and planks)
        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 10), "AAA", " B ", "AAA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood");

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 5), "AAA", " BC", "AAA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                Items.REDSTONE);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 0), " A ", "ABA", " A ", 'A',
                Items.REDSTONE, 'B', "plankWood");

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.turretBase, 1, 0), "ABA", "BCB", "ABA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                new ItemStack(ModItems.intermediateProductTiered, 1, 0));

        JSONRecipeBuilder.addShapedRecipe(expanderPowerTierOne, "ABA", "DCD", "ADA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                Items.REDSTONE, 'D', ioBus);

        JSONRecipeBuilder.addShapedRecipe(expanderInvTierOne, "ABA", "DCD", "ADA", 'A',
                Blocks.COBBLESTONE, 'B', "plankWood", 'C',
                Blocks.CHEST, 'D', ioBus);

        if (OMTConfigHandler.getDisposableTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.disposableItemTurret, 1), " A ", "CBC", "CDC", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                    Blocks.COBBLESTONE, 'D', Items.REDSTONE);
        }

        if (OMTConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.potatoCannonTurret, 1), "CAC", "CAC", "DBD", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C',
                    Blocks.COBBLESTONE, 'D', Items.REDSTONE);
        }

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.leverBlock, 1), "AAA", "A  ", "A  ", 'A',
                Blocks.COBBLESTONE);

        // Addons
        JSONRecipeBuilder.addShapedRecipe(addonSolarPanel, "AAA", "CBC", "DED", 'A',
                Blocks.GLASS_PANE, 'B',
                Blocks.LAPIS_BLOCK, 'C',
                Items.REDSTONE, 'D', Items.IRON_INGOT, 'E',
                ioBus);

        JSONRecipeBuilder.addShapedRecipe(addonRedstoneReactor, "CAC", "ABD", "CAC", 'A', Items.IRON_INGOT,
                'B', Items.ENDER_EYE, 'C', Items.QUARTZ, 'D',
                ioBus);

        JSONRecipeBuilder.addShapedRecipe(addonFakeDrops, "CAC", "ABD", "CAC", 'A', Blocks.LAPIS_BLOCK,
                'B', Items.ENDER_EYE, 'C', Items.QUARTZ, 'D',
                ioBus);

        JSONRecipeBuilder.addShapedRecipe(addonDamageAmp, "AAA", "B B", "AAA", 'A', Items.IRON_INGOT, 'B',
                Items.ENDER_PEARL);

        JSONRecipeBuilder.addShapedRecipe(addonRecycler, "ABA", "BCD", "ABA", 'A', "ingotGold", 'B',
                Items.MAGMA_CREAM, 'C', Blocks.ENDER_CHEST,
                'D', ioBus);

        JSONRecipeBuilder.addShapedRecipe(addonConcealer, "ABA", "BCD", "ABA", 'A', Items.IRON_INGOT, 'B',
                Items.QUARTZ, 'C', Blocks.CHEST,
                'D', ioBus);

        // Integration
        if (ModCompatibility.ThaumcraftLoaded && OMTConfigHandler.shouldDoThaumcraftIntegration) {
            //ThaumcraftRecipeHandler.init();
        }

        if ((ComputerCraftLoaded || OpenComputersLoaded) && OMTConfigHandler.shouldDoThaumcraftIntegration) {
            ComputerRecipeHandler.init();
        }

        // Upgrades
        JSONRecipeBuilder.addShapedRecipe(upgradeEfficiency, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B', Items.ENDER_EYE, 'C',
                ioBus);

        JSONRecipeBuilder.addShapedRecipe(upgradeAccuracy, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B', "ingotGold", 'C', ioBus);

        JSONRecipeBuilder.addShapedRecipe(upgradeFireRate, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B', Items.BLAZE_POWDER, 'C',
                ioBus);

        JSONRecipeBuilder.addShapedRecipe(upgradeRange, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B',
                Items.DIAMOND, 'C', ioBus);

        JSONRecipeBuilder.addShapedRecipe(upgradeScatterShot, " A ", "ABA", " C ", 'A',
                Items.QUARTZ, 'B', Items.FLINT, 'C',
                ioBus);

        //Ammo
        JSONRecipeBuilder.addShapedRecipe(ammoBlazingClay, "BCB", "CAC", "BCB", 'A',
                Items.BLAZE_POWDER, 'B', Items.CLAY_BALL, 'C',
                Items.REDSTONE);

        //Other
        JSONRecipeBuilder.addShapedRecipe(ioBus, " A ", "BBB", " C ", 'A', "ingotGold", 'B',
                Items.REDSTONE, 'C', Items.IRON_INGOT);

        JSONRecipeBuilder.addShapedRecipe(memoryCard, "BAB", "CEC", "FDF", 'A', "ingotGold", 'B',
                Items.REDSTONE, 'C', Items.IRON_INGOT, 'D', OMTRecipeHandler.ioBus, 'E', Items.PAPER,
                'F', new ItemStack(Items.DYE, 1, 4));


        JSONRecipeBuilder.generateConstants();

    }
}
