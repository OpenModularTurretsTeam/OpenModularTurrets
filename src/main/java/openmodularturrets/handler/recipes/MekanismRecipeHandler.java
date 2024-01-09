package openmodularturrets.handler.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

class MekanismRecipeHandler {

    public static void init() {
        String Mek = "Mekanism";

        ItemStack ironEnriched;
        ItemStack alloyEnriched;
        ItemStack alloyReinforced;
        ItemStack alloyAtomic;
        ItemStack energyTablet;
        ItemStack controlCircuit;

        /* Items */
        ironEnriched = GameRegistry.findItemStack(Mek, "EnrichedIron", 1);
        alloyEnriched = GameRegistry.findItemStack(Mek, "EnrichedAlloy", 1);
        alloyReinforced = GameRegistry.findItemStack(Mek, "ReinforcedAlloy", 1);
        alloyAtomic = GameRegistry.findItemStack(Mek, "AtomicAlloy", 1);
        energyTablet = GameRegistry.findItemStack(Mek, "EnergyTablet", 1);
        energyTablet.setItemDamage(OreDictionary.WILDCARD_VALUE);
        controlCircuit = GameRegistry.findItemStack(Mek, "ControlCircuit", 1);

        // Items
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierTwoItem, 1),
                        "CAC",
                        " B ",
                        "CAC",
                        'A',
                        ironEnriched,
                        'B',
                        Items.barrelTierOneItem,
                        'C',
                        "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierThreeItem, 1),
                        "CAC",
                        " B ",
                        "CAC",
                        'A',
                        alloyEnriched,
                        'B',
                        Items.barrelTierTwoItem,
                        'C',
                        "ingotSteel"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierFourItem, 1),
                        "CAC",
                        " B ",
                        "CAC",
                        'A',
                        alloyReinforced,
                        'B',
                        Items.barrelTierThreeItem,
                        'C',
                        "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.barrelTierFiveItem, 1),
                        "CAC",
                        " B ",
                        "CAC",
                        'A',
                        alloyAtomic,
                        'B',
                        Items.barrelTierFourItem,
                        'C',
                        "ingotRefinedObsidian"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierTwoItem, 1),
                        "ACA",
                        " BC",
                        "ACA",
                        'A',
                        ironEnriched,
                        'B',
                        Items.chamberTierOneItem,
                        'C',
                        "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierThreeItem, 1),
                        "ACA",
                        " BC",
                        "ACA",
                        'A',
                        alloyEnriched,
                        'B',
                        Items.chamberTierTwoItem,
                        'C',
                        "ingotSteel"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierFourItem, 1),
                        "ACA",
                        " BC",
                        "ACA",
                        'A',
                        alloyReinforced,
                        'B',
                        Items.chamberTierThreeItem,
                        'C',
                        "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.chamberTierFiveItem, 1),
                        "ACA",
                        " BC",
                        "ACA",
                        'A',
                        alloyAtomic,
                        'B',
                        Items.chamberTierFourItem,
                        'C',
                        "ingotRefinedObsidian"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierTwoItem, 1),
                        " A ",
                        "DBD",
                        " C ",
                        'A',
                        ironEnriched,
                        'B',
                        Items.sensorTierOneItem,
                        'C',
                        Items.ioBus,
                        'D',
                        controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierThreeItem, 1),
                        " A ",
                        "DBD",
                        " C ",
                        'A',
                        alloyEnriched,
                        'B',
                        Items.sensorTierTwoItem,
                        'C',
                        Items.ioBus,
                        'D',
                        controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierFourItem, 1),
                        " A ",
                        "DBD",
                        " C ",
                        'B',
                        Items.sensorTierThreeItem,
                        'C',
                        Items.ioBus,
                        'D',
                        net.minecraft.init.Items.diamond,
                        'A',
                        alloyReinforced,
                        'D',
                        controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Items.sensorTierFiveItem, 1),
                        " A ",
                        "DBD",
                        " C ",
                        'B',
                        Items.sensorTierFourItem,
                        'C',
                        Items.ioBus,
                        'A',
                        alloyAtomic,
                        'D',
                        controlCircuit));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierTwo, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        ironEnriched,
                        'B',
                        energyTablet,
                        'C',
                        Items.sensorTierTwoItem,
                        'D',
                        "ingotTin",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyEnriched,
                        'B',
                        energyTablet,
                        'C',
                        Items.sensorTierThreeItem,
                        'D',
                        "ingotSteel",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.turretBaseTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyReinforced,
                        'B',
                        energyTablet,
                        'C',
                        Items.sensorTierFourItem,
                        'D',
                        "ingotRefinedGlowstone",
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
                        energyTablet,
                        'C',
                        Items.sensorTierFiveItem,
                        'D',
                        "ingotRefinedObsidian",
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
                        ironEnriched,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        "ingotTin",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyEnriched,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        "ingotSteel",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderPowerTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyReinforced,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        "ingotRefinedGlowstone",
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
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.redstone_block,
                        'D',
                        "ingotRefinedObsidian",
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
                        ironEnriched,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        "ingotTin",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierThree, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyEnriched,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        "ingotSteel",
                        'E',
                        Items.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(Blocks.expanderInvTierFour, 1),
                        "ABA",
                        "ECE",
                        "ADA",
                        'A',
                        alloyReinforced,
                        'B',
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        "ingotRefinedGlowstone",
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
                        energyTablet,
                        'C',
                        net.minecraft.init.Blocks.chest,
                        'D',
                        "ingotRefinedObsidian",
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
                            "ingotTin",
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
                            "ingotTin",
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
                            "ingotSteel",
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
                            "ingotSteel",
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
                            "ingotRefinedGlowstone",
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
                            "ingotRefinedObsidian"));
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
                            "ingotRefinedObsidian"));
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
                        "ingotOsmium",
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
                        alloyEnriched,
                        'B',
                        net.minecraft.init.Items.flint,
                        'C',
                        net.minecraft.init.Items.redstone));
    }
}
