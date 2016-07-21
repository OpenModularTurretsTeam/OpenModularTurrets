package openmodularturrets.handler.recipes;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.ModBlocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.ModItems;

class MekanismRecipeHandler {
    public static void init() {
        String Mek = "Mekanism";

        ItemStack ironEnriched;
        ItemStack alloyEnriched;
        ItemStack alloyReinforced;
        ItemStack alloyAtomic;
        ItemStack energyTablet;
        ItemStack controlCircuit;


		/* ModItems */
        ironEnriched = new ItemStack(GameRegistry.findItem(Mek, "EnrichedIron"),1);
        alloyEnriched = new ItemStack(GameRegistry.findItem(Mek, "EnrichedAlloy"),1);
        alloyReinforced = new ItemStack(GameRegistry.findItem(Mek, "ReinforcedAlloy"),1);
        alloyAtomic = new ItemStack(GameRegistry.findItem(Mek, "AtomicAlloy"),1);
        energyTablet = new ItemStack(GameRegistry.findItem(Mek, "EnergyTablet"),1);
        energyTablet.setItemDamage(OreDictionary.WILDCARD_VALUE);
        controlCircuit = new ItemStack(GameRegistry.findItem(Mek, "ControlCircuit"),1);

        // ModItems
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierTwoItem, 1), "CAC", " B ", "CAC", 'A', ironEnriched,
                                    'B', ModItems.barrelTierOneItem, 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierThreeItem, 1), "CAC", " B ", "CAC", 'A',
                                    alloyEnriched, 'B', ModItems.barrelTierTwoItem, 'C', "ingotSteel"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFourItem, 1), "CAC", " B ", "CAC", 'A',
                                                   alloyReinforced, 'B', ModItems.barrelTierThreeItem, 'C',
                                                   "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFiveItem, 1), "CAC", " B ", "CAC", 'A', alloyAtomic,
                                    'B', ModItems.barrelTierFourItem, 'C', "ingotRefinedObsidian"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierTwoItem, 1), "ACA", " BC", "ACA", 'A', ironEnriched,
                                    'B', ModItems.chamberTierOneItem, 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierThreeItem, 1), "ACA", " BC", "ACA", 'A',
                                    alloyEnriched, 'B', ModItems.chamberTierTwoItem, 'C', "ingotSteel"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFourItem, 1), "ACA", " BC", "ACA", 'A',
                                    alloyReinforced, 'B', ModItems.chamberTierThreeItem, 'C', "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFiveItem, 1), "ACA", " BC", "ACA", 'A', alloyAtomic,
                                    'B', ModItems.chamberTierFourItem, 'C', "ingotRefinedObsidian"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierTwoItem, 1), " A ", "DBD", " C ", 'A', ironEnriched,
                                    'B', ModItems.sensorTierOneItem, 'C', ModItems.ioBus, 'D', controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierThreeItem, 1), " A ", "DBD", " C ", 'A',
                                    alloyEnriched, 'B', ModItems.sensorTierTwoItem, 'C', ModItems.ioBus, 'D',
                                    controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFourItem, 1), " A ", "DBD", " C ", 'B',
                                                   ModItems.sensorTierThreeItem, 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.Items.diamond, 'A', alloyReinforced, 'D',
                                                   controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFiveItem, 1), " A ", "DBD", " C ", 'B',
                                                   ModItems.sensorTierFourItem, 'C', ModItems.ioBus, 'A', alloyAtomic, 'D',
                                                   controlCircuit));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "ECE", "ADA", 'A', ironEnriched,
                                    'B', energyTablet, 'C', ModItems.sensorTierTwoItem, 'D', "ingotTin", 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "ECE", "ADA", 'A',
                                    alloyEnriched, 'B', energyTablet, 'C', ModItems.sensorTierThreeItem, 'D', "ingotSteel",
                                    'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "ECE", "ADA", 'A',
                                    alloyReinforced, 'B', energyTablet, 'C', ModItems.sensorTierFourItem, 'D',
                                    "ingotRefinedGlowstone", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', energyTablet, 'C',
                                    ModItems.sensorTierFiveItem, 'D', "ingotRefinedObsidian", 'E', ModItems.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "ECE", "ADA", 'A',
                                    ironEnriched, 'B', energyTablet, 'C', net.minecraft.init.Blocks.redstone_block, 'D',
                                    "ingotTin", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    alloyEnriched, 'B', energyTablet, 'C', net.minecraft.init.Blocks.redstone_block,
                                    'D', "ingotSteel", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    alloyReinforced, 'B', energyTablet, 'C', net.minecraft.init.Blocks.redstone_block,
                                    'D', "ingotRefinedGlowstone", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', energyTablet, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', "ingotRefinedObsidian", 'E',
                                    ModItems.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "ECE", "ADA", 'A', ironEnriched,
                                    'B', energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D', "ingotTin", 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    alloyEnriched, 'B', energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D',
                                    "ingotSteel", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    alloyReinforced, 'B', energyTablet, 'C', net.minecraft.init.Blocks.chest, 'D',
                                    "ingotRefinedGlowstone", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', energyTablet, 'C',
                                    net.minecraft.init.Blocks.chest, 'D', "ingotRefinedObsidian", 'E', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotTin", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotTin", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        ModItems.barrelTierThreeItem, 'B', ModItems.chamberTierThreeItem, 'C', "ingotSteel",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.ender_pearl, 'B', ModItems.sensorTierThreeItem, 'C',
                                        "ingotSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       ModItems.barrelTierFourItem, 'B', ModItems.chamberTierFourItem, 'C',
                                                       "ingotRefinedGlowstone", 'D', ModItems.ioBus, 'E',
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
                                                       "ingotRefinedObsidian"));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotRefinedObsidian"));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.rocketCraftable, 32), " A ", "ABA", "ACA", 'A', "ingotTin", 'B',
                                    net.minecraft.init.Items.gunpowder, 'C', net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", " B ", " C ", 'A', "ingotOsmium",
                                    'B', net.minecraft.init.Items.gunpowder, 'C', "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A', "ingotIron",
                                    'B', net.minecraft.init.Items.gunpowder, 'C', net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A', alloyEnriched, 'B',
                                    net.minecraft.init.Items.flint, 'C', net.minecraft.init.Items.redstone));
    }
}
