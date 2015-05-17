package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.client.render.renderers.blockitem.TileEntityRenderers;
import openmodularturrets.tileentity.LeverTileEntity;
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

	}
}
