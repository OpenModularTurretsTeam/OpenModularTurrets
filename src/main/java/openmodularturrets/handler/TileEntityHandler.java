package openmodularturrets.handler;

import net.minecraftforge.fml.common.registry.GameRegistry;
import openmodularturrets.reference.Names;
import openmodularturrets.reference.Reference;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.expander.*;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turrets.*;

public class TileEntityHandler {
    public static void init() {
        GameRegistry.registerTileEntity(TurretBase.class, Reference.MOD_ID.toLowerCase() + ":" + Names.Blocks.turretBase);
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, "disposableItemTurret");
        GameRegistry.registerTileEntity(PotatoCannonTurretTileEntity.class, "potatoCannonTurret");
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, "rocketTurret");
        GameRegistry.registerTileEntity(GunTurretTileEntity.class, "machineGunTurret");
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, "grenadeTurret");
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, "laserTurret");
        GameRegistry.registerTileEntity(LeverTileEntity.class, "leverTileEntity");
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
