package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;

import omtteam.openmodularturrets.tileentity.turrets.*;

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

    public static final LeverRenderer leverRenderer = new LeverRenderer();

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(DisposableItemTurretTileEntity.class,
                disposableItemTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(PotatoCannonTurretTileEntity.class, potatoCannonTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RocketTurretTileEntity.class, rocketTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GunTurretTileEntity.class, machineGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(GrenadeLauncherTurretTileEntity.class,
                grenadeLauncherTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LaserTurretTileEntity.class, laserTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RailGunTurretTileEntity.class, railGunTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(IncendiaryTurretTileEntity.class, incendiaryTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(RelativisticTurretTileEntity.class, relativisticTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TeleporterTurretTileEntity.class, teleporterTurretRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, leverRenderer);

    }
}
