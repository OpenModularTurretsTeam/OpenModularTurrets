package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.blocks.BlockBaseAddon;
import omtteam.openmodularturrets.blocks.BlockExpander;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.LeverBlock;
import omtteam.openmodularturrets.blocks.turretheads.*;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.BaseAddon;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.*;
import omtteam.openmodularturrets.util.TurretType;

import static omtteam.omlib.util.InitHelper.registerBlock;

public class ModBlocks {
    public static final Block laserTurret = new BlockLaserTurret();
    public static Block turretBase;
    public static Block disposableItemTurret;
    public static Block potatoCannonTurret;
    public static Block incendiaryTurret;
    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;
    public static Block relativisticTurret;
    public static Block teleporterTurret;
    public static Block rocketTurret;
    public static Block railGunTurret;
    public static Block leverBlock;

    public static Block expander;
    public static Block baseAddon;

    public static void initBlocks(IForgeRegistry<Block> registry) {
        turretBase = registerBlock(new BlockTurretBase(), registry, ModItems.subblocks);
        expander = registerBlock(new BlockExpander(), registry, ModItems.subblocks);
        baseAddon = registerBlock(new BlockBaseAddon(), registry, ModItems.subblocks);

        if (OMTConfig.TURRETS.disposable_turret.isEnabled()) {
            disposableItemTurret = registerBlock(new BlockDisposableTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.disposableItemTurret, OMTConfig.TURRETS.disposable_turret));
        }

        if (OMTConfig.TURRETS.potato_cannon_turret.isEnabled()) {
            potatoCannonTurret = registerBlock(new BlockPotatoCannonTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.potatoCannonTurret, OMTConfig.TURRETS.potato_cannon_turret));
        }

        if (OMTConfig.TURRETS.machine_gun_turret.isEnabled()) {
            machineGunTurret = registerBlock(new BlockGunTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.gunTurret, OMTConfig.TURRETS.machine_gun_turret));
        }

        if (OMTConfig.TURRETS.incendiary_turret.isEnabled()) {
            incendiaryTurret = registerBlock(new BlockIncendiaryTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.incendiaryTurret, OMTConfig.TURRETS.incendiary_turret));
        }

        if (OMTConfig.TURRETS.laser_turret.isEnabled()) {
            grenadeLauncherTurret = registerBlock(new BlockGrenadeTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.grenadeTurret, OMTConfig.TURRETS.laser_turret));
        }

        if (OMTConfig.TURRETS.relativistic_turret.isEnabled()) {
            relativisticTurret = registerBlock(new BlockRelativisticTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.relativisticTurret, OMTConfig.TURRETS.relativistic_turret));
        }

        if (OMTConfig.TURRETS.rocket_turret.isEnabled()) {
            rocketTurret = registerBlock(new BlockRocketTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.rocketTurret, OMTConfig.TURRETS.rocket_turret));
        }

        if (OMTConfig.TURRETS.teleporter_turret.isEnabled()) {
            teleporterTurret = registerBlock(new BlockTeleporterTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.teleporterTurret, OMTConfig.TURRETS.teleporter_turret));
        }

        if (OMTConfig.TURRETS.laser_turret.isEnabled()) {
            registerBlock(laserTurret, registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.laserTurret, OMTConfig.TURRETS.laser_turret));
        }

        if (OMTConfig.TURRETS.railgun_turret.isEnabled()) {
            railGunTurret = registerBlock(new BlockRailGunTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.railGunTurret, OMTConfig.TURRETS.railgun_turret));
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
