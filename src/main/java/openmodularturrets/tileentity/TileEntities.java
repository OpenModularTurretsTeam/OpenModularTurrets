package openmodularturrets.tileentity;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.tileentity.turretBase.*;
import openmodularturrets.tileentity.turrets.*;

public class TileEntities {
	
	public static void init()
	{
		GameRegistry.registerTileEntity(TurretBase.class, "turretBase");
		GameRegistry.registerTileEntity(TurretWoodBase.class, "turretWoodBase");
		GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
		GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
		GameRegistry.registerTileEntity(MachineGunTurretTileEntity.class, "machineGunTurret");
		GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
		GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
		GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
		GameRegistry.registerTileEntity(TurretBaseTierOneTileEntity.class, "turretBaseOne");
		GameRegistry.registerTileEntity(TurretBaseTierTwoTileEntity.class, "turretBaseTwo");
		GameRegistry.registerTileEntity(TurretBaseTierThreeTileEntity.class, "turretBaseThree");
		GameRegistry.registerTileEntity(TurretBaseTierFourTileEntity.class, "turretBaseFour");
	}
	
}
