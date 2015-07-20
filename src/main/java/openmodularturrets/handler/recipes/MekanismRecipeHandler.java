package openmodularturrets.handler.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

public class MekanismRecipeHandler {

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
        controlCircuit = GameRegistry.findItemStack(Mek, "ControlCircuit", 1);


        // Items
        // Barrels
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierTwoItem, 1), new Object[]{"CAC", " B ",
                "CAC", 'A', ironEnriched, 'B', Items.barrelTierOneItem, 'C', "ingotTin"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierThreeItem, 1), new Object[]{"CAC", " B ",
                "CAC", 'A', alloyEnriched, 'B', Items.barrelTierTwoItem, 'C', "ingotSteel"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierFourItem, 1), new Object[]{"CAC", " B ",
                "CAC", 'A', alloyReinforced, 'B', Items.barrelTierThreeItem, 'C', "ingotRefinedGlowstone"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierFiveItem, 1), new Object[]{"CAC", " B ",
                "CAC", 'A', alloyAtomic, 'B',
                Items.barrelTierFourItem, 'C', "ingotRefinedObsidian"}));

        // Chambers
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierTwoItem, 1), new Object[]{"ACA", " BC",
                "ACA", 'A', ironEnriched, 'B', Items.chamberTierOneItem, 'C',
                "ingotTin"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierThreeItem, 1), new Object[]{"ACA", " BC",
                "ACA", 'A', alloyEnriched, 'B', Items.chamberTierTwoItem, 'C',
                "ingotSteel"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierFourItem, 1), new Object[]{"ACA", " BC",
                "ACA", 'A', alloyReinforced, 'B',
                Items.chamberTierThreeItem, 'C', "ingotRefinedGlowstone"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierFiveItem, 1), new Object[]{"ACA", " BC",
                "ACA", 'A', alloyAtomic, 'B',
                Items.chamberTierFourItem, 'C', "ingotRefinedObsidian"}));

        // Sensors
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierTwoItem, 1), new Object[]{" A ", "DBD",
                " C ", 'A', ironEnriched, 'B', Items.sensorTierOneItem, 'C',
                Items.ioBus, 'D', controlCircuit}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierThreeItem, 1), new Object[]{" A ",
                "DBD", " C ", 'A', alloyEnriched, 'B', Items.sensorTierTwoItem, 'C',
                Items.ioBus, 'D', controlCircuit}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierFourItem, 1), new Object[]{" A ",
                "DBD", " C ", 'B', Items.sensorTierThreeItem, 'C', Items.ioBus, 'D',
                net.minecraft.init.Items.diamond, 'A', alloyReinforced, 'D', controlCircuit}));

        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Items.sensorTierFiveItem, 1), new Object[]{" A ",
                        "DBD", " C ", 'B', Items.sensorTierFourItem, 'C',
                        Items.ioBus, 'A',
                        alloyAtomic, 'D', controlCircuit}));

        // Bases
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.turretBaseTierTwo, 1), new Object[]{"ABA",
                        "ECE", "ADA", 'A', ironEnriched, 'B', energyTablet, 'C',
                        Items.sensorTierTwoItem, 'D', "ingotTin", 'E',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierThree, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyEnriched, 'B',
                        energyTablet, 'C', Items.sensorTierThreeItem, 'D',
                        "ingotSteel", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierFour, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyReinforced, 'B',
                        energyTablet, 'C', Items.sensorTierFourItem, 'D',
                        "ingotRefinedGlowstone", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierFive, 1), new Object[]{"ABA", "ECE",
                "ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                energyTablet, 'C', Items.sensorTierFiveItem, 'D',
                "ingotRefinedObsidian", 'E', Items.ioBus}));


        //Power Expanders
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.expanderPowerTierTwo, 1), new Object[]{"ABA",
                        "ECE", "ADA", 'A', ironEnriched, 'B', energyTablet, 'C',
                        net.minecraft.init.Blocks.redstone_block, 'D', "ingotTin", 'E',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderPowerTierThree, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyEnriched, 'B',
                        energyTablet, 'C', net.minecraft.init.Blocks.redstone_block, 'D',
                        "ingotSteel", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderPowerTierFour, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyReinforced, 'B',
                        energyTablet, 'C', net.minecraft.init.Blocks.redstone_block, 'D',
                        "ingotRefinedGlowstone", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderPowerTierFive, 1), new Object[]{"ABA", "ECE",
                "ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                energyTablet, 'C', net.minecraft.init.Blocks.redstone_block, 'D',
                "ingotRefinedObsidian", 'E', Items.ioBus}));

        //Inventory Expanders
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.expanderInvTierTwo, 1), new Object[]{"ABA",
                        "ECE", "ADA", 'A', ironEnriched, 'B', energyTablet, 'C',
                        net.minecraft.init.Blocks.chest, 'D', "ingotTin", 'E',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderInvTierThree, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyEnriched, 'B',
                        energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D',
                        "ingotSteel", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderInvTierFour, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyReinforced, 'B',
                        energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D',
                        "ingotRefinedGlowstone", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.expanderInvTierFive, 1), new Object[]{"ABA", "ECE",
                "ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D',
                "ingotRefinedObsidian", 'E', Items.ioBus}));

        // Turrets
        if (ConfigHandler.getMachineGunTurretSettings().isEnabled()) {
            GameRegistry
                    .addRecipe(new ShapedOreRecipe(new ItemStack(
                            Blocks.machineGunTurret, 1), new Object[]{" A ",
                            "CAC", "DBD", 'A', Items.barrelTierTwoItem, 'B',
                            Items.chamberTierTwoItem, 'C', "ingotTin", 'D',
                            Items.ioBus}));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry
                    .addRecipe(new ShapedOreRecipe(new ItemStack(
                            Blocks.incendiaryTurret, 1), new Object[]{"A A",
                            "BCB", "DCD", 'A', Items.barrelTierTwoItem, 'B',
                            Items.chamberTierTwoItem, 'C', "ingotTin", 'D',
                            Items.ioBus}));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.grenadeLauncherTurret, 1),
                    new Object[]{" A ", "CBC", "CDC", 'A',
                            Items.barrelTierThreeItem, 'B',
                            Items.chamberTierThreeItem, 'C', "ingotSteel", 'D',
                            Items.ioBus}));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.relativisticTurret, 1),
                    new Object[]{"CAC", "ABA", "CDC", 'A',
                            net.minecraft.init.Items.ender_pearl, 'B',
                            Items.sensorTierThreeItem, 'C', "ingotSteel", 'D',
                            Items.ioBus}));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.rocketTurret, 1), new Object[]{"CAC", "CAC", "EDE",
                    'A', Items.barrelTierFourItem, 'B', Items.chamberTierFourItem,
                    'C', "ingotRefinedGlowstone", 'D', Items.ioBus, 'E',
                    net.minecraft.init.Items.diamond}));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.teleporterTurret, 1), new Object[]{"CEC", "ABA",
                    "CDC", 'A', net.minecraft.init.Items.diamond, 'B',
                    Items.sensorTierFourItem, 'C',
                    net.minecraft.init.Items.ender_eye, 'D', Items.ioBus, 'E',
                    net.minecraft.init.Items.diamond}));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.laserTurret, 1), new Object[]{"EAE", "CBC", "DCD",
                    'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
                    'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus, 'E',
                    "ingotRefinedObsidian"}));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                    Blocks.railGunTurret, 1), new Object[]{"EAE", "CAC", "DBD",
                    'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
                    'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus, 'E',
                    "ingotRefinedObsidian"}));
        }

        // Ammo
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.rocketCraftable, 32), new Object[]{" A ", "ABA", "ACA",
                'A', "ingotTin", 'B', net.minecraft.init.Items.gunpowder, 'C',
                net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.bulletCraftable, 64), new Object[]{" A ", " B ", " C ",
                'A', "ingotOsmium", 'B', net.minecraft.init.Items.gunpowder, 'C',
                "ingotIron"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.grenadeCraftable, 32), new Object[]{" C ", "ABA",
                " A ", 'A', "ingotIron", 'B',
                net.minecraft.init.Items.gunpowder, 'C',
                net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.ferroSlug, 16), new Object[]{" C ", "CBC", " A ", 'A',
                alloyEnriched, 'B', net.minecraft.init.Items.flint, 'C',
                net.minecraft.init.Items.redstone}));
    }
}
