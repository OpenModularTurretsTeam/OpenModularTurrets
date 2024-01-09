package openmodularturrets.handler.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

class ThermalExpansionRecipeHandler {

    public static void init() {
        String TE = "ThermalExpansion";

        ItemStack powerCoilGold;
        ItemStack powerCoilSilver;
        ItemStack cellResonant;
        ItemStack cellReinforced;
        ItemStack cellHardened;
        ItemStack cellBasic;

        /* Blocks */
        cellResonant = GameRegistry.findItemStack(TE, "cellResonant", 1);
        cellReinforced = GameRegistry.findItemStack(TE, "cellReinforced", 1);
        cellHardened = GameRegistry.findItemStack(TE, "cellHardened", 1);
        cellBasic = GameRegistry.findItemStack(TE, "cellBasic", 1);

        /* Items */
        powerCoilGold = GameRegistry.findItemStack(TE, "powerCoilGold", 1);
        powerCoilSilver = GameRegistry.findItemStack(TE, "powerCoilSilver", 1);

        // Items
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierTwoItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotLead",
                        'B',
                        Items.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierThreeItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotInvar",
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
                        "ingotElectrum"));

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
                        "ingotEnderium"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierTwoItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotLead",
                        'B',
                        Items.chamberTierOneItem,
                        'C',
                        powerCoilSilver));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierThreeItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotInvar",
                        'B',
                        Items.chamberTierTwoItem,
                        'C',
                        powerCoilSilver));

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
                        powerCoilSilver,
                        'D',
                        "ingotElectrum"));

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
                        powerCoilSilver,
                        'D',
                        "ingotEnderium"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierTwoItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        "ingotLead",
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
                        "ingotInvar",
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
                        "ingotElectrum"));

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
                        "ingotEnderium"));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierTwo, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotLead",
                        'B',
                        cellBasic,
                        'C',
                        Items.sensorTierTwoItem,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotInvar",
                        'B',
                        cellHardened,
                        'C',
                        Items.sensorTierThreeItem,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectrum",
                        'B',
                        cellReinforced,
                        'C',
                        Items.sensorTierFourItem,
                        'D',
                        powerCoilGold,
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
                        cellResonant,
                        'C',
                        Items.sensorTierFiveItem,
                        'D',
                        powerCoilGold,
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
                        "ingotLead",
                        'B',
                        cellBasic,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotInvar",
                        'B',
                        cellHardened,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectrum",
                        'B',
                        cellReinforced,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        powerCoilGold,
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
                        cellResonant,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        powerCoilGold,
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
                        "ingotLead",
                        'B',
                        cellBasic,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotInvar",
                        'B',
                        cellHardened,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        powerCoilGold,
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        "ingotElectrum",
                        'B',
                        cellReinforced,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        powerCoilGold,
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
                        cellResonant,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        powerCoilGold,
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
                            "ingotLead",
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
                            "ingotLead",
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
                            "ingotInvar",
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
                            "ingotInvar",
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
                            "ingotElectrum",
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
                            "ingotEnderium"));
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
                            "ingotEnderium"));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.rocketCraftable, 32),
                        " A ",
                        "ABA",
                        "ACA",
                        'A',
                        "ingotTin",
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
                        "ingotLead",
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
                        "ingotInvar",
                        'B',
                        net.minecraft.init.Items.flint,
                        'C',
                        net.minecraft.init.Items.redstone));
    }
}
