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

    public static ExpanderPowerTierOneRenderer expanderPowerTierOneRenderer = new ExpanderPowerTierOneRenderer();
    public static ExpanderPowerTierTwoRenderer expanderPowerTierTwoRenderer = new ExpanderPowerTierTwoRenderer();
    public static ExpanderPowerTierThreeRenderer expanderPowerTierThreeRenderer = new ExpanderPowerTierThreeRenderer();
    public static ExpanderPowerTierFourRenderer expanderPowerTierFourRenderer = new ExpanderPowerTierFourRenderer();
    public static ExpanderPowerTierFiveRenderer expanderPowerTierFiveRenderer = new ExpanderPowerTierFiveRenderer();

    public static ExpanderInvTierOneRenderer expanderInvTierOneRenderer = new ExpanderInvTierOneRenderer();
    public static ExpanderInvTierTwoRenderer expanderInvTierTwoRenderer = new ExpanderInvTierTwoRenderer();
    public static ExpanderInvTierThreeRenderer expanderInvTierThreeRenderer = new ExpanderInvTierThreeRenderer();
    public static ExpanderInvTierFourRenderer expanderInvTierFourRenderer = new ExpanderInvTierFourRenderer();
    public static ExpanderInvTierFiveRenderer expanderInvTierFiveRenderer = new ExpanderInvTierFiveRenderer();

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
                GunTurretTileEntity.class, machineGunTurretRenderer);
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
                expanderPowerTierOneRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierTwoTileEntity.class,
                expanderPowerTierTwoRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierThreeTileEntity.class,
                expanderPowerTierThreeRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierFourTileEntity.class,
                expanderPowerTierFourRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderPowerTierFiveTileEntity.class,
                expanderPowerTierFiveRenderer);

        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierOneTileEntity.class,
                expanderInvTierOneRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierTwoTileEntity.class,
                expanderInvTierTwoRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierThreeTileEntity.class,
                expanderInvTierThreeRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierFourTileEntity.class,
                expanderInvTierFourRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierFiveTileEntity.class,
                expanderInvTierFiveRenderer);


    }
}
