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

    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class, new DisposableItemTurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, new RocketTurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(MachineGunTurretTileEntity.class, new MachineGunTurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class, new GrenadeLauncherTurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, new LaserTurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, new LeverRenderer());

    }
}
