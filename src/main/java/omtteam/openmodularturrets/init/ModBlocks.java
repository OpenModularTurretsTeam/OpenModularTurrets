package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
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
    public static Block laserTurret = new BlockLaserTurret();
    public static Block railGunTurret;
    public static Block leverBlock;

    public static Block expander;
    public static Block baseAddon;


    public static void initBlocks(IForgeRegistry<Block> registry) {
        turretBase = registerBlock(new BlockTurretBase(), registry, ModItems.subblocks);
        expander = registerBlock(new BlockExpander(), registry, ModItems.subblocks);
        baseAddon = registerBlock(new BlockBaseAddon(), registry, ModItems.subblocks);

        if (OMTConfigHandler.getDisposableTurretSettings().isEnabled()) {
            disposableItemTurret = registerBlock(new BlockDisposableTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getPotatoCannonTurretSettings().isEnabled()) {
            potatoCannonTurret = registerBlock(new BlockPotatoCannonTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getGunTurretSettings().isEnabled()) {
            machineGunTurret = registerBlock(new BlockGunTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getIncendiaryTurretSettings().isEnabled()) {
            incendiaryTurret = registerBlock(new BlockIncendiaryTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getGrenadeTurretSettings().isEnabled()) {
            grenadeLauncherTurret = registerBlock(new BlockGrenadeTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getRelativisticTurretSettings().isEnabled()) {
            relativisticTurret = registerBlock(new BlockRelativisticTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getRocketTurretSettings().isEnabled()) {
            rocketTurret = registerBlock(new BlockRocketTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getTeleporterTurretSettings().isEnabled()) {
            teleporterTurret = registerBlock(new BlockTeleporterTurret(), registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getLaserTurretSettings().isEnabled()) {
            registerBlock(laserTurret, registry, ModItems.subblocks);
        }

        if (OMTConfigHandler.getRailgunTurretSettings().isEnabled()) {
            railGunTurret = registerBlock(new BlockRailGunTurret(), registry, ModItems.subblocks);
        }

        leverBlock = registerBlock(new LeverBlock(), registry, ModItems.subblocks);
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
