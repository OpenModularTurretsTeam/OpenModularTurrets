package openmodularturrets.handler;

import cpw.mods.fml.common.registry.GameRegistry;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierFiveTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierFourTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierOneTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierThreeTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierTwoTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierFiveTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierFourTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierOneTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierThreeTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierTwoTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFiveTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFourTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierThreeTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierTwoTileEntity;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import openmodularturrets.tileentity.turrets.GunTurretTileEntity;
import openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import openmodularturrets.tileentity.turrets.PotatoCannonTurretTileEntity;
import openmodularturrets.tileentity.turrets.RailGunTurretTileEntity;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

public class TileEntityHandler {

    public static void init() {
        GameRegistry.registerTileEntity(TurretBase.class, "turretbase");
        GameRegistry.registerTileEntity(TurretBaseTierOneTileEntity.class, "turretWoodBase");
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
        GameRegistry.registerTileEntity(PotatoCannonTurretTileEntity.class, "potatoCannonTurret");
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
        GameRegistry.registerTileEntity(GunTurretTileEntity.class, "machineGunTurret");
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
        GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
        GameRegistry.registerTileEntity(TurretBaseTierTwoTileEntity.class, "turretBaseOne");
        GameRegistry.registerTileEntity(TurretBaseTierThreeTileEntity.class, "turretBaseTwo");
        GameRegistry.registerTileEntity(TurretBaseTierFourTileEntity.class, "turretBaseThree");
        GameRegistry.registerTileEntity(TurretBaseTierFiveTileEntity.class, "turretBaseFour");
        GameRegistry.registerTileEntity(RailGunTurretTileEntity.class, "railGunTurret");
        GameRegistry.registerTileEntity(IncendiaryTurretTileEntity.class, "incendiaryTurret");
        GameRegistry.registerTileEntity(RelativisticTurretTileEntity.class, "relativisticTurret");
        GameRegistry.registerTileEntity(TeleporterTurretTileEntity.class, "teleporterTurret");
        GameRegistry.registerTileEntity(ExpanderPowerTierOneTileEntity.class, "expanderPowerTierOne");
        GameRegistry.registerTileEntity(ExpanderPowerTierTwoTileEntity.class, "expanderPowerTierTwo");
        GameRegistry.registerTileEntity(ExpanderPowerTierThreeTileEntity.class, "expanderPowerTierThree");
        GameRegistry.registerTileEntity(ExpanderPowerTierFourTileEntity.class, "expanderPowerTierFour");
        GameRegistry.registerTileEntity(ExpanderPowerTierFiveTileEntity.class, "expanderPowerTierFive");
        GameRegistry.registerTileEntity(ExpanderInvTierOneTileEntity.class, "expanderInvTierOne");
        GameRegistry.registerTileEntity(ExpanderInvTierTwoTileEntity.class, "expanderInvTierTwo");
        GameRegistry.registerTileEntity(ExpanderInvTierThreeTileEntity.class, "expanderInvTierThree");
        GameRegistry.registerTileEntity(ExpanderInvTierFourTileEntity.class, "expanderInvTierFour");
        GameRegistry.registerTileEntity(ExpanderInvTierFiveTileEntity.class, "expanderInvTierFive");
    }
}
