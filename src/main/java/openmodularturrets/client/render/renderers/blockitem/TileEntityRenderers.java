package openmodularturrets.client.render.renderers.blockitem;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.expander.*;
import openmodularturrets.tileentity.turrets.*;

public class TileEntityRenderers {
    public static DisposableItemTurretRenderer disposableItemTurretRenderer = new DisposableItemTurretRenderer();
    public static PotatoCannonTurretRenderer potatoCannonTurretRenderer = new PotatoCannonTurretRenderer();
    public static RocketTurretRenderer rocketTurretRenderer = new RocketTurretRenderer();
    public static MachineGunTurretRenderer machineGunTurretRenderer = new MachineGunTurretRenderer();
    public static GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer = new GrenadeLauncherTurretRenderer();
    public static LaserTurretRenderer laserTurretRenderer = new LaserTurretRenderer();
    public static RailGunTurretRenderer railGunTurretRenderer = new RailGunTurretRenderer();

    public static IncendiaryTurretRenderer incendiaryTurretRenderer = new IncendiaryTurretRenderer();
    public static RelativisticTurretRenderer relativisticTurretRenderer = new RelativisticTurretRenderer();
    public static TeleporterTurretRenderer teleporterTurretRenderer = new TeleporterTurretRenderer();

    public static ExpanderTierOneRenderer expanderTierOneRenderer = new ExpanderTierOneRenderer();
    public static ExpanderTierTwoRenderer expanderTierTwoRenderer = new ExpanderTierTwoRenderer();
    public static ExpanderTierThreeRenderer expanderTierThreeRenderer = new ExpanderTierThreeRenderer();
    public static ExpanderTierFourRenderer expanderTierFourRenderer = new ExpanderTierFourRenderer();
    public static ExpanderTierFiveRenderer expanderTierFiveRenderer = new ExpanderTierFiveRenderer();

    public static LeverRenderer leverRenderer = new LeverRenderer();

    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(
                DisposableItemTurretTileEntity.class,
                disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                PotatoCannonTurretTileEntity.class, potatoCannonTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                MachineGunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                GrenadeLauncherTurretTileEntity.class,
                grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                RailGunTurretTileEntity.class, railGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                IncendiaryTurretTileEntity.class, incendiaryTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                RelativisticTurretTileEntity.class, relativisticTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(
                TeleporterTurretTileEntity.class, teleporterTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class,
                leverRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierOneTileEntity.class,
                expanderTierOneRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierTwoTileEntity.class,
                expanderTierTwoRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierThreeTileEntity.class,
                expanderTierThreeRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierFourTileEntity.class,
                expanderTierFourRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierFiveTileEntity.class,
                expanderTierFiveRenderer);


    }
}
