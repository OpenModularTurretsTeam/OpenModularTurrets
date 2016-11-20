package openmodularturrets.handler.recipes;


import net.minecraft.block.Block;
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
                                                   net.minecraft.init.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,12),
                                                   'C', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,14), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.init.Blocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,13),
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
                                    net.minecraft.init.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C',
                                    doubleCapacitor, 'D', "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,9), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
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
                                                   net.minecraft.init.Items.diamond, 'E', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,4), "EDE", "CBC", "EDE", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C', RecipeHandler.ioBus, 'D',
                                                   net.minecraft.init.Blocks.obsidian, 'E', vibrantCrystal));

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
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,4), 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierTwo, "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierThree, "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.redstone_block,
                                    'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFour, "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', doubleCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFive, "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierTwo, "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.chest,
                                    'D', basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierThree, "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.chest, 'D',
                                    basicCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFour, "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C', net.minecraft.init.Blocks.chest,
                                    'D', doubleCapacitor, 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFive, "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.chest, 'D', octadicCapacitor, 'E', RecipeHandler.ioBus));

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
                                        net.minecraft.init.Items.ender_pearl, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C',
                                        "ingotDarkSteel", 'D', RecipeHandler.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                                       "ingotSoularium", 'D', RecipeHandler.ioBus, 'E',
                                                       net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C',
                                        net.minecraft.init.Items.ender_eye, 'D', RecipeHandler.ioBus, 'E',
                                        net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', RecipeHandler.ioBus, 'E',
                                                       vibrantCrystal));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', RecipeHandler.ioBus, 'E',
                                                       vibrantCrystal));
        }

        // Ammo
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.rocketCraftable, 64), " A ", "ABA", "ACA", 'A',
                                                   "ingotElectricalSteel", 'B', net.minecraft.init.Items.gunpowder, 'C',
                                                   net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", " B ", " C ", 'A',
                                                   "ingotElectricalSteel", 'B', net.minecraft.init.Items.gunpowder, 'C',
                                                   "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A', "ingotIron",
                                    'B', net.minecraft.init.Items.gunpowder, 'C', net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A', "ingotDarkSteel", 'B',
                                    net.minecraft.init.Items.flint, 'C', net.minecraft.init.Items.redstone));
    }
}
