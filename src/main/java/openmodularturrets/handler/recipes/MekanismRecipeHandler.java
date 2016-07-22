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
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,11), "CAC", " B ", "CAC", 'A', ironEnriched,
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,10), 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,12), "CAC", " B ", "CAC", 'A',
                                    alloyEnriched, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'C', "ingotSteel"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,13), "CAC", " B ", "CAC", 'A',
                                                   alloyReinforced, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,12), 'C',
                                                   "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,14), "CAC", " B ", "CAC", 'A', alloyAtomic,
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'C', "ingotRefinedObsidian"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,6), "ACA", " BC", "ACA", 'A', ironEnriched,
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,7), "ACA", " BC", "ACA", 'A',
                                    alloyEnriched, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotSteel"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,8), "ACA", " BC", "ACA", 'A',
                                    alloyReinforced, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C', "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,9), "ACA", " BC", "ACA", 'A', alloyAtomic,
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C', "ingotRefinedObsidian"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,1), " A ", "DBD", " C ", 'A', ironEnriched,
                                    'B', new ItemStack(ModItems.intermediateProductTiered,1,0) , 'C', ModItems.ioBus, 'D', controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,2), " A ", "DBD", " C ", 'A',
                                    alloyEnriched, 'B',new ItemStack(ModItems.intermediateProductTiered, 1 ,1), 'C', ModItems.ioBus, 'D',
                                    controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,3), " A ", "DBD", " C ", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.Items.diamond, 'A', alloyReinforced, 'D',
                                                   controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,4), " A ", "DBD", " C ", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C', ModItems.ioBus, 'A', alloyAtomic, 'D',
                                                   controlCircuit));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "ECE", "ADA", 'A', ironEnriched,
                                    'B', energyTablet, 'C',new ItemStack(ModItems.intermediateProductTiered, 1 ,1), 'D', "ingotTin", 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "ECE", "ADA", 'A',
                                    alloyEnriched, 'B', energyTablet, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'D', "ingotSteel",
                                    'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "ECE", "ADA", 'A',
                                    alloyReinforced, 'B', energyTablet, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'D',
                                    "ingotRefinedGlowstone", 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', energyTablet, 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,4), 'D', "ingotRefinedObsidian", 'E', ModItems.ioBus));

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
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotTin", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotTin", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C', "ingotSteel",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.ender_pearl, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C',
                                        "ingotSteel", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                                       "ingotRefinedGlowstone", 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C',
                                        net.minecraft.init.Items.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotRefinedObsidian"));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
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
