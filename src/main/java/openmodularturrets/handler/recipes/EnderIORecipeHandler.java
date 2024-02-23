package openmodularturrets.handler.recipes;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

class EnderIORecipeHandler {

    public static void init() {
        ItemStack capacitorBank;
        ItemStack capacitorBankVibrant;
        ItemStack capacitorBankBasic;
        ItemStack basicCapacitor;
        ItemStack doubleCapacitor;
        ItemStack octadicCapacitor;
        ItemStack vibrantCrystal;

        /* Items */

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

        // Items
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierTwoItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        Items.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierThreeItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        Items.barrelTierTwoItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierFourItem, 1),
                        "CAC",
                        " B ",
                        "CAC",
                        'A',
                        net.minecraft.init.Items.diamond,
                        'B',
                        Items.barrelTierThreeItem,
                        'C',
                        "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierFiveItem, 1),
                        "AAA",
                        "CBC",
                        "AAA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        Items.barrelTierFourItem,
                        'C',
                        vibrantCrystal));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierTwoItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        Items.chamberTierOneItem,
                        'C',
                        basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierThreeItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        Items.chamberTierTwoItem,
                        'C',
                        basicCapacitor));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierFourItem, 1),
                        "DAD",
                        " BC",
                        "DAD",
                        'A',
                        net.minecraft.init.Items.diamond,
                        'B',
                        Items.chamberTierThreeItem,
                        'C',
                        doubleCapacitor,
                        'D',
                        "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierFiveItem, 1),
                        "ADA",
                        " BC",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        Items.chamberTierFourItem,
                        'C',
                        octadicCapacitor,
                        'D',
                        vibrantCrystal));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierTwoItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        Items.sensorTierOneItem,
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierThreeItem, 1),
                        " C ",
                        "ABA",
                        " C ",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        Items.sensorTierTwoItem,
                        'C',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierFourItem, 1),
                        "EDE",
                        "CBC",
                        "EDE",
                        'B',
                        Items.sensorTierThreeItem,
                        'C',
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.diamond,
                        'E',
                        "ingotSoularium"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierFiveItem, 1),
                        "EDE",
                        "CBC",
                        "EDE",
                        'B',
                        Items.sensorTierFourItem,
                        'C',
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Blocks.obsidian,
                        'E',
                        vibrantCrystal));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierTwo, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        capacitorBankBasic,
                        'C',
                        Items.sensorTierTwoItem,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        capacitorBank,
                        'C',
                        Items.sensorTierThreeItem,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotSoularium",
                        'B',
                        capacitorBankVibrant,
                        'C',
                        Items.sensorTierFourItem,
                        'D',
                        doubleCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFive, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        capacitorBankVibrant,
                        'C',
                        Items.sensorTierFiveItem,
                        'D',
                        octadicCapacitor,
                        'E',
                        Items.ioBus));

        // Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierTwo, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        capacitorBank,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        capacitorBank,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotSoularium",
                        'B',
                        capacitorBankVibrant,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        doubleCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFive, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        capacitorBankVibrant,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        octadicCapacitor,
                        'E',
                        Items.ioBus));

        // Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierTwo, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        capacitorBank,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        capacitorBank,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        basicCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotSoularium",
                        'B',
                        capacitorBankVibrant,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        doubleCapacitor,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFive, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        capacitorBankVibrant,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        octadicCapacitor,
                        'E',
                        Items.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.machineGunTurret, 1),
                            " A ",
                            "CAC",
                            "DBD",
                            'A',
                            Items.barrelTierTwoItem,
                            'B',
                            Items.chamberTierTwoItem,
                            'C',
                            "ingotElectricalSteel",
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.incendiaryTurret, 1),
                            "A A",
                            "BCB",
                            "DCD",
                            'A',
                            Items.barrelTierTwoItem,
                            'B',
                            Items.chamberTierTwoItem,
                            'C',
                            "ingotElectricalSteel",
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.grenadeLauncherTurret, 1),
                            " A ",
                            "CBC",
                            "CDC",
                            'A',
                            Items.barrelTierThreeItem,
                            'B',
                            Items.chamberTierThreeItem,
                            'C',
                            "ingotDarkSteel",
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.relativisticTurret, 1),
                            "CAC",
                            "ABA",
                            "CDC",
                            'A',
                            net.minecraft.init.Items.ender_pearl,
                            'B',
                            Items.sensorTierThreeItem,
                            'C',
                            "ingotDarkSteel",
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.rocketTurret, 1),
                            "CAC",
                            "CAC",
                            "EDE",
                            'A',
                            Items.barrelTierFourItem,
                            'B',
                            Items.chamberTierFourItem,
                            'C',
                            "ingotSoularium",
                            'D',
                            Items.ioBus,
                            'E',
                            net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.teleporterTurret, 1),
                            "CEC",
                            "ABA",
                            "CDC",
                            'A',
                            net.minecraft.init.Items.diamond,
                            'B',
                            Items.sensorTierFourItem,
                            'C',
                            net.minecraft.init.Items.ender_eye,
                            'D',
                            Items.ioBus,
                            'E',
                            net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.laserTurret, 1),
                            "EAE",
                            "CBC",
                            "DCD",
                            'A',
                            Items.barrelTierFiveItem,
                            'B',
                            Items.chamberTierFiveItem,
                            'C',
                            net.minecraft.init.Blocks.obsidian,
                            'D',
                            Items.ioBus,
                            'E',
                            vibrantCrystal));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.railGunTurret, 1),
                            "EAE",
                            "CAC",
                            "DBD",
                            'A',
                            Items.barrelTierFiveItem,
                            'B',
                            Items.chamberTierFiveItem,
                            'C',
                            net.minecraft.init.Blocks.obsidian,
                            'D',
                            Items.ioBus,
                            'E',
                            vibrantCrystal));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.rocketCraftable, 64),
                        " A ",
                        "ABA",
                        "ACA",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        net.minecraft.init.Items.gunpowder,
                        'C',
                        net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.bulletCraftable, 64),
                        " A ",
                        " B ",
                        " C ",
                        'A',
                        "ingotElectricalSteel",
                        'B',
                        net.minecraft.init.Items.gunpowder,
                        'C',
                        "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.grenadeCraftable, 32),
                        " C ",
                        "ABA",
                        " A ",
                        'A',
                        "ingotIron",
                        'B',
                        net.minecraft.init.Items.gunpowder,
                        'C',
                        net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.ferroSlug, 16),
                        " C ",
                        "CBC",
                        " A ",
                        'A',
                        "ingotDarkSteel",
                        'B',
                        net.minecraft.init.Items.flint,
                        'C',
                        net.minecraft.init.Items.redstone));
    }
}
