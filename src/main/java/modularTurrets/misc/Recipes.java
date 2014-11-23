package modularTurrets.misc;

import java.util.ArrayList;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import modularTurrets.blocks.Blocks;
import modularTurrets.items.Items;

public class Recipes {

    public static void initRecipes() {

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierFour,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotEnderium", 'D',
			Items.ioBus, 'C', Items.sensorTierFourItem }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierThree,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotElectrum", 'D',
			Items.ioBus, 'C', Items.sensorTierThreeItem }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierTwo,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotInvar", 'D',
			Items.ioBus, 'C', Items.sensorTierTwoItem }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierOne,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotLead", 'D',
			Items.ioBus, 'C', Items.sensorTierOneItem }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierWood,
		new Object[] { "ADA", "DCD", "ADA", 'A', Block.planks, 'D',
			Item.ingotIron, 'C', Item.redstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierWood,
		new Object[] { "ADA", "DCD", "ADA", 'A', Block.planks, 'D',
			Item.ingotIron, 'C', Item.redstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierFourItem,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotEnderium", 'D',
			Item.diamond, 'C', Block.blockRedstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierThreeItem,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotElectrum", 'D',
			Item.diamond, 'C', Block.blockRedstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierTwoItem,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotInvar", 'D',
			Item.diamond, 'C', Block.blockRedstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierOneItem,
		new Object[] { "ADA", "DCD", "ADA", 'A', "ingotLead", 'D',
			Item.diamond, 'C', Block.blockRedstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.fireRateUpgradeItem,
		new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
			Item.blazePowder, 'C', Item.diamond }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.efficiencyUpgradeItem,
		new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
			Item.enderPearl, 'C', Item.diamond }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.rangeUpgradeItem,
		new Object[] { "   ", "BCB", "ADA", 'A', "dustElectrum", 'D',
			Item.emerald, 'C', Item.diamond }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.accuraccyUpgradeItem,
		new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
			"ingotInvar", 'C', Item.diamond }));

	GameRegistry
		.addRecipe(new ShapedOreRecipe(Items.solarPanelAddon,
			new Object[] { "BBB", "DDD", "ACA", 'A',
				"dustElectrum", 'D', Block.blockLapis, 'C',
				Item.redstone, 'B', Block.glass }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.redstoneReactorAddon,
		new Object[] { "DDD", "DCD", "DAD", 'A', Item.redstone, 'D',
			"ingotLead", 'C', "nuggetPlatinum" }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.damageAmpAddon,
		new Object[] { "   ", "CCC", "ADA", 'A', "ingotElectrum", 'D',
			"ingotElectrum", 'C', Item.diamond }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.barrel, new Object[] {
		"CCC", "   ", "CCC", 'C', "ingotInvar" }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.chamber, new Object[] {
		"CCC", "  C", "CCC", 'C', "ingotInvar" }));

	GameRegistry
		.addRecipe(new ShapedOreRecipe(Items.ioBus, new Object[] {
			" A ", "BBB", " A ", 'A', Item.ingotGold, 'B',
			Item.redstone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.energeticBarrel,
		new Object[] { "CCC", "D D", "CCC", 'C', "ingotInvar", 'D',
			Item.diamond }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.containmentChamber,
		new Object[] { "CCC", "DBC", "CCC", 'C', "ingotInvar", 'D',
			Item.diamond, 'B', "ingotPlatinum" }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Items.configTab,
		new Object[] { " A ", "BCB", "BBB", 'A', "ingotElectrum", 'B',
			"ingotInvar", 'C', Items.sensorTierOneItem }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.leverBlock,
		new Object[] { "CCC", "C  ", "C  ", 'C', Block.stone }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.disposableItemTurret,
		new Object[] { "BCB", "CAC", " C ", 'C', Item.ingotIron, 'B',
			Block.planks, 'A', Block.dispenser }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.machineGunTurret,
		new Object[] { " B ", "BAB", " C ", 'C', Items.barrel, 'B',
			"ingotLead", 'A', Items.chamber }));

	GameRegistry.addRecipe(new ShapedOreRecipe(
		Blocks.grenadeLauncherTurret, new Object[] { "DBD", "BAB",
			" C ", 'C', Items.barrel, 'B', "ingotInvar", 'A',
			Items.chamber, 'D', "dustLead" }));

	GameRegistry
		.addRecipe(new ShapedOreRecipe(Blocks.rocketTurret,
			new Object[] { "BDB", "BAB", " C ", 'C', Items.barrel,
				'B', "ingotElectrum", 'A', Items.chamber, 'D',
				"dustInvar" }));

	GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.laserTurret,
		new Object[] { "BDB", "BAB", " C ", 'C', Items.energeticBarrel,
			'B', "ingotEnderium", 'A', Items.containmentChamber,
			'D', "dustElectrum" }));

	GameRegistry.addRecipe(new ItemStack(Items.bulletCraftable, 32),
		new Object[] { " B ", " A ", " A ", 'B', Item.ingotIron, 'A',
			Item.gunpowder });

	GameRegistry.addRecipe(new ItemStack(Items.rocketCraftable, 16),
		new Object[] { " A ", "ABA", "ABA", 'A', Item.ingotIron, 'B',
			Item.gunpowder });

	GameRegistry.addRecipe(new ItemStack(Items.grenadeCraftable, 16),
		new Object[] { " A ", "ABA", " A ", 'A', Item.ingotIron, 'B',
			Item.gunpowder });
    }

}
