package openmodularturrets.client.render.renderers.blockitem;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turrets.*;

public class TileEntityRenderers {
    public static DisposableItemTurretRenderer disposableItemTurretRenderer = new DisposableItemTurretRenderer();
    public static RocketTurretRenderer rocketTurretRenderer = new RocketTurretRenderer();
    public static MachineGunTurretRenderer machineGunTurretRenderer = new MachineGunTurretRenderer();
    public static GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer = new GrenadeLauncherTurretRenderer();
    public static LaserTurretRenderer laserTurretRenderer = new LaserTurretRenderer();
    public static RailGunTurretRenderer railGunTurretRenderer = new RailGunTurretRenderer();
    public static LeverRenderer leverRenderer = new LeverRenderer();

    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class, disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(MachineGunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class, grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RailGunTurretTileEntity.class, railGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, leverRenderer);
    }
}
