package modularTurrets.models.renderers;

import modularTurrets.tileentity.LeverTileEntity;
import modularTurrets.tileentity.turrets.DisposableItemTurretTileEntity;
import modularTurrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import modularTurrets.tileentity.turrets.LaserTurretTileEntity;
import modularTurrets.tileentity.turrets.MachineGunTurretTileEntity;
import modularTurrets.tileentity.turrets.RocketTurretTileEntity;
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
