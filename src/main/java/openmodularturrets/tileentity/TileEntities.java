package openmodularturrets.tileentity;

import openmodularturrets.tileentity.turretBase.TurretBase;
import openmodularturrets.tileentity.turretBase.TurretBaseTierFourTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierOneTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierThreeTileEntity;
import openmodularturrets.tileentity.turretBase.TurretBaseTierTwoTileEntity;
import openmodularturrets.tileentity.turretBase.TurretWoodBase;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;
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
