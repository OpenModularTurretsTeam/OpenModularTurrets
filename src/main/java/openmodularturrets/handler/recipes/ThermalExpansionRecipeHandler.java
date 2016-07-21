package openmodularturrets.handler.recipes;


class ThermalExpansionRecipeHandler {
    /*public static void init() {
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
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierTwoItem, 1), "AAA", " B ", "AAA", 'A', "ingotLead",
                                    'B', ModItems.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierThreeItem, 1), "AAA", " B ", "AAA", 'A', "ingotInvar",
                                    'B', ModItems.barrelTierTwoItem));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFourItem, 1), "CAC", " B ", "CAC", 'A',
                                                   net.minecraft.init.ModItems.diamond, 'B', ModItems.barrelTierThreeItem,
                                                   'C', "ingotElectrum"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFiveItem, 1), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.init.ModBlocks.obsidian, 'B', ModItems.barrelTierFourItem,
                                                   'C', "ingotEnderium"));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierTwoItem, 1), "AAA", " BC", "AAA", 'A', "ingotLead",
                                    'B', ModItems.chamberTierOneItem, 'C', powerCoilSilver));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierThreeItem, 1), "AAA", " BC", "AAA", 'A',
                                    "ingotInvar", 'B', ModItems.chamberTierTwoItem, 'C', powerCoilSilver));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFourItem, 1), "DAD", " BC", "DAD", 'A',
                                    net.minecraft.init.ModItems.diamond, 'B', ModItems.chamberTierThreeItem, 'C',
                                    powerCoilSilver, 'D', "ingotElectrum"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFiveItem, 1), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.init.ModBlocks.obsidian, 'B', ModItems.chamberTierFourItem, 'C',
                                    powerCoilSilver, 'D', "ingotEnderium"));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierTwoItem, 1), " A ", "ABA", " C ", 'A', "ingotLead",
                                    'B', ModItems.sensorTierOneItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierThreeItem, 1), " C ", "ABA", " C ", 'A', "ingotInvar",
                                    'B', ModItems.sensorTierTwoItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFourItem, 1), "EDE", "CBC", "EDE", 'B',
                                                   ModItems.sensorTierThreeItem, 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.ModItems.diamond, 'E', "ingotElectrum"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFiveItem, 1), "EDE", "CBC", "EDE", 'B',
                                                   ModItems.sensorTierFourItem, 'C', ModItems.ioBus, 'D',
                                                   net.minecraft.init.ModBlocks.obsidian, 'E', "ingotEnderium"));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierTwo, 1), "ABA", "ECE", "ADA", 'A', "ingotLead",
                                    'B', cellBasic, 'C', ModItems.sensorTierTwoItem, 'D', powerCoilGold, 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', ModItems.sensorTierThreeItem, 'D', powerCoilGold,
                                    'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', ModItems.sensorTierFourItem, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBaseTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    ModItems.sensorTierFiveItem, 'D', powerCoilGold, 'E', ModItems.ioBus));

        //Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotLead", 'B', cellBasic, 'C', net.minecraft.init.ModBlocks.redstone_block, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', net.minecraft.init.ModBlocks.redstone_block, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', net.minecraft.init.ModBlocks.redstone_block,
                                    'D', powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    net.minecraft.init.ModBlocks.redstone_block, 'D', powerCoilGold, 'E', ModItems.ioBus));

        //Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "ECE", "ADA", 'A', "ingotLead",
                                    'B', cellBasic, 'C', net.minecraft.init.ModBlocks.chest, 'D', powerCoilGold, 'E',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotInvar", 'B', cellHardened, 'C', net.minecraft.init.ModBlocks.chest, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "ECE", "ADA", 'A',
                                    "ingotElectrum", 'B', cellReinforced, 'C', net.minecraft.init.ModBlocks.chest, 'D',
                                    powerCoilGold, 'E', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "ECE", "ADA", 'A',
                                    net.minecraft.init.ModBlocks.obsidian, 'B', cellResonant, 'C',
                                    net.minecraft.init.ModBlocks.chest, 'D', powerCoilGold, 'E', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotLead", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotLead", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        ModItems.barrelTierThreeItem, 'B', ModItems.chamberTierThreeItem, 'C', "ingotInvar",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.init.ModItems.ender_pearl, 'B', ModItems.sensorTierThreeItem, 'C',
                                        "ingotInvar", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "CAC", "EDE", 'A',
                                                       ModItems.barrelTierFourItem, 'B', ModItems.chamberTierFourItem, 'C',
                                                       "ingotElectrum", 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.init.ModItems.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.init.ModItems.diamond, 'B', ModItems.sensorTierFourItem, 'C',
                                        net.minecraft.init.ModItems.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.init.ModItems.diamond));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), "EAE", "CBC", "DCD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.ModBlocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotEnderium"));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "EAE", "CAC", "DBD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.ModBlocks.obsidian, 'D', ModItems.ioBus, 'E',
                                                       "ingotEnderium"));
        }

        // Ammo
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.rocketCraftable, 32), " A ", "ABA", "ACA", 'A', "ingotTin", 'B',
                                    net.minecraft.init.ModItems.gunpowder, 'C', net.minecraft.init.ModItems.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", " B ", " C ", 'A', "ingotLead",
                                    'B', net.minecraft.init.ModItems.gunpowder, 'C', "ingotIron"));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A', "ingotIron",
                                    'B', net.minecraft.init.ModItems.gunpowder, 'C', net.minecraft.init.ModItems.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A', "ingotInvar", 'B',
                                    net.minecraft.init.ModItems.flint, 'C', net.minecraft.init.ModItems.redstone));
    }     */
}
