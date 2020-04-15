package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.*;

public class TileEntityRenderers {
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public static void init() {
        final DisposableItemTurretRenderer disposableItemTurretRenderer = new DisposableItemTurretRenderer();
        final PotatoCannonTurretRenderer potatoCannonTurretRenderer = new PotatoCannonTurretRenderer();
        final RocketTurretRenderer rocketTurretRenderer = new RocketTurretRenderer();
        final MachineGunTurretRenderer machineGunTurretRenderer = new MachineGunTurretRenderer();
        final GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer = new GrenadeLauncherTurretRenderer();
        final LaserTurretRenderer laserTurretRenderer = new LaserTurretRenderer();
        final RailGunTurretRenderer railGunTurretRenderer = new RailGunTurretRenderer();
        final IncendiaryTurretRenderer incendiaryTurretRenderer = new IncendiaryTurretRenderer();
        final RelativisticTurretRenderer relativisticTurretRenderer = new RelativisticTurretRenderer();
        final TeleporterTurretRenderer teleporterTurretRenderer = new TeleporterTurretRenderer();

        final LeverRenderer leverRenderer = new LeverRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class,
                                                     disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(PotatoCannonTurretTileEntity.class, potatoCannonTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class,
                                                     grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RailGunTurretTileEntity.class, railGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(PlasmaLauncherTurretTileEntity.class, grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(IncendiaryTurretTileEntity.class, incendiaryTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RelativisticTurretTileEntity.class, relativisticTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TeleporterTurretTileEntity.class, teleporterTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, leverRenderer);
    }
}
