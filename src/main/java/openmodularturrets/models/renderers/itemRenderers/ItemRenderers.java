package openmodularturrets.models.renderers.itemRenderers;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.models.renderers.TileEntityRenderers;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turrets.*;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ItemRenderers {
	public static void init()
	{
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.disposableItemTurret), new DisposableItemTurretItemRenderer(TileEntityRenderers.disposableItemTurretRenderer, new DisposableItemTurretTileEntity()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.rocketTurret), new RocketTurretItemRenderer(TileEntityRenderers.rocketTurretRenderer, new RocketTurretTileEntity()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.machineGunTurret), new MachineGunTurretItemRenderer(TileEntityRenderers.machineGunTurretRenderer, new MachineGunTurretTileEntity()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.grenadeLauncherTurret), new GrenadeLauncherTurretItemRenderer(TileEntityRenderers.grenadeLauncherTurretRenderer, new GrenadeLauncherTurretTileEntity()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.laserTurret), new LaserTurretItemRenderer(TileEntityRenderers.laserTurretRenderer, new LaserTurretTileEntity()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Blocks.leverBlock), new LeverItemRenderer(TileEntityRenderers.leverRenderer, new LeverTileEntity()));
	}
}
