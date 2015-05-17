package openmodularturrets.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;

public class RecipeHandler {

	@SuppressWarnings("RedundantArrayCreation")
	public static void initRecipes() {

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierFive,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotEnderium", 'D',
						Items.ioBus, 'C', Items.sensorTierFiveItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierFour,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotElectrum", 'D',
						Items.ioBus, 'C', Items.sensorTierFourItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierThree,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotInvar", 'D',
						Items.ioBus, 'C', Items.sensorTierThreeItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierTwo,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotLead", 'D',
						Items.ioBus, 'C', Items.sensorTierTwoItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.turretBaseTierOne,
				new Object[] { "ADA", "DCD", "ADA", 'A',
						net.minecraft.init.Blocks.planks, 'D',
						net.minecraft.init.Items.iron_ingot, 'C',
						net.minecraft.init.Items.redstone }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierFiveItem,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotEnderium", 'D',
						net.minecraft.init.Items.diamond, 'C',
						net.minecraft.init.Blocks.redstone_block }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierFourItem,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotElectrum", 'D',
						net.minecraft.init.Items.diamond, 'C',
						net.minecraft.init.Blocks.redstone_block }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierThreeItem,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotInvar", 'D',
						net.minecraft.init.Items.diamond, 'C',
						net.minecraft.init.Blocks.redstone_block }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.sensorTierTwoItem,
				new Object[] { "ADA", "DCD", "ADA", 'A', "ingotLead", 'D',
						net.minecraft.init.Items.diamond, 'C',
						net.minecraft.init.Blocks.redstone_block }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.fireRateUpgradeItem,
				new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
						net.minecraft.init.Items.blaze_powder, 'C',
						net.minecraft.init.Items.diamond }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.efficiencyUpgradeItem,
				new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
						net.minecraft.init.Items.ender_pearl, 'C',
						net.minecraft.init.Items.diamond }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.rangeUpgradeItem,
				new Object[] { "   ", "BCB", "ADA", 'A', "dustElectrum", 'D',
						net.minecraft.init.Items.emerald, 'C',
						net.minecraft.init.Items.diamond }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.accuraccyUpgradeItem,
				new Object[] { "   ", "DCD", "ADA", 'A', "dustElectrum", 'D',
						"ingotInvar", 'C', net.minecraft.init.Items.diamond }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.solarPanelAddon,
				new Object[] { "BBB", "DDD", "ACA", 'A', "dustElectrum", 'D',
						net.minecraft.init.Blocks.lapis_block, 'C',
						net.minecraft.init.Items.redstone, 'B',
						net.minecraft.init.Blocks.glass }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Items.redstoneReactorAddon,
				new Object[] { "DDD", "DCD", "DAD", 'A',
						net.minecraft.init.Items.redstone, 'D', "ingotLead",
						'C', "nuggetPlatinum" }));

		GameRegistry
				.addRecipe(new ShapedOreRecipe(Items.damageAmpAddon,
						new Object[] { "   ", "CCC", "ADA", 'A',
								"ingotElectrum", 'D', "ingotElectrum", 'C',
								net.minecraft.init.Items.diamond }));


		GameRegistry.addRecipe(new ShapedOreRecipe(Items.ioBus, new Object[] {
				" A ", "BBB", " A ", 'A', net.minecraft.init.Items.gold_ingot,
				'B', net.minecraft.init.Items.redstone }));


		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.leverBlock,
				new Object[] { "CCC", "C  ", "C  ", 'C',
						net.minecraft.init.Blocks.stone }));

		GameRegistry.addRecipe(new ShapedOreRecipe(Blocks.disposableItemTurret,
				new Object[] { "BCB", "CAC", " C ", 'C',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Blocks.planks, 'A',
						net.minecraft.init.Blocks.dispenser }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.bulletCraftable, 32), new Object[] { " B ", " A " + "",
				" A ", 'B', "ingotLead", 'A',
				net.minecraft.init.Items.gunpowder }));

		GameRegistry.addRecipe(new ItemStack(Items.rocketCraftable, 16),
				new Object[] { " A ", "ABA", "ABA", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.gunpowder });

		GameRegistry.addRecipe(new ItemStack(Items.grenadeCraftable, 16),
				new Object[] { " A ", "ABA", " A ", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.gunpowder });

		GameRegistry.addRecipe(new ItemStack(Items.ferroSlug, 16),
				new Object[] { " A ", "CBC", " A ", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.water_bucket, 'C',
						net.minecraft.init.Blocks.glass_pane });
	}
}
