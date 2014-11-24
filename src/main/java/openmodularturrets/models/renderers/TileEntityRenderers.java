package openmodularturrets.models.renderers;

import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRenderers {
    public static DisposableItemTurretRenderer disposableItemTurretRenderer = new DisposableItemTurretRenderer();
    public static RocketTurretRenderer rocketTurretRenderer = new RocketTurretRenderer();
    public static MachineGunTurretRenderer machineGunTurretRenderer = new MachineGunTurretRenderer();
    public static GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer = new GrenadeLauncherTurretRenderer();
    public static LaserTurretRenderer laserTurretRenderer = new LaserTurretRenderer();
    public static LeverRenderer leverRenderer = new LeverRenderer();

    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class, disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(MachineGunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class, grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, leverRenderer);
    }
}
