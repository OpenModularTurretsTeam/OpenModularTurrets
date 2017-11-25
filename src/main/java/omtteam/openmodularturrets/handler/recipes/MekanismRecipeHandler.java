package omtteam.openmodularturrets.handler.recipes;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;


class MekanismRecipeHandler {
    private static void postErrorMessage() {
        OpenModularTurrets.getLogger().info("Error while initializing Mekanism recipes, please report to OMT devs!");
    }

    public static void init() {
        String Mek = "Mekanism";

        Item ironEnrichedItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "EnrichedIron"));
        Item alloyEnrichedItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "EnrichedAlloy"));
        Item alloyReinforcedItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "ReinforcedAlloy"));
        Item alloyAtomicItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "AtomicAlloy"));
        Item energyTabletItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "EnergyTablet"));
        Item controlCircuitItem = Item.REGISTRY.getObject(new ResourceLocation(Mek, "ControlCircuit"));
        ItemStack ironEnriched;
        ItemStack alloyEnriched;
        ItemStack alloyReinforced;
        ItemStack alloyAtomic;
        ItemStack energyTablet;
        ItemStack controlCircuit;


		/* ModItems */
        if (ironEnrichedItem != null) {
            ironEnriched = new ItemStack(ironEnrichedItem, 1);
        } else {
            postErrorMessage();
            return;
        }
        if (alloyEnrichedItem != null) {
            alloyEnriched = new ItemStack(alloyEnrichedItem, 1);
        } else {
            postErrorMessage();
            return;
        }
        if (alloyReinforcedItem != null) {
            alloyReinforced = new ItemStack(alloyReinforcedItem, 1);
        } else {
            postErrorMessage();
            return;
        }
        if (alloyAtomicItem != null) {
            alloyAtomic = new ItemStack(alloyAtomicItem, 1);
        } else {
            postErrorMessage();
            return;
        }
        if (energyTabletItem != null) {
            energyTablet = new ItemStack(energyTabletItem, 1);
            energyTablet.setItemDamage(OreDictionary.WILDCARD_VALUE);
        } else {
            postErrorMessage();
            return;
        }
        if (controlCircuitItem != null) {
            controlCircuit = new ItemStack(controlCircuitItem, 1);
        } else {
            postErrorMessage();
            return;
        }


        // ModItems
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 11), "CAC", " B ", "CAC", 'A', ironEnriched,
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 10), 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 12), "CAC", " B ", "CAC", 'A',
                        alloyEnriched, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 11), 'C', "ingotSteel"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 13), "CAC", " B ", "CAC", 'A',
                alloyReinforced, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 12), 'C',
                "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 14), "CAC", " B ", "CAC", 'A', alloyAtomic,
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 13), 'C', "ingotRefinedObsidian"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 6), "ACA", " BC", "ACA", 'A', ironEnriched,
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C', "ingotTin"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 7), "ACA", " BC", "ACA", 'A',
                        alloyEnriched, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', "ingotSteel"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 8), "ACA", " BC", "ACA", 'A',
                        alloyReinforced, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 7), 'C', "ingotRefinedGlowstone"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 9), "ACA", " BC", "ACA", 'A', alloyAtomic,
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 8), 'C', "ingotRefinedObsidian"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 1), " A ", "DBD", " C ", 'A', ironEnriched,
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 0), 'C', RecipeHandler.ioBus, 'D', controlCircuit));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 2), " A ", "DBD", " C ", 'A',
                        alloyEnriched, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 1), 'C', RecipeHandler.ioBus, 'D',
                        controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 3), " A ", "DBD", " C ", 'B',
                new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'C', RecipeHandler.ioBus, 'D',
                Items.DIAMOND, 'A', alloyReinforced, 'D',
                controlCircuit));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 4), " A ", "DBD", " C ", 'B',
                new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'C', RecipeHandler.ioBus, 'A', alloyAtomic, 'D',
                controlCircuit));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 1), "ABA", "ECE", "ADA", 'A', ironEnriched,
                        'B', energyTablet, 'C', new ItemStack(ModItems.intermediateProductTiered, 1, 1), 'D', "ingotTin", 'E',
                        RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 2), "ABA", "ECE", "ADA", 'A',
                        alloyEnriched, 'B', energyTablet, 'C', new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'D', "ingotSteel",
                        'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 3), "ABA", "ECE", "ADA", 'A',
                        alloyReinforced, 'B', energyTablet, 'C', new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'D',
                        "ingotRefinedGlowstone", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1, 4), "ABA", "ECE", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', energyTablet, 'C',
                        new ItemStack(ModItems.intermediateProductTiered, 1, 4), 'D', "ingotRefinedObsidian", 'E', RecipeHandler.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierTwo, "ABA", "ECE", "ADA", 'A',
                        ironEnriched, 'B', energyTablet, 'C', Blocks.REDSTONE_BLOCK, 'D',
                        "ingotTin", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierThree, "ABA", "ECE", "ADA", 'A',
                        alloyEnriched, 'B', energyTablet, 'C', Blocks.REDSTONE_BLOCK,
                        'D', "ingotSteel", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFour, "ABA", "ECE", "ADA", 'A',
                        alloyReinforced, 'B', energyTablet, 'C', Blocks.REDSTONE_BLOCK,
                        'D', "ingotRefinedGlowstone", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderPowerTierFive, "ABA", "ECE", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', energyTablet, 'C',
                        Blocks.REDSTONE_BLOCK, 'D', "ingotRefinedObsidian", 'E',
                        RecipeHandler.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierTwo, "ABA", "ECE", "ADA", 'A', ironEnriched,
                        'B', energyTablet, 'C', Blocks.CHEST, 'D', "ingotTin", 'E',
                        RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierThree, "ABA", "ECE", "ADA", 'A',
                        alloyEnriched, 'B', energyTablet, 'C', Blocks.CHEST, 'D',
                        "ingotSteel", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFour, "ABA", "ECE", "ADA", 'A',
                        alloyReinforced, 'B', energyTablet, 'C', Blocks.CHEST, 'D',
                        "ingotRefinedGlowstone", 'E', RecipeHandler.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.expanderInvTierFive, "ABA", "ECE", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', energyTablet, 'C',
                        Blocks.CHEST, 'D', "ingotRefinedObsidian", 'E', RecipeHandler.ioBus));

        // Turrets
        if (OMTConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', "ingotTin", 'D',
                            RecipeHandler.ioBus));
        }

        if (OMTConfigHandler.getIncendiaryTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', "ingotTin", 'D',
                            RecipeHandler.ioBus));
        }

        if (OMTConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 7), 'C', "ingotSteel",
                            'D', RecipeHandler.ioBus));
        }

        if (OMTConfigHandler.getRelativisticTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                            Items.ENDER_PEARL, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'C',
                            "ingotSteel", 'D', RecipeHandler.ioBus));
        }

        if (OMTConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CBC", "EDE", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 8), 'C',
                    "ingotRefinedGlowstone", 'D', RecipeHandler.ioBus, 'E',
                    Items.DIAMOND));
        }

        if (OMTConfigHandler.getTeleporterTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                            Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'C',
                            Items.ENDER_EYE, 'D', RecipeHandler.ioBus, 'E',
                            Items.DIAMOND));
        }

        if (OMTConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 9), 'C',
                    Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus, 'E',
                    "ingotRefinedObsidian"));
        }

        if (OMTConfigHandler.getRailgunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 9), 'C',
                    Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus, 'E',
                    "ingotRefinedObsidian"));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.ammoRocket, " A ", "ABA", "ACA", 'A', "ingotTin", 'B',
                        Items.GUNPOWDER, 'C', Items.REDSTONE));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.ammoBullet, " A ", " B ", " C ", 'A', "ingotOsmium",
                        'B', Items.GUNPOWDER, 'C', "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.ammoGrenade, " C ", "ABA", " A ", 'A', "ingotIron",
                        'B', Items.GUNPOWDER, 'C', Items.REDSTONE));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(RecipeHandler.ammoFerroSlug, " C ", "CBC", " A ", 'A', alloyEnriched, 'B',
                        Items.FLINT, 'C', Items.REDSTONE));
    }
}
