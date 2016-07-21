package openmodularturrets.handler.recipes;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.ModBlocks;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.ModItems;

class VanillaRecipeHandler {
    public static void init() {
        // ModItems
        // Barrels
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierTwoItem, 1), "AAA", " B ", "AAA", 'A', "ingotIron",
                                    'B', ModItems.barrelTierOneItem));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.barrelTierThreeItem, 1), "AAA", " B ", "AAA", 'A', "ingotGold",
                                    'B', ModItems.barrelTierTwoItem));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFourItem, 1), "CAC", " B ", "CAC", 'A',
                                                   net.minecraft.init.Items.diamond, 'B', ModItems.barrelTierThreeItem,
                                                   'C', net.minecraft.init.Items.quartz));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.barrelTierFiveItem, 1), "AAA", "CBC", "AAA", 'A',
                                                   net.minecraft.init.Blocks.obsidian, 'B', ModItems.barrelTierFourItem,
                                                   'C', net.minecraft.init.Items.glowstone_dust));

        // Chambers
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierTwoItem, 1), "AAA", " BC", "AAA", 'A', "ingotIron",
                                    'B', ModItems.chamberTierOneItem, 'C', net.minecraft.init.Items.redstone));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierThreeItem, 1), "AAA", " BC", "AAA", 'A', "ingotGold",
                                    'B', ModItems.chamberTierTwoItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFourItem, 1), "DAD", " BC", "DAD", 'A',
                                    net.minecraft.init.Items.diamond, 'B', ModItems.chamberTierThreeItem, 'C', ModItems.ioBus,
                                    'D', net.minecraft.init.Items.quartz));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.chamberTierFiveItem, 1), "ADA", " BC", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', ModItems.chamberTierFourItem, 'C',
                                    ModItems.ioBus, 'D', net.minecraft.init.Items.quartz));

        // Sensors
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierTwoItem, 1), " A ", "ABA", " C ", 'A', "ingotIron",
                                    'B', ModItems.sensorTierOneItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierThreeItem, 1), " C ", "ABA", " C ", 'A', "ingotGold",
                                    'B', ModItems.sensorTierTwoItem, 'C', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFourItem, 1), "EDE", "CBC", "EDE", 'A', "ingotGold",
                                    'B', ModItems.sensorTierThreeItem, 'C', ModItems.ioBus, 'D',
                                    net.minecraft.init.Items.diamond, 'E', net.minecraft.init.Items.quartz));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModItems.sensorTierFiveItem, 1), "EDE", "CBC", "EDE", 'A', "ingotGold",
                                    'B', ModItems.sensorTierFourItem, 'C', ModItems.ioBus, 'D',
                                    net.minecraft.init.Items.glowstone_dust, 'E', net.minecraft.init.Blocks.obsidian));

        // Bases
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,2), "ABA", "DCD", "ADA", 'A', "ingotIron",
                                    'B', new ItemStack(ModBlocks.turretBase,1,1), 'C', ModItems.sensorTierTwoItem, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,3), "ABA", "DCD", "ADA", 'A', "ingotGold",
                                    'B', new ItemStack(ModBlocks.turretBase,1,2), 'C', ModItems.sensorTierThreeItem, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,4), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Items.diamond, 'B', new ItemStack(ModBlocks.turretBase,1,3), 'C',
                                    ModItems.sensorTierFourItem, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.turretBase, 1,5), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', new ItemStack(ModBlocks.turretBase,1,4), 'C',
                                    ModItems.sensorTierFiveItem, 'D', ModItems.ioBus));

        // Power Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierTwo, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotIron", 'B', ModBlocks.expanderPowerTierOne, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierThree, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotGold", 'B', ModBlocks.expanderPowerTierTwo, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFour, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Items.diamond, 'B', ModBlocks.expanderPowerTierThree, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderPowerTierFive, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', ModBlocks.expanderPowerTierFour, 'C',
                                    net.minecraft.init.Blocks.redstone_block, 'D', ModItems.ioBus));

        // Inventory Expanders
        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierTwo, 1), "ABA", "DCD", "ADA", 'A', "ingotIron",
                                    'B', ModBlocks.expanderInvTierOne, 'C', net.minecraft.init.Blocks.chest, 'D',
                                    ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierThree, 1), "ABA", "DCD", "ADA", 'A',
                                    "ingotGold", 'B', ModBlocks.expanderInvTierTwo, 'C', net.minecraft.init.Blocks.chest,
                                    'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFour, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Items.diamond, 'B', ModBlocks.expanderInvTierThree, 'C',
                                    net.minecraft.init.Blocks.chest, 'D', ModItems.ioBus));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(ModBlocks.expanderInvTierFive, 1), "ABA", "DCD", "ADA", 'A',
                                    net.minecraft.init.Blocks.obsidian, 'B', ModBlocks.expanderInvTierFour, 'C',
                                    net.minecraft.init.Blocks.chest, 'D', ModItems.ioBus));

        // Turrets
        if (ConfigHandler.getGunTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.machineGunTurret, 1), " A ", "CAC", "DBD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotIron", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getIncendiary_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.incendiaryTurret, 1), "A A", "BCB", "DCD", 'A',
                                        ModItems.barrelTierTwoItem, 'B', ModItems.chamberTierTwoItem, 'C', "ingotIron", 'D',
                                        ModItems.ioBus));
        }

        if (ConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.grenadeLauncherTurret, 1), " A ", "CBC", "CDC", 'A',
                                        ModItems.barrelTierThreeItem, 'B', ModItems.chamberTierThreeItem, 'C', "ingotGold",
                                        'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRelativistic_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.relativisticTurret, 1), "CAC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.ender_pearl, 'B', ModItems.sensorTierThreeItem, 'C',
                                        "ingotGold", 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRocketTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.rocketTurret, 1), "CAC", "ABA", "EDE", 'A',
                                                       ModItems.barrelTierFourItem, 'B', ModItems.chamberTierFourItem, 'C',
                                                       net.minecraft.init.Items.quartz, 'D', ModItems.ioBus, 'E',
                                                       net.minecraft.init.Items.diamond));
        }

        if (ConfigHandler.getTeleporter_turret().isEnabled()) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(new ItemStack(ModBlocks.teleporterTurret, 1), "CEC", "ABA", "CDC", 'A',
                                        net.minecraft.init.Items.diamond, 'B', ModItems.sensorTierFourItem, 'C',
                                        net.minecraft.init.Items.ender_eye, 'D', ModItems.ioBus, 'E',
                                        net.minecraft.init.Items.quartz));
        }

        if (ConfigHandler.getLaserTurretSettings().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.laserTurret, 1), " A ", "CBC", "DCD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus));
        }

        if (ConfigHandler.getRailgun_turret().isEnabled()) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.railGunTurret, 1), "CAC", "CAC", "DBD", 'A',
                                                       ModItems.barrelTierFiveItem, 'B', ModItems.chamberTierFiveItem, 'C',
                                                       net.minecraft.init.Blocks.obsidian, 'D', ModItems.ioBus));
        }

        // Ammo
        GameRegistry.addRecipe(new ItemStack(ModItems.rocketCraftable, 32), " A ", "ABA", "ACA", 'A',
                               net.minecraft.init.Items.iron_ingot, 'B', net.minecraft.init.Items.gunpowder, 'C',
                               net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.bulletCraftable, 64), " A ", "BC ", " A ", 'A',
                               net.minecraft.init.Items.iron_ingot, 'B', net.minecraft.init.Items.gunpowder, 'C',
                               net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.grenadeCraftable, 32), " C ", "ABA", " A ", 'A',
                               net.minecraft.init.Items.iron_ingot, 'B', net.minecraft.init.Items.gunpowder, 'C',
                               net.minecraft.init.Items.redstone);

        GameRegistry.addRecipe(new ItemStack(ModItems.ferroSlug, 16), " C ", "CBC", " A ", 'A',
                               net.minecraft.init.Items.iron_ingot, 'B', net.minecraft.init.Items.flint, 'C',
                               net.minecraft.init.Items.redstone);
    }
}
