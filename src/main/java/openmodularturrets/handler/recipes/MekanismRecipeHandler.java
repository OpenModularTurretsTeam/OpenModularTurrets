package openmodularturrets.handler.recipes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;

public class MekanismRecipeHandler {

    public static void init() {

        String Mek = "Mekanism";

        ItemStack ironEnriched;
        ItemStack alloyEnriched;
        ItemStack alloyReinforced;
        ItemStack alloyAtomic;
        ItemStack energyTablet;


		/* Items */
        ironEnriched = GameRegistry.findItemStack(Mek, "EnrichedIron", 1);
        alloyEnriched = GameRegistry.findItemStack(Mek, "EnrichedAlloy", 1);
        alloyReinforced = GameRegistry.findItemStack(Mek, "ReinforcedAlloy", 1);
        alloyAtomic = GameRegistry.findItemStack(Mek, "AtomicAlloy", 1);
        energyTablet = GameRegistry.findItemStack(Mek, "EnergyTablet", 1);


        // Items
        // Barrels
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierTwoItem, 1), new Object[]{"AAA", " B ",
                "AAA", 'A', ironEnriched, 'B', Items.barrelTierOneItem}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierThreeItem, 1), new Object[]{"AAA", " B ",
                "AAA", 'A', alloyEnriched, 'B', Items.barrelTierTwoItem}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierFourItem, 1), new Object[]{"CAC", " B ",
                "CAC", 'A', net.minecraft.init.Items.diamond, 'B',
                Items.barrelTierThreeItem, 'C', alloyReinforced}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.barrelTierFiveItem, 1), new Object[]{"AAA", "CBC",
                "AAA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                Items.barrelTierFourItem, 'C', alloyAtomic}));

        // Chambers
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierTwoItem, 1), new Object[]{"AAA", " BC",
                "AAA", 'A', ironEnriched, 'B', Items.chamberTierOneItem, 'C',
                "ingotSteel"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierThreeItem, 1), new Object[]{"AAA", " BC",
                "AAA", 'A', alloyEnriched, 'B', Items.chamberTierTwoItem, 'C',
                "ingotSteel"}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierFourItem, 1), new Object[]{"DAD", " BC",
                "DAD", 'A', net.minecraft.init.Items.diamond, 'B',
                Items.chamberTierThreeItem, 'C', "ingotSteel", 'D',
                alloyReinforced}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.chamberTierFiveItem, 1), new Object[]{"ADA", " BC",
                "ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                Items.chamberTierFourItem, 'C', "ingotSteel", 'D',
                alloyAtomic}));

        // Sensors
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierTwoItem, 1), new Object[]{" A ", "ABA",
                " C ", 'A', ironEnriched, 'B', Items.sensorTierOneItem, 'C',
                Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierThreeItem, 1), new Object[]{" C ", "ABA",
                " C ", 'A', alloyEnriched, 'B', Items.sensorTierTwoItem, 'C',
                Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.sensorTierFourItem, 1), new Object[]{"EDE", "CBC",
                "EDE", 'B', Items.sensorTierThreeItem, 'C', Items.ioBus, 'D',
                net.minecraft.init.Items.diamond, 'E', alloyReinforced}));

        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Items.sensorTierFiveItem, 1), new Object[]{"EDE",
                        "CBC", "EDE", 'B', Items.sensorTierFourItem, 'C',
                        Items.ioBus, 'D',
                        net.minecraft.init.Items.glowstone_dust, 'E',
                        alloyAtomic}));

        // Bases
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.turretBaseTierTwo, 1), new Object[]{"ABA",
                        "ECE", "ADA", 'A', ironEnriched, 'B', energyTablet, 'C',
                        Items.sensorTierTwoItem, 'D', "ingotOsmium", 'E',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierThree, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyEnriched, 'B',
                        energyTablet, 'C', Items.sensorTierThreeItem, 'D',
                        "ingotOsmium", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierFour, 1),
                new Object[]{"ABA", "ECE", "ADA", 'A', alloyReinforced, 'B',
                        energyTablet, 'C', Items.sensorTierFourItem, 'D',
                        "ingotOsmium", 'E', Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.turretBaseTierFive, 1), new Object[]{"ABA", "ECE",
                "ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
                energyTablet, 'C', Items.sensorTierFiveItem, 'D',
                "ingotOsmium", 'E', Items.ioBus}));

        // Turrets
        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.machineGunTurret, 1), new Object[]{" A ",
                        "CAC", "DBD", 'A', Items.barrelTierTwoItem, 'B',
                        Items.chamberTierTwoItem, 'C', ironEnriched, 'D',
                        Items.ioBus}));

        GameRegistry
                .addRecipe(new ShapedOreRecipe(new ItemStack(
                        Blocks.incendiaryTurret, 1), new Object[]{"A A",
                        "BCB", "DCD", 'A', Items.barrelTierTwoItem, 'B',
                        Items.chamberTierTwoItem, 'C', ironEnriched, 'D',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.grenadeLauncherTurret, 1),
                new Object[]{" A ", "CBC", "CDC", 'A',
                        Items.barrelTierThreeItem, 'B',
                        Items.chamberTierThreeItem, 'C', alloyEnriched, 'D',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.relativisticTurret, 1),
                new Object[]{"CAC", "ABA", "CDC", 'A',
                        net.minecraft.init.Items.ender_pearl, 'B',
                        Items.sensorTierThreeItem, 'C', alloyEnriched, 'D',
                        Items.ioBus}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.rocketTurret, 1), new Object[]{"CAC", "CAC", "EDE",
                'A', Items.barrelTierFourItem, 'B', Items.chamberTierFourItem,
                'C', alloyReinforced, 'D', Items.ioBus, 'E',
                net.minecraft.init.Items.diamond}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.teleporterTurret, 1), new Object[]{"CEC", "ABA",
                "CDC", 'A', net.minecraft.init.Items.diamond, 'B',
                Items.sensorTierFourItem, 'C',
                net.minecraft.init.Items.ender_eye, 'D', Items.ioBus, 'E',
                net.minecraft.init.Items.diamond}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.laserTurret, 1), new Object[]{"EAE", "CBC", "DCD",
                'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
                'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus, 'E',
                alloyAtomic}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Blocks.railGunTurret, 1), new Object[]{"EAE", "CAC", "DBD",
                'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
                'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus, 'E',
                alloyAtomic}));

        // Ammo
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.rocketCraftable, 32), new Object[]{" A ", "ABA", "ACA",
                'A', "ingotTin", 'B', net.minecraft.init.Items.gunpowder, 'C',
                net.minecraft.init.Items.redstone}));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
                Items.bulletCraftable, 64), new Object[]{" A ", " B ", " C ",
                'A', ironEnriched, 'B', net.minecraft.init.Items.gunpowder, 'C',
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
