package openmodularturrets.handler.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

class VanillaRecipeHandler {

    public static void init() {
        // Items
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierTwoItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotIron",
                        'B',
                        Items.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierThreeItem, 1),
                        "AAA",
                        " B ",
                        "AAA",
                        'A',
                        "ingotGold",
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
                        net.minecraft.init.Items.quartz));

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
                        net.minecraft.init.Items.glowstone_dust));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierTwoItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotIron",
                        'B',
                        Items.chamberTierOneItem,
                        'C',
                        net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierThreeItem, 1),
                        "AAA",
                        " BC",
                        "AAA",
                        'A',
                        "ingotGold",
                        'B',
                        Items.chamberTierTwoItem,
                        'C',
                        Items.ioBus));

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
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.quartz));

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
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.quartz));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierTwoItem, 1),
                        " A ",
                        "ABA",
                        " C ",
                        'A',
                        "ingotIron",
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
                        "ingotGold",
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
                        'A',
                        "ingotGold",
                        'B',
                        Items.sensorTierThreeItem,
                        'C',
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.diamond,
                        'E',
                        net.minecraft.init.Items.quartz));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierFiveItem, 1),
                        "EDE",
                        "CBC",
                        "EDE",
                        'A',
                        "ingotGold",
                        'B',
                        Items.sensorTierFourItem,
                        'C',
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.glowstone_dust,
                        'E',
                        net.minecraft.init.Blocks.obsidian));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierTwo, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotIron",
                        'B',
                        Blocks.turretBaseTierOne,
                        'C',
                        Items.sensorTierTwoItem,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierThree, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotGold",
                        'B',
                        Blocks.turretBaseTierTwo,
                        'C',
                        Items.sensorTierThreeItem,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFour, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Items.diamond,
                        'B',
                        Blocks.turretBaseTierThree,
                        'C',
                        Items.sensorTierFourItem,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFive, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        Blocks.turretBaseTierFour,
                        'C',
                        Items.sensorTierFiveItem,
                        'D',
                        Items.ioBus));

        // Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierTwo, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotIron",
                        'B',
                        Blocks.expanderPowerTierOne,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierThree, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotGold",
                        'B',
                        Blocks.expanderPowerTierTwo,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFour, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Items.diamond,
                        'B',
                        Blocks.expanderPowerTierThree,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFive, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        Blocks.expanderPowerTierFour,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        Items.ioBus));

        // Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierTwo, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotIron",
                        'B',
                        Blocks.expanderInvTierOne,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierThree, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        "ingotGold",
                        'B',
                        Blocks.expanderInvTierTwo,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFour, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Items.diamond,
                        'B',
                        Blocks.expanderInvTierThree,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFive, 1),
                        "ABA",
                        "DCD",
                        "ADA",
                        'A',
                        net.minecraft.init.Blocks.obsidian,
                        'B',
                        Blocks.expanderInvTierFour,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
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
                            "ingotIron",
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
                            "ingotIron",
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
                            "ingotGold",
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
                            "ingotGold",
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.rocketTurret, 1),
                            "CAC",
                            "ABA",
                            "EDE",
                            'A',
                            Items.barrelTierFourItem,
                            'B',
                            Items.chamberTierFourItem,
                            'C',
                            net.minecraft.init.Items.quartz,
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
                            net.minecraft.init.Items.quartz));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.laserTurret, 1),
                            " A ",
                            "CBC",
                            "DCD",
                            'A',
                            Items.barrelTierFiveItem,
                            'B',
                            Items.chamberTierFiveItem,
                            'C',
                            net.minecraft.init.Blocks.obsidian,
                            'D',
                            Items.ioBus));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(Blocks.railGunTurret, 1),
                            "CAC",
                            "CAC",
                            "DBD",
                            'A',
                            Items.barrelTierFiveItem,
                            'B',
                            Items.chamberTierFiveItem,
                            'C',
                            net.minecraft.init.Blocks.obsidian,
                            'D',
                            Items.ioBus));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ItemStack(Items.rocketCraftable, 32),
                " A ",
                "ABA",
                "ACA",
                'A',
                net.minecraft.init.Items.iron_ingot,
                'B',
                net.minecraft.init.Items.gunpowder,
                'C',
                net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(
                new ItemStack(Items.bulletCraftable, 64),
                " A ",
                "BC ",
                " A ",
                'A',
                net.minecraft.init.Items.iron_ingot,
                'B',
                net.minecraft.init.Items.gunpowder,
                'C',
                net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(
                new ItemStack(Items.grenadeCraftable, 32),
                " C ",
                "ABA",
                " A ",
                'A',
                net.minecraft.init.Items.iron_ingot,
                'B',
                net.minecraft.init.Items.gunpowder,
                'C',
                net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(
                new ItemStack(Items.ferroSlug, 16),
                " C ",
                "CBC",
                " A ",
                'A',
                net.minecraft.init.Items.iron_ingot,
                'B',
                net.minecraft.init.Items.flint,
                'C',
                net.minecraft.init.Items.redstone);
    }
}
