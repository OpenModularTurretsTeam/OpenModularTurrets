package openmodularturrets.handler.recipes;


class VanillaRecipeHandler {
    public static void init() {
        // ModItems
        // Barrels
        /*GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,11), "AAA", " B ", "AAA", 'A', "ingotIron",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,10)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,12), "AAA", " B ", "AAA", 'A', "ingotGold",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,11)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,13), "CAC", " B ", "CAC", 'A',
                                                   net.minecraft.initBlocks.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,12),
                                                   'C', net.minecraft.initBlocks.Items.quartz));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,14), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.initBlocks.Blocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,13),
                                                   'C', net.minecraft.initBlocks.Items.glowstone_dust));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,6), "AAA", " BC", "AAA", 'A', "ingotIron",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C', net.minecraft.initBlocks.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,7), "AAA", " BC", "AAA", 'A', "ingotGold",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,8), "DAD", " BC", "DAD", 'A',
                                    net.minecraft.initBlocks.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C', ModItems.ioBus,
                                    'D', net.minecraft.initBlocks.Items.quartz));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,9), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                    ModItems.ioBus, 'D', net.minecraft.initBlocks.Items.quartz));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,1), " A ", "ABA", " C ", 'A', "ingotIron",
                                    'B'new ItemStack(ModItems.intermediateProductTiered, 1 ,0), 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,2), " C ", "ABA", " C ", 'A', "ingotGold",
                                    'B'new ItemStack(ModItems.intermediateProductTiered, 1 ,1, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,3), "EDE", "CBC", "EDE", 'A', "ingotGold",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C', ModItems.ioBus, 'D',
                                    net.minecraft.initBlocks.Items.diamond, 'E', net.minecraft.initBlocks.Items.quartz));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,4), "EDE", "CBC", "EDE", 'A', "ingotGold",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C', ModItems.ioBus, 'D',
                                    net.minecraft.initBlocks.Items.glowstone_dust, 'E', net.minecraft.initBlocks.Blocks.obsidian));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "DCD", "ADA", 'A', "ingotIron",
                                    'B', new ItemStack(ModBlocks.turretBase,1,1), 'C'new ItemStack(ModItems.intermediateProductTiered, 1 ,1, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "DCD", "ADA", 'A', "ingotGold",
                                    'B', new ItemStack(ModBlocks.turretBase,1,2), 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Items.diamond, 'B', new ItemStack(ModBlocks.turretBase,1,3), 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.obsidian, 'B', new ItemStack(ModBlocks.turretBase,1,4), 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,4), 'D', ModItems.ioBus));

        // Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotIron", 'B', ModBlocks.expanderPowerTierOne, 'C',
                                    net.minecraft.initBlocks.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotGold", 'B', ModBlocks.expanderPowerTierTwo, 'C',
                                    net.minecraft.initBlocks.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Items.diamond, 'B', ModBlocks.expanderPowerTierThree, 'C',
                                    net.minecraft.initBlocks.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.obsidian, 'B', ModBlocks.expanderPowerTierFour, 'C',
                                    net.minecraft.initBlocks.Blocks.redstone_block, 'D', ModItems.ioBus));

        // Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "DCD", "ADA", 'A', "ingotIron",
                                    'B', ModBlocks.expanderInvTierOne, 'C', net.minecraft.initBlocks.Blocks.chest, 'D',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotGold", 'B', ModBlocks.expanderInvTierTwo, 'C', net.minecraft.initBlocks.Blocks.chest,
                                    'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Items.diamond, 'B', ModBlocks.expanderInvTierThree, 'C',
                                    net.minecraft.initBlocks.Blocks.chest, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.initBlocks.Blocks.obsidian, 'B', ModBlocks.expanderInvTierFour, 'C',
                                    net.minecraft.initBlocks.Blocks.chest, 'D', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotIron", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotIron", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C', "ingotGold",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.initBlocks.Items.ender_pearl, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C',
                                        "ingotGold", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "ABA", "EDE", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                                       net.minecraft.initBlocks.Items.quartz, 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.initBlocks.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.initBlocks.Items.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C',
                                        net.minecraft.initBlocks.Items.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.initBlocks.Items.quartz));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), " A ", "CBC", "DCD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.initBlocks.Blocks.obsidian, 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "CAC", "CAC", "DBD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.initBlocks.Blocks.obsidian, 'D', ModItems.ioBus));
        }

        // Ammo
        GameRegistry.addRecipe(new ItemStack(ModItems.rocketCraftable, 32), " A ", "ABA", "ACA", 'A',
                               net.minecraft.initBlocks.Items.iron_ingot, 'B', net.minecraft.initBlocks.Items.gunpowder, 'C',
                               net.minecraft.initBlocks.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", "BC ", " A ", 'A',
                               net.minecraft.initBlocks.Items.iron_ingot, 'B', net.minecraft.initBlocks.Items.gunpowder, 'C',
                               net.minecraft.initBlocks.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A',
                               net.minecraft.initBlocks.Items.iron_ingot, 'B', net.minecraft.initBlocks.Items.gunpowder, 'C',
                               net.minecraft.initBlocks.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A',
                               net.minecraft.initBlocks.Items.iron_ingot, 'B', net.minecraft.initBlocks.Items.flint, 'C',
                               net.minecraft.initBlocks.Items.redstone);     */
    }
}
