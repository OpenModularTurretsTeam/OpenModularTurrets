package openmodularturrets.client.render.renderers.blockitem;

import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.expander.*;
import openmodularturrets.tileentity.turrets.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRenderers {

    public static final DisposableItemTurretRenderer disposableItemTurretRenderer = new DisposableItemTurretRenderer();
    public static final PotatoCannonTurretRenderer potatoCannonTurretRenderer = new PotatoCannonTurretRenderer();
    public static final RocketTurretRenderer rocketTurretRenderer = new RocketTurretRenderer();
    public static final MachineGunTurretRenderer machineGunTurretRenderer = new MachineGunTurretRenderer();
    public static final GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer = new GrenadeLauncherTurretRenderer();
    public static final LaserTurretRenderer laserTurretRenderer = new LaserTurretRenderer();
    public static final RailGunTurretRenderer railGunTurretRenderer = new RailGunTurretRenderer();

    public static final IncendiaryTurretRenderer incendiaryTurretRenderer = new IncendiaryTurretRenderer();
    public static final RelativisticTurretRenderer relativisticTurretRenderer = new RelativisticTurretRenderer();
    public static final TeleporterTurretRenderer teleporterTurretRenderer = new TeleporterTurretRenderer();

    public static final ExpanderPowerTierOneRenderer expanderPowerTierOneRenderer = new ExpanderPowerTierOneRenderer();
    public static final ExpanderPowerTierTwoRenderer expanderPowerTierTwoRenderer = new ExpanderPowerTierTwoRenderer();
    public static final ExpanderPowerTierThreeRenderer expanderPowerTierThreeRenderer = new ExpanderPowerTierThreeRenderer();
    public static final ExpanderPowerTierFourRenderer expanderPowerTierFourRenderer = new ExpanderPowerTierFourRenderer();
    public static final ExpanderPowerTierFiveRenderer expanderPowerTierFiveRenderer = new ExpanderPowerTierFiveRenderer();

    public static final ExpanderInvTierOneRenderer expanderInvTierOneRenderer = new ExpanderInvTierOneRenderer();
    public static final ExpanderInvTierTwoRenderer expanderInvTierTwoRenderer = new ExpanderInvTierTwoRenderer();
    public static final ExpanderInvTierThreeRenderer expanderInvTierThreeRenderer = new ExpanderInvTierThreeRenderer();
    public static final ExpanderInvTierFourRenderer expanderInvTierFourRenderer = new ExpanderInvTierFourRenderer();
    public static final ExpanderInvTierFiveRenderer expanderInvTierFiveRenderer = new ExpanderInvTierFiveRenderer();

    public static final LeverRenderer leverRenderer = new LeverRenderer();

    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry
                .bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class, disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(PotatoCannonTurretTileEntity.class, potatoCannonTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class, grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RailGunTurretTileEntity.class, railGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(IncendiaryTurretTileEntity.class, incendiaryTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RelativisticTurretTileEntity.class, relativisticTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TeleporterTurretTileEntity.class, teleporterTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, leverRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderPowerTierOneTileEntity.class, expanderPowerTierOneRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderPowerTierTwoTileEntity.class, expanderPowerTierTwoRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderPowerTierThreeTileEntity.class, expanderPowerTierThreeRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderPowerTierFourTileEntity.class, expanderPowerTierFourRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderPowerTierFiveTileEntity.class, expanderPowerTierFiveRenderer);

        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierOneTileEntity.class, expanderInvTierOneRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierTwoTileEntity.class, expanderInvTierTwoRenderer);
        ClientRegistry
                .bindTileEntitySpecialRenderer(ExpanderInvTierThreeTileEntity.class, expanderInvTierThreeRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierFourTileEntity.class, expanderInvTierFourRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(ExpanderInvTierFiveTileEntity.class, expanderInvTierFiveRenderer);
    }
}
