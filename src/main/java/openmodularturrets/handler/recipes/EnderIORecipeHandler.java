package openmodularturrets.handler.recipes;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.ModBlocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.ModItems;

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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierTwoItem, 1), "AAA", " B ", "AAA", 'A',
                                                   "ingotElectricalSteel", 'B', ModItems.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierThreeItem, 1), "AAA", " B ", "AAA", 'A',
                                    "ingotDarkSteel", 'B', ModItems.barrelTierTwoItem));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFourItem, 1), "CAC", " B ", "CAC", 'A',
                                                   net.minecraft.init.Items.diamond, 'B', ModItems.barrelTierThreeItem,
                                                   'C', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFiveItem, 1), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.init.Blocks.obsidian, 'B', ModItems.barrelTierFourItem,
                                                   'C', vibrantCrystal));

        // Chambers
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.chamberTierTwoItem, 1), "AAA", " BC", "AAA", 'A',
                                                   "ingotElectricalSteel", 'B', ModItems.chamberTierOneItem, 'C',
                                                   basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierThreeItem, 1), "AAA", " BC", "AAA", 'A',
                                    "ingotDarkSteel", 'B', ModItems.chamberTierTwoItem, 'C', basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFourItem, 1), "DAD", " BC", "DAD", 'A',
                                    net.minecraft.init.Items.diamond, 'B', ModItems.chamberTierThreeItem, 'C',
                                    doubleCapacitor, 'D', "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFiveItem, 1), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', ModItems.chamberTierFourItem, 'C',
                                    octadicCapacitor, 'D', vibrantCrystal));

        // Sensors
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierTwoItem, 1), " A ", "ABA", " C ", 'A',
                                                   "ingotElectricalSteel", 'B', ModItems.sensorTierOneItem, 'C',
                                                   ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierThreeItem, 1), " C ", "ABA", " C ", 'A',
                                    "ingotDarkSteel", 'B', ModItems.sensorTierTwoItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFourItem, 1), "EDE", "CBC", "EDE", 'B',
                                                   ModItems.sensorTierThreeItem, 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.Items.diamond, 'E', "ingotSoularium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFiveItem, 1), "EDE", "CBC", "EDE", 'B',
                                                   ModItems.sensorTierFourItem, 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.Blocks.obsidian, 'E', vibrantCrystal));

        // Bases
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "ECE", "ADA", 'A',
                                                   "ingotElectricalSteel", 'B', capacitorBankBasic, 'C',
                                                   ModItems.sensorTierTwoItem, 'D', basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', ModItems.sensorTierThreeItem, 'D',
                                    basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C', ModItems.sensorTierFourItem, 'D',
                                    doubleCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    ModItems.sensorTierFiveItem, 'D', octadicCapacitor, 'E', ModItems.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.redstone_block,
                                    'D', basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', doubleCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', octadicCapacitor, 'E', ModItems.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectricalSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.chest,
                                    'D', basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotDarkSteel", 'B', capacitorBank, 'C', net.minecraft.init.Blocks.chest, 'D',
                                    basicCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotSoularium", 'B', capacitorBankVibrant, 'C', net.minecraft.init.Blocks.chest,
                                    'D', doubleCapacitor, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', capacitorBankVibrant, 'C',
                                    net.minecraft.init.Blocks.chest, 'D', octadicCapacitor, 'E', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C',
                                        "ingotElectricalSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C',
                                        "ingotElectricalSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        ModItems.barrelTierThreeItem, 'B', ModItems.chamberTierThreeItem, 'C',
                                        "ingotDarkSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.ender_pearl, 'B', ModItems.sensorTierThreeItem, 'C',
                                        "ingotDarkSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       ModItems.barrelTierFourItem, 'B', ModItems.chamberTierFourItem, 'C',
                                                       "ingotSoularium", 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.diamond, 'B', ModItems.sensorTierFourItem, 'C',
                                        net.minecraft.init.Items.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       vibrantCrystal));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus, 'E',
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
