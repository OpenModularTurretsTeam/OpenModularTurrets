package modularTurrets.tileEntities;

import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretBaseTierFourTileEntity;
import modularTurrets.tileEntities.turretBase.TurretBaseTierOneTileEntity;
import modularTurrets.tileEntities.turretBase.TurretBaseTierThreeTileEntity;
import modularTurrets.tileEntities.turretBase.TurretBaseTierTwoTileEntity;
import modularTurrets.tileEntities.turretBase.TurretWoodBase;
import modularTurrets.tileEntities.turrets.DisposableItemTurretTileEntity;
import modularTurrets.tileEntities.turrets.GrenadeLauncherTurretTileEntity;
import modularTurrets.tileEntities.turrets.LaserTurretTileEntity;
import modularTurrets.tileEntities.turrets.MachineGunTurretTileEntity;
import modularTurrets.tileEntities.turrets.RocketTurretTileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

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
