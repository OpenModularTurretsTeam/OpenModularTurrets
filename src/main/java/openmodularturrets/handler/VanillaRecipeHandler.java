package openmodularturrets.handler;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class VanillaRecipeHandler {

	public static void init() {

		// Items
		// Barrels
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.barrelTierOneItem, 1), new Object[] { "AAA", " B ",
				"AAA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
				"plankWood" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.barrelTierTwoItem, 1), new Object[] { "AAA", " B ",
				"AAA", 'A', "ingotIron", 'B', Items.barrelTierOneItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.barrelTierThreeItem, 1), new Object[] { "AAA", " B ",
				"AAA", 'A', "ingotGold", 'B', Items.barrelTierTwoItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.barrelTierFourItem, 1),
				new Object[] { "CAC", " B ", "CAC", 'A',
						net.minecraft.init.Items.diamond, 'B',
						Items.barrelTierThreeItem, 'C',
						net.minecraft.init.Items.quartz }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.barrelTierFiveItem, 1), new Object[] { "AAA", "CBC",
				"AAA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
				Items.barrelTierFourItem, 'C',
				net.minecraft.init.Items.glowstone_dust }));

		// Chambers
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.chamberTierOneItem, 1), new Object[] { "AAA", " BC",
				"AAA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
				"plankWood", 'C', net.minecraft.init.Items.redstone }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.chamberTierTwoItem, 1), new Object[] { "AAA", " BC",
				"AAA", 'A', "ingotIron", 'B', Items.chamberTierOneItem, 'C',
				net.minecraft.init.Items.redstone }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.chamberTierThreeItem, 1), new Object[] { "AAA", " BC",
				"AAA", 'A', "ingotGold", 'B', Items.chamberTierTwoItem, 'C',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.chamberTierFourItem, 1), new Object[] { "DAD", " BC",
				"DAD", 'A', net.minecraft.init.Items.diamond, 'B',
				Items.chamberTierThreeItem, 'C', Items.ioBus, 'D',
				net.minecraft.init.Items.quartz }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.chamberTierFiveItem, 1), new Object[] { "ADA", " BC",
				"ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
				Items.chamberTierFourItem, 'C', Items.ioBus, 'D',
				net.minecraft.init.Items.quartz }));

		// Sensors
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.sensorTierOneItem, 1),
				new Object[] { " A ", "ABA", " A ", 'A',
						net.minecraft.init.Items.redstone, 'B', "plankWood" }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.sensorTierTwoItem, 1), new Object[] { " A ", "ABA",
				" C ", 'A', "ingotIron", 'B', Items.sensorTierOneItem, 'C',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.sensorTierThreeItem, 1), new Object[] { " C ", "ABA",
				" C ", 'A', "ingotGold", 'B', Items.sensorTierTwoItem, 'C',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.sensorTierFourItem, 1), new Object[] { "EDE", "CBC",
				"EDE", 'A', "ingotGold", 'B', Items.sensorTierThreeItem, 'C',
				Items.ioBus, 'D', net.minecraft.init.Items.diamond, 'E',
				net.minecraft.init.Items.quartz }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.sensorTierFiveItem, 1), new Object[] { "EDE", "CBC",
				"EDE", 'A', "ingotGold", 'B', Items.sensorTierFourItem, 'C',
				Items.ioBus, 'D', net.minecraft.init.Items.glowstone_dust, 'E',
				net.minecraft.init.Blocks.obsidian }));

		// Bases
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierOne, 1), new Object[] { "ABA", "BCB",
				"ABA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
				"plankWood", 'C', Items.sensorTierOneItem }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierTwo, 1), new Object[] { "ABA", "DCD",
				"ADA", 'A', "ingotIron", 'B', Blocks.turretBaseTierOne, 'C',
				Items.sensorTierTwoItem, 'D', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierThree, 1), new Object[] { "ABA", "DCD",
				"ADA", 'A', "ingotGold", 'B', Blocks.turretBaseTierTwo, 'C',
				Items.sensorTierThreeItem, 'D', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierFour, 1), new Object[] { "ABA", "DCD",
				"ADA", 'A', net.minecraft.init.Items.diamond, 'B',
				Blocks.turretBaseTierThree, 'C', Items.sensorTierFourItem, 'D',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierFive, 1), new Object[] { "ABA", "DCD",
				"ADA", 'A', net.minecraft.init.Blocks.obsidian, 'B',
				Blocks.turretBaseTierFour, 'C', Items.sensorTierFiveItem, 'D',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.turretBaseTierOne, 1), new Object[] { "ABA", "BCB",
				"ABA", 'A', net.minecraft.init.Blocks.cobblestone, 'B',
				"plankWood", 'C', Items.sensorTierOneItem }));

		// Addons
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.solarPanelAddon, 1), new Object[] { "AAA", "CBC", "DED",
				'A', net.minecraft.init.Blocks.glass_pane, 'B',
				net.minecraft.init.Blocks.lapis_block, 'C',
				net.minecraft.init.Items.redstone, 'D', "ingotIron", 'E',
				Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.redstoneReactorAddon, 1), new Object[] { "CAC", "ABD",
				"CAC", 'A', "ingotIron", 'B',
				net.minecraft.init.Items.ender_eye, 'C',
				net.minecraft.init.Items.quartz, 'D', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.damageAmpAddon, 1), new Object[] { "AAA", "B B", "AAA",
				'A', "ingotIron", 'B', net.minecraft.init.Items.ender_pearl }));

		// Upgrades
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.efficiencyUpgradeItem, 1), new Object[] { " A ", "ABA",
				" C ", 'A', net.minecraft.init.Items.quartz, 'B',
				net.minecraft.init.Items.ender_eye, 'C', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.accuraccyUpgradeItem, 1), new Object[] { " A ", "ABA",
				" C ", 'A', net.minecraft.init.Items.quartz, 'B', "ingotGold",
				'C', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.fireRateUpgradeItem, 1), new Object[] { " A ", "ABA",
				" C ", 'A', net.minecraft.init.Items.quartz, 'B',
				net.minecraft.init.Items.blaze_powder, 'C', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.rangeUpgradeItem, 1), new Object[] { " A ", "ABA", " C ",
				'A', net.minecraft.init.Items.quartz, 'B',
				net.minecraft.init.Items.diamond, 'C', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Items.scattershotUpgradeItem, 1), new Object[] { " A ", "ABA",
				" C ", 'A', net.minecraft.init.Items.quartz, 'B',
				net.minecraft.init.Items.flint, 'C', Items.ioBus }));

		// Turrets
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.disposableItemTurret, 1), new Object[] { " A ", "CBC",
				"CDC", 'A', Items.barrelTierOneItem, 'B',
				Items.chamberTierOneItem, 'C',
				net.minecraft.init.Blocks.cobblestone, 'D',
				net.minecraft.init.Items.redstone }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.potatoCannonTurret, 1), new Object[] { "CAC", "CAC",
				"DBD", 'A', Items.barrelTierOneItem, 'B',
				Items.chamberTierOneItem, 'C',
				net.minecraft.init.Blocks.cobblestone, 'D',
				net.minecraft.init.Items.redstone }));

		GameRegistry
				.addRecipe(new ShapedOreRecipe(new ItemStack(
						Blocks.machineGunTurret, 1), new Object[] { " A ",
						"CAC", "DBD", 'A', Items.barrelTierTwoItem, 'B',
						Items.chamberTierTwoItem, 'C', "ingotIron", 'D',
						Items.ioBus }));

		GameRegistry
				.addRecipe(new ShapedOreRecipe(new ItemStack(
						Blocks.incendiaryTurret, 1), new Object[] { "A A",
						"BCB", "DCD", 'A', Items.barrelTierTwoItem, 'B',
						Items.chamberTierTwoItem, 'C', "ingotIron", 'D',
						Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.grenadeLauncherTurret, 1),
				new Object[] { " A ", "CBC", "CDC", 'A',
						Items.barrelTierThreeItem, 'B',
						Items.chamberTierThreeItem, 'C', "ingotGold", 'D',
						Items.ioBus }));

		GameRegistry
				.addRecipe(new ShapedOreRecipe(new ItemStack(
						Blocks.relativisticTurret, 1), new Object[] { "CAC",
						"ABA", "CDC", 'A',
						net.minecraft.init.Items.ender_pearl, 'B',
						Items.sensorTierThreeItem, 'C', "ingotGold", 'D',
						Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.rocketTurret, 1), new Object[] { "CAC", "ABA", "EDE",
				'A', Items.barrelTierFourItem, 'B', Items.chamberTierFourItem,
				'C', net.minecraft.init.Items.quartz, 'D', Items.ioBus, 'E',
				net.minecraft.init.Items.diamond }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.teleporterTurret, 1), new Object[] { "CEC", "ABA",
				"CDC", 'A', net.minecraft.init.Items.diamond, 'B',
				Items.sensorTierFourItem, 'C',
				net.minecraft.init.Items.ender_eye, 'D', Items.ioBus, 'E',
				net.minecraft.init.Items.quartz }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.laserTurret, 1), new Object[] { " A ", "CBC", "DCD",
				'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
				'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus }));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(
				Blocks.railGunTurret, 1), new Object[] { "CAC", "CAC", "DBD",
				'A', Items.barrelTierFiveItem, 'B', Items.chamberTierFiveItem,
				'C', net.minecraft.init.Blocks.obsidian, 'D', Items.ioBus }));

		// Ammo
		GameRegistry.addRecipe(new ItemStack(Items.rocketCraftable, 32),
				new Object[] { " A ", "ABA", "ACA", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.gunpowder, 'C',
						net.minecraft.init.Items.redstone });

		GameRegistry.addRecipe(new ItemStack(Items.bulletCraftable, 64),
				new Object[] { " A ", "BC ", " A ", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.gunpowder, 'C',
						net.minecraft.init.Items.redstone });

		GameRegistry.addRecipe(new ItemStack(Items.grenadeCraftable, 32),
				new Object[] { " C ", "ABA", " A ", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.gunpowder, 'C',
						net.minecraft.init.Items.redstone });

		GameRegistry.addRecipe(new ItemStack(Items.blazingClayCraftable, 32),
				new Object[] { "BCB", "CAC", "BCB", 'A',
						net.minecraft.init.Items.blaze_powder, 'B',
						net.minecraft.init.Items.clay_ball, 'C',
						net.minecraft.init.Items.redstone });

		GameRegistry.addRecipe(new ItemStack(Items.ferroSlug, 16),
				new Object[] { " C ", "CBC", " A ", 'A',
						net.minecraft.init.Items.iron_ingot, 'B',
						net.minecraft.init.Items.flint, 'C',
						net.minecraft.init.Items.redstone });

		// Other
		GameRegistry.addRecipe(new ItemStack(Blocks.leverBlock, 1),
				new Object[] { "AAA", "A  ", "A  ", 'A',
						net.minecraft.init.Blocks.cobblestone });

	}
}
