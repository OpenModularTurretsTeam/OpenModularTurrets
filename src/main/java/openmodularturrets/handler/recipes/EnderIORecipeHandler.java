package openmodularturrets.handler.recipes;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.init.ModItems;

class EnderIORecipeHandler {
    public static void init() {
        ItemStack capacitorBank;
        ItemStack capacitorBankVibrant;
        ItemStack capacitorBankBasic;
        ItemStack basicCapacitor;
        ItemStack doubleCapacitor;
        ItemStack octadicCapacitor;
        ItemStack vibrantCrystal;

		/* ModItems */

        Block capBankBlock = GameRegistry.findBlock("EnderIO", "blockCapBank");
        capacitorBank = new ItemStack(capBankBlock, 1, 2);
        capacitorBankVibrant = new ItemStack(capBankBlock, 1, 3);
        capacitorBankBasic = new ItemStack(capBankBlock, 1, 1);

        Item capacitorItem = GameRegistry.findItem("EnderIO", "itemBasicCapacitor");
        basicCapacitor = new ItemStack(capacitorItem, 1, 0);
        doubleCapacitor = new ItemStack(capacitorItem, 1, 1);
        octadicCapacitor = new ItemStack(capacitorItem, 1, 2);

        Item materialsItem = GameRegistry.findItem("EnderIO", "itemMaterial");
        vibrantCrystal = new ItemStack(materialsItem, 1, 6);

        // ModItems
        // Barrels
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,11), "AAA", " B ", "AAA", 'A',
                                                   "ingotElectricalSteel", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,10)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,12), "AAA", " B ", "AAA", 'A',
                                    "ingotDarkSteel", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,11)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,13), "CAC", " B ", "CAC", 'A',
                                                   Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,12),
                                                   'C', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,14), "AAA", "CBC", "AAA", 'A',
                                                   Blocks.OBSIDIAN, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,13),
                                                   'C', vibrantCrystal));

        // Chambers
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,6), "AAA", " BC", "AAA", 'A',
                                                   "ingotElectricalSteel", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C',
                                                   basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,7), "AAA", " BC", "AAA", 'A',
                                    "ingotDarkSteel", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,8), "DAD", " BC", "DAD", 'A',
                                    Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C',
                                    doubleCapacitor, 'D', "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,9), "ADA", " BC", "ADA", 'A',
                                    Blocks.OBSIDIAN, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                    octadicCapacitor, 'D', vibrantCrystal));

        // Sensors
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,1), " A ", "ABA", " C ", 'A',
                                                   "ingotElectricalSteel", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,0), 'C',
                                                   RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,2), " C ", "ABA", " C ", 'A',
                                    "ingotDarkSteel", 'B',new ItemStack(ModItems.intermediateProductTiered, 1 ,1), 'C', RecipeHandler.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,3), "EDE", "CBC", "EDE", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C', RecipeHandler.ioBus, 'D',
                                                   Items.DIAMOND, 'E', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,4), "EDE", "CBC", "EDE", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C', RecipeHandler.ioBus, 'D',
                                                   Blocks.OBSIDIAN, 'E', vibrantCrystal));

        // Bases
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "ECE", "ADA", 'A',
                                                   "ingotElectricalSteel", 'B', capacitorBankBasic, 'C',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,1), 'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'D',
                                    basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'D',
                                    doubleCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "ECE", "ADA", 'A',
                                    Blocks.OBSIDIAN, 'B', capacitorBankVibrant, 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,4), 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierTwo, "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C',
                                    Blocks.REDSTONE_BLOCK, 'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierThree, "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', Blocks.REDSTONE_BLOCK,
                                    'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFour, "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C',
                                    Blocks.REDSTONE_BLOCK, 'D', doubleCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFive, "ABA", "ECE", "ADA", 'A',
                                    Blocks.OBSIDIAN, 'B', capacitorBankVibrant, 'C',
                                    Blocks.REDSTONE_BLOCK, 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierTwo, "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C', Blocks.CHEST,
                                    'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierThree, "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', Blocks.CHEST, 'D',
                                    basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFour, "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C', Blocks.CHEST,
                                    'D', doubleCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFive, "ABA", "ECE", "ADA", 'A',
                                    Blocks.OBSIDIAN, 'B', capacitorBankVibrant, 'C',
                                    Blocks.CHEST, 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C',
                                        "ingotElectricalSteel", 'D', RecipeHandler.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C',
                                        "ingotElectricalSteel", 'D', RecipeHandler.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C',
                                        "ingotDarkSteel", 'D', RecipeHandler.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        Items.ENDER_PEARL, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C',
                                        "ingotDarkSteel", 'D', RecipeHandler.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                                       "ingotSoularium", 'D', RecipeHandler.ioBus, 'E',
                                                       Items.DIAMOND));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C',
                                        Items.ENDER_EYE, 'D', RecipeHandler.ioBus, 'E',
                                        Items.DIAMOND));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus, 'E',
                                                       vibrantCrystal));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus, 'E',
                                                       vibrantCrystal));
        }

        // Ammo
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.rocketCraftable, 64), " A ", "ABA", "ACA", 'A',
                                                   "ingotElectricalSteel", 'B', Items.GUNPOWDER, 'C',
                                                   Items.REDSTONE));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", " B ", " C ", 'A',
                                                   "ingotElectricalSteel", 'B', Items.GUNPOWDER, 'C',
                                                   "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A', "ingotIron",
                                    'B', Items.GUNPOWDER, 'C', Items.REDSTONE));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A', "ingotDarkSteel", 'B',
                                    Items.FLINT, 'C', Items.REDSTONE));
    }
}
