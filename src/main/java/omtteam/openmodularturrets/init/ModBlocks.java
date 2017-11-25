package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import omtteam.openmodularturrets.blocks.BlockBaseAddon;
import omtteam.openmodularturrets.blocks.BlockExpander;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.LeverBlock;
import omtteam.openmodularturrets.blocks.turretheads.*;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.BaseAddon;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.*;

import static omtteam.omlib.util.InitHelper.registerBlock;


public class ModBlocks {
    public static Block turretBase;
    public static Block disposableItemTurret;
    public static Block potatoCannonTurret;

    public static Block incendiaryTurret;

    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;

    public static Block relativisticTurret;

    public static Block teleporterTurret;

    public static Block rocketTurret;
    public static Block laserTurret;
    public static Block railGunTurret;
    public static Block leverBlock;

    public static Block expander;
    public static Block baseAddon;

    public static void initBlocks() {
        turretBase = registerBlock(new BlockTurretBase());
        expander = registerBlock(new BlockExpander());
        baseAddon = registerBlock(new BlockBaseAddon());

        if (OMTConfigHandler.getDisposableTurretSettings().isEnabled()) {
            disposableItemTurret = registerBlock(new BlockDisposableTurret());
        }

        if (OMTConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            potatoCannonTurret = registerBlock(new BlockPotatoCannonTurret());
        }

        if (OMTConfigHandler.getGunTurretSettings().isEnabled()) {
            machineGunTurret = registerBlock(new BlockGunTurret());
        }

        if (OMTConfigHandler.getIncendiaryTurretSettings().isEnabled()) {
            incendiaryTurret = registerBlock(new BlockIncendiaryTurret());
        }

        if (OMTConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            grenadeLauncherTurret = registerBlock(new BlockGrenadeTurret());
        }

        if (OMTConfigHandler.getRelativisticTurretSettings().isEnabled()) {
            relativisticTurret = registerBlock(new BlockRelativisticTurret());
        }

        if (OMTConfigHandler.getRocketTurretSettings().isEnabled()) {
            rocketTurret = registerBlock(new BlockRocketTurret());
        }

        if (OMTConfigHandler.getTeleporterTurretSettings().isEnabled()) {
            teleporterTurret = registerBlock(new BlockTeleporterTurret());
        }

        if (OMTConfigHandler.getLaserTurretSettings().isEnabled()) {
            laserTurret = registerBlock(new BlockLaserTurret());
        }

        if (OMTConfigHandler.getRailgunTurretSettings().isEnabled()) {
            railGunTurret = registerBlock(new BlockRailGunTurret());
        }

        leverBlock = registerBlock(new LeverBlock());
    }

    public static void initTileEntities() {
        GameRegistry.registerTileEntity(TurretBase.class, OMTNames.Blocks.turretBase);
        GameRegistry.registerTileEntity(DisposableItemTurretTileEntity.class, OMTNames.Blocks.disposableItemTurret);
        GameRegistry.registerTileEntity(PotatoCannonTurretTileEntity.class, OMTNames.Blocks.potatoCannonTurret);
        GameRegistry.registerTileEntity(RocketTurretTileEntity.class, OMTNames.Blocks.rocketTurret);
        GameRegistry.registerTileEntity(GunTurretTileEntity.class, OMTNames.Blocks.gunTurret);
        GameRegistry.registerTileEntity(GrenadeLauncherTurretTileEntity.class, OMTNames.Blocks.grenadeTurret);
        GameRegistry.registerTileEntity(LaserTurretTileEntity.class, OMTNames.Blocks.laserTurret);
        GameRegistry.registerTileEntity(LeverTileEntity.class, OMTNames.Blocks.lever);
        GameRegistry.registerTileEntity(RailGunTurretTileEntity.class, OMTNames.Blocks.railGunTurret);
        GameRegistry.registerTileEntity(IncendiaryTurretTileEntity.class, OMTNames.Blocks.incendiaryTurret);
        GameRegistry.registerTileEntity(RelativisticTurretTileEntity.class, OMTNames.Blocks.relativisticTurret);
        GameRegistry.registerTileEntity(TeleporterTurretTileEntity.class, OMTNames.Blocks.teleporterTurret);
        GameRegistry.registerTileEntity(Expander.class, OMTNames.Blocks.expander);
        GameRegistry.registerTileEntity(BaseAddon.class, OMTNames.Blocks.baseAddon);
    }
}
