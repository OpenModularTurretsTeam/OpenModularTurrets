package openmodularturrets.handler.recipes;


class ThermalExpansionRecipeHandler {
    /*public static void initBlocks() {
        String TE = "ThermalExpansion";

        ItemStack powerCoilGold;
        ItemStack powerCoilSilver;
        ItemStack cellResonant;
        ItemStack cellReinforced;
        ItemStack cellHardened;
        ItemStack cellBasic;

		// ModBlocks
        cellResonant = GameRegistry.findItemStack(TE, "cellResonant", 1);
        cellReinforced = GameRegistry.findItemStack(TE, "cellReinforced", 1);
        cellHardened = GameRegistry.findItemStack(TE, "cellHardened", 1);
        cellBasic = GameRegistry.findItemStack(TE, "cellBasic", 1);

		// ModItems
        powerCoilGold = GameRegistry.findItemStack(TE, "powerCoilGold", 1);
        powerCoilSilver = GameRegistry.findItemStack(TE, "powerCoilSilver", 1);

        // ModItems
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,11), "AAA", " B ", "AAA", 'A', "ingotLead",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,10)));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,12), "AAA", " B ", "AAA", 'A', "ingotInvar",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,11)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,13), "CAC", " B ", "CAC", 'A',
                                                   net.minecraft.initBlocks.ModItems.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,12),
                                                   'C', "ingotElectrum"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,14), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.initBlocks.ModBlocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,13),
                                                   'C', "ingotEnderium"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,6), "AAA", " BC", "AAA", 'A', "ingotLead",
                                    'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,5), 'C', powerCoilSilver));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,7), "AAA", " BC", "AAA", 'A',
                                    "ingotInvar", 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', powerCoilSilver));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,8), "DAD", " BC", "DAD", 'A',
                                    net.minecraft.initBlocks.ModItems.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C',
                                    powerCoilSilver, 'D', "ingotElectrum"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,9), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.initBlocks.ModBlocks.obsidian, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                    powerCoilSilver, 'D', "ingotEnderium"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,1), " A ", "ABA", " C ", 'A', "ingotLead",
                                    'B'new ItemStack(ModItems.intermediateProductTiered, 1 ,0), 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,2), " C ", "ABA", " C ", 'A', "ingotInvar",
                                    'B'new ItemStack(ModItems.intermediateProductTiered, 1 ,1, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,3), "EDE", "CBC", "EDE", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.initBlocks.ModItems.diamond, 'E', "ingotElectrum"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.intermediateProductTiered, 1 ,4), "EDE", "CBC", "EDE", 'B',
                                                   new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.initBlocks.ModBlocks.obsidian, 'E', "ingotEnderium"));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierTwo, 1), "ABA", "ECE", "ADA", 'A', "ingotLead",
                                    'B', cellBasic, 'C'new ItemStack(ModItems.intermediateProductTiered, 1 ,1, 'D', powerCoilGold, 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'D', powerCoilGold,
                                    'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.initBlocks.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    new ItemStack(ModItems.intermediateProductTiered, 1 ,4), 'D', powerCoilGold, 'E', ModItems.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotLead", 'B', cellBasic, 'C', net.minecraft.initBlocks.ModBlocks.redstone_block, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', net.minecraft.initBlocks.ModBlocks.redstone_block, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', net.minecraft.initBlocks.ModBlocks.redstone_block,
                                    'D', powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.initBlocks.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    net.minecraft.initBlocks.ModBlocks.redstone_block, 'D', powerCoilGold, 'E', ModItems.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "ECE", "ADA", 'A', "ingotLead",
                                    'B', cellBasic, 'C', net.minecraft.initBlocks.ModBlocks.chest, 'D', powerCoilGold, 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', net.minecraft.initBlocks.ModBlocks.chest, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', net.minecraft.initBlocks.ModBlocks.chest, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.initBlocks.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    net.minecraft.initBlocks.ModBlocks.chest, 'D', powerCoilGold, 'E', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotLead", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,11), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,6), 'C', "ingotLead", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        new ItemStack(ModItems.intermediateProductTiered, 1 ,12), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,7), 'C', "ingotInvar",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.initBlocks.ModItems.ender_pearl, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,2), 'C',
                                        "ingotInvar", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,13), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,8), 'C',
                                                       "ingotElectrum", 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.initBlocks.ModItems.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.initBlocks.ModItems.diamond, 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,3), 'C',
                                        net.minecraft.initBlocks.ModItems.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.initBlocks.ModItems.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.initBlocks.ModBlocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotEnderium"));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       new ItemStack(ModItems.intermediateProductTiered, 1 ,14), 'B', new ItemStack(ModItems.intermediateProductTiered, 1 ,9), 'C',
                                                       net.minecraft.initBlocks.ModBlocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotEnderium"));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.rocketCraftable, 32), " A ", "ABA", "ACA", 'A', "ingotTin", 'B',
                                    net.minecraft.initBlocks.ModItems.gunpowder, 'C', net.minecraft.initBlocks.ModItems.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", " B ", " C ", 'A', "ingotLead",
                                    'B', net.minecraft.initBlocks.ModItems.gunpowder, 'C', "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A', "ingotIron",
                                    'B', net.minecraft.initBlocks.ModItems.gunpowder, 'C', net.minecraft.initBlocks.ModItems.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A', "ingotInvar", 'B',
                                    net.minecraft.initBlocks.ModItems.flint, 'C', net.minecraft.initBlocks.ModItems.redstone));
    }     */
}
