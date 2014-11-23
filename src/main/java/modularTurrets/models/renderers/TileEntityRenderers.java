package modularTurrets.models.renderers;

import modularTurrets.tileEntities.LeverTileEntity;
import modularTurrets.tileEntities.turrets.DisposableItemTurretTileEntity;
import modularTurrets.tileEntities.turrets.GrenadeLauncherTurretTileEntity;
import modularTurrets.tileEntities.turrets.LaserTurretTileEntity;
import modularTurrets.tileEntities.turrets.MachineGunTurretTileEntity;
import modularTurrets.tileEntities.turrets.RocketTurretTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRenderers {

    @SideOnly(Side.CLIENT)
    public static void init() {

	ClientRegistry.bindTileEntitySpecialRenderer(
		DisposableItemTurretTileEntity.class,
		new DisposableItemTurretRenderer());
	ClientRegistry.bindTileEntitySpecialRenderer(
		RocketTurretTileEntity.class, new RocketTurretRenderer());
	ClientRegistry.bindTileEntitySpecialRenderer(
		MachineGunTurretTileEntity.class,
		new MachineGunTurretRenderer());
	ClientRegistry.bindTileEntitySpecialRenderer(
		GrenadeLauncherTurretTileEntity.class,
		new GrenadeLauncherTurretRenderer());
	ClientRegistry.bindTileEntitySpecialRenderer(
		LaserTurretTileEntity.class, new LaserTurretRenderer());
	ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class,
		new LeverRenderer());

    }
}
