package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.expander.*;
import openmodularturrets.tileentity.turrets.*;

public class ItemRenderers {
	public static void init() {
		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.disposableItemTurret),
				new DisposableItemTurretItemRenderer(
						TileEntityRenderers.disposableItemTurretRenderer,
						new DisposableItemTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.potatoCannonTurret),
				new PotatoCannonTurretItemRenderer(
						TileEntityRenderers.potatoCannonTurretRenderer,
						new PotatoCannonTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.rocketTurret),
				new RocketTurretItemRenderer(
						TileEntityRenderers.rocketTurretRenderer,
						new RocketTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.machineGunTurret),
				new MachineGunTurretItemRenderer(
						TileEntityRenderers.machineGunTurretRenderer,
						new MachineGunTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.grenadeLauncherTurret),
				new GrenadeLauncherTurretItemRenderer(
						TileEntityRenderers.grenadeLauncherTurretRenderer,
						new GrenadeLauncherTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.laserTurret),
				new LaserTurretItemRenderer(
						TileEntityRenderers.laserTurretRenderer,
						new LaserTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.leverBlock), new LeverItemRenderer(
				TileEntityRenderers.leverRenderer, new LeverTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.railGunTurret),
				new RailGunTurretItemRenderer(
						TileEntityRenderers.railGunTurretRenderer,
						new RailGunTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.incendiaryTurret),
				new IncendiaryTurretItemRenderer(
						TileEntityRenderers.incendiaryTurretRenderer,
						new IncendiaryTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.relativisticTurret),
				new RelativisticTurretItemRenderer(
						TileEntityRenderers.relativisticTurretRenderer,
						new RelativisticTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
				.getItemFromBlock(Blocks.teleporterTurret),
				new TeleporterTurretItemRenderer(
						TileEntityRenderers.teleporterTurretRenderer,
						new TeleporterTurretTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
						.getItemFromBlock(Blocks.expanderPowerTierOne),
				new ExpanderPowerTierOneItemRenderer(
						TileEntityRenderers.expanderTierOneRenderer,
						new ExpanderPowerTierOneTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
						.getItemFromBlock(Blocks.expanderPowerTierTwo),
				new ExpanderPowerTierTwoItemRenderer(
						TileEntityRenderers.expanderTierTwoRenderer,
						new ExpanderPowerTierTwoTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
						.getItemFromBlock(Blocks.expanderPowerTierThree),
				new ExpanderPowerTierThreeItemRenderer(
						TileEntityRenderers.expanderTierThreeRenderer,
						new ExpanderPowerTierThreeTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
						.getItemFromBlock(Blocks.expanderPowerTierFour),
				new ExpanderPowerTierFourItemRenderer(
						TileEntityRenderers.expanderTierFourRenderer,
						new ExpanderPowerTierFourTileEntity()));

		MinecraftForgeClient.registerItemRenderer(Item
						.getItemFromBlock(Blocks.expanderPowerTierFive),
				new ExpanderPowerTierFiveItemRenderer(
						TileEntityRenderers.expanderTierFiveRenderer,
						new ExpanderPowerTierFiveTileEntity()));

	}
}
