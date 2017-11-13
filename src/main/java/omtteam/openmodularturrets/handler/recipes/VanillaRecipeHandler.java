package omtteam.openmodularturrets.handler.recipes;


import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import omtteam.omlib.util.JSONRecipeBuilder;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.init.ModItems;

import static omtteam.openmodularturrets.handler.recipes.RecipeHandler.*;


class VanillaRecipeHandler {
    public static void init() {
        // ModItems
        // Barrels
        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 11), "AAA", " B ", "AAA", 'A', "ingotIron",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 10));

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 12), "AAA", " B ", "AAA", 'A', "ingotGold",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 11));

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 13), "CAC", " B ", "CAC", 'A',
                Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 12),
                'C', Items.QUARTZ);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 14), "AAA", "CBC", "AAA", 'A',
                Blocks.OBSIDIAN, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 13),
                'C', Items.GLOWSTONE_DUST);

        // Chambers
        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 6), "AAA", " BC", "AAA", 'A', "ingotIron",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 5), 'C', Items.REDSTONE);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 7), "AAA", " BC", "AAA", 'A', "ingotGold",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 8), "DAD", " BC", "DAD", 'A',
                        Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 7), 'C', RecipeHandler.ioBus,
                        'D', Items.QUARTZ);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 9), "ADA", " BC", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 8), 'C',
                        RecipeHandler.ioBus, 'D', Items.QUARTZ);

        // Sensors
        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 1), " A ", "ABA", " C ", 'A', "ingotIron",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 0), 'C', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 2), " C ", "ABA", " C ", 'A', "ingotGold",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 1), 'C', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 3), "EDE", "CBC", "EDE", 'A', "ingotGold",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'C', RecipeHandler.ioBus, 'D',
                        Items.DIAMOND, 'E', Items.QUARTZ);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModItems.intermediateProductTiered, 1, 4), "EDE", "CBC", "EDE", 'A', "ingotGold",
                        'B', new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'C', RecipeHandler.ioBus, 'D',
                        Items.GLOWSTONE_DUST, 'E', Blocks.OBSIDIAN);

        // Bases
        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.turretBase, 1, 1), "ABA", "DCD", "ADA", 'A', "ingotIron",
                        'B', new ItemStack(ModBlocks.turretBase, 1, 0), 'C', new ItemStack(ModItems.intermediateProductTiered, 1, 1), 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.turretBase, 1, 2), "ABA", "DCD", "ADA", 'A', "ingotGold",
                        'B', new ItemStack(ModBlocks.turretBase, 1, 1), 'C', new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.turretBase, 1, 3), "ABA", "DCD", "ADA", 'A',
                        Items.DIAMOND, 'B', new ItemStack(ModBlocks.turretBase, 1, 2), 'C',
                        new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.turretBase, 1, 4), "ABA", "DCD", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', new ItemStack(ModBlocks.turretBase, 1, 3), 'C',
                        new ItemStack(ModItems.intermediateProductTiered, 1, 4), 'D', RecipeHandler.ioBus);

        // Power Expanders
        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderPowerTierTwo, "ABA", "DCD", "ADA", 'A',
                        "ingotIron", 'B', RecipeHandler.expanderPowerTierOne, 'C',
                        Blocks.REDSTONE_BLOCK, 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderPowerTierThree, "ABA", "DCD", "ADA", 'A',
                        "ingotGold", 'B', RecipeHandler.expanderPowerTierTwo, 'C',
                        Blocks.REDSTONE_BLOCK, 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderPowerTierFour, "ABA", "DCD", "ADA", 'A',
                        Items.DIAMOND, 'B', RecipeHandler.expanderPowerTierThree, 'C',
                        Blocks.REDSTONE_BLOCK, 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderPowerTierFive, "ABA", "DCD", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', RecipeHandler.expanderPowerTierFour, 'C',
                        Blocks.REDSTONE_BLOCK, 'D', RecipeHandler.ioBus);

        // Inventory Expanders
        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderInvTierTwo, "ABA", "DCD", "ADA", 'A', "ingotIron",
                        'B', RecipeHandler.expanderInvTierOne, 'C', Blocks.CHEST, 'D',
                        RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderInvTierThree, "ABA", "DCD", "ADA", 'A',
                        "ingotGold", 'B', RecipeHandler.expanderInvTierTwo, 'C', Blocks.CHEST,
                        'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderInvTierFour, "ABA", "DCD", "ADA", 'A',
                        Items.DIAMOND, 'B', RecipeHandler.expanderInvTierThree, 'C',
                        Blocks.CHEST, 'D', RecipeHandler.ioBus);

        JSONRecipeBuilder.addShapedRecipe(RecipeHandler.expanderInvTierFive, "ABA", "DCD", "ADA", 'A',
                        Blocks.OBSIDIAN, 'B', RecipeHandler.expanderInvTierFour, 'C',
                        Blocks.CHEST, 'D', RecipeHandler.ioBus);

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', "ingotIron", 'D',
                            RecipeHandler.ioBus);
        }

        if (ConfigHandler.getIncendiaryTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 6), 'C', "ingotIron", 'D',
                            RecipeHandler.ioBus);
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                            new ItemStack(ModItems.intermediateProductTiered, 1, 12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 7), 'C', "ingotGold",
                            'D', RecipeHandler.ioBus);
        }

        if (ConfigHandler.getRelativisticTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                            Items.ENDER_PEARL, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 2), 'C',
                            "ingotGold", 'D', RecipeHandler.ioBus);
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "ABA", "EDE", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 8), 'C',
                    Items.QUARTZ, 'D', RecipeHandler.ioBus, 'E',
                    Items.DIAMOND);
        }

        if (ConfigHandler.getTeleporterTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                            Items.DIAMOND, 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 3), 'C',
                            Items.ENDER_EYE, 'D', RecipeHandler.ioBus, 'E',
                            Items.QUARTZ);
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.laserTurret, 1), " A ", "CBC", "DCD", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 9), 'C',
                    Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus);
        }

        if (ConfigHandler.getRailgunTurretSettings().isEnabled()) {
            JSONRecipeBuilder.addShapedRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "CAC", "CAC", "DBD", 'A',
                    new ItemStack(ModItems.intermediateProductTiered, 1, 14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1, 9), 'C',
                    Blocks.OBSIDIAN, 'D', RecipeHandler.ioBus);
        }

        // Ammo
        JSONRecipeBuilder.addShapedRecipe(ammoRocket, " A ", "ABA", "ACA", 'A',
                Items.IRON_INGOT, 'B', Items.GUNPOWDER, 'C',
                Items.REDSTONE);

        JSONRecipeBuilder.addShapedRecipe(ammoBullet, " A ", "BC ", " A ", 'A',
                Items.IRON_INGOT, 'B', Items.GUNPOWDER, 'C',
                Items.REDSTONE);

        JSONRecipeBuilder.addShapedRecipe(ammoGrenade, " C ", "ABA", " A ", 'A',
                Items.IRON_INGOT, 'B', Items.GUNPOWDER, 'C',
                Items.REDSTONE);

        JSONRecipeBuilder.addShapedRecipe(ammoFerroSlug, " C ", "CBC", " A ", 'A',
                Items.IRON_INGOT, 'B', Items.FLINT, 'C',
                Items.REDSTONE);
    }
}
