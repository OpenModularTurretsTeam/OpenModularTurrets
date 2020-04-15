package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.blocks.BlockBaseAttachment;
import omtteam.openmodularturrets.blocks.BlockExpander;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.LeverBlock;
import omtteam.openmodularturrets.blocks.turretheads.*;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.BaseAddon;
import omtteam.openmodularturrets.tileentity.Expander;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.*;
import omtteam.openmodularturrets.turret.TurretType;

public class ModBlocks {
    public static Block laserTurret;
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
    public static Block plasmaTurret;
    public static Block leverBlock;

    public static Block expander;
    public static Block baseAddon;

    public static void initBlocks(IForgeRegistry<Block> registry) {
        turretBase = InitHelper.registerBlock(new BlockTurretBase(), registry, ModItems.subblocks);
        expander = InitHelper.registerBlock(new BlockExpander(), registry, ModItems.subblocks);
        baseAddon = InitHelper.registerBlock(new BlockBaseAttachment(), registry, ModItems.subblocks);

        if (OMTConfig.TURRETS.disposable_turret.enabled) {
            disposableItemTurret = InitHelper.registerBlock(new BlockDisposableTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.disposableItemTurret, OMTConfig.TURRETS.disposable_turret));
        }

        if (OMTConfig.TURRETS.potato_cannon_turret.enabled) {
            potatoCannonTurret = InitHelper.registerBlock(new BlockPotatoCannonTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.potatoCannonTurret, OMTConfig.TURRETS.potato_cannon_turret));
        }

        if (OMTConfig.TURRETS.machine_gun_turret.enabled) {
            machineGunTurret = InitHelper.registerBlock(new BlockGunTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.machineGunTurret, OMTConfig.TURRETS.machine_gun_turret));
        }

        if (OMTConfig.TURRETS.incendiary_turret.enabled) {
            incendiaryTurret = InitHelper.registerBlock(new BlockIncendiaryTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.incendiaryTurret, OMTConfig.TURRETS.incendiary_turret));
        }

        if (OMTConfig.TURRETS.laser_turret.enabled) {
            grenadeLauncherTurret = InitHelper.registerBlock(new BlockGrenadeTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.grenadeTurret, OMTConfig.TURRETS.grenade_turret));
        }

        if (OMTConfig.TURRETS.relativistic_turret.enabled) {
            relativisticTurret = InitHelper.registerBlock(new BlockRelativisticTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.relativisticTurret, OMTConfig.TURRETS.relativistic_turret));
        }

        if (OMTConfig.TURRETS.rocket_turret.enabled) {
            rocketTurret = InitHelper.registerBlock(new BlockRocketTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.rocketTurret, OMTConfig.TURRETS.rocket_turret));
        }

        if (OMTConfig.TURRETS.teleporter_turret.enabled) {
            teleporterTurret = InitHelper.registerBlock(new BlockTeleporterTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.teleporterTurret, OMTConfig.TURRETS.teleporter_turret));
        }

        if (OMTConfig.TURRETS.laser_turret.enabled) {
            laserTurret = InitHelper.registerBlock(new BlockLaserTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.laserTurret, OMTConfig.TURRETS.laser_turret));
        }

        if (OMTConfig.TURRETS.railgun_turret.enabled) {
            railGunTurret = InitHelper.registerBlock(new BlockRailGunTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.railGunTurret, OMTConfig.TURRETS.railgun_turret));
        }

        if (OMTConfig.TURRETS.plasma_turret.enabled) {
            plasmaTurret = InitHelper.registerBlock(new BlockPlasmaTurret(), registry, ModItems.subblocks);
            TurretList.addTurret(new TurretType(OMTNames.Blocks.plasmaTurret, OMTConfig.TURRETS.plasma_turret));
        }

        leverBlock = InitHelper.registerBlock(new LeverBlock(), registry, ModItems.subblocks);
    }

    public static void initTileEntities() {
        InitHelper.registerTileEntity(TurretBase.class, Reference.MOD_ID, OMTNames.Blocks.turretBase);
        InitHelper.registerTileEntity(DisposableItemTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.disposableItemTurret);
        InitHelper.registerTileEntity(PotatoCannonTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.potatoCannonTurret);
        InitHelper.registerTileEntity(RocketTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.rocketTurret);
        InitHelper.registerTileEntity(GunTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.machineGunTurret);
        InitHelper.registerTileEntity(GrenadeLauncherTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.grenadeTurret);
        InitHelper.registerTileEntity(LaserTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.laserTurret);
        InitHelper.registerTileEntity(LeverTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.lever);
        InitHelper.registerTileEntity(RailGunTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.railGunTurret);
        InitHelper.registerTileEntity(IncendiaryTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.incendiaryTurret);
        InitHelper.registerTileEntity(RelativisticTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.relativisticTurret);
        InitHelper.registerTileEntity(TeleporterTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.teleporterTurret);
        InitHelper.registerTileEntity(PlasmaLauncherTurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.plasmaTurret);
        InitHelper.registerTileEntity(Expander.class, Reference.MOD_ID, OMTNames.Blocks.expander);
        InitHelper.registerTileEntity(BaseAddon.class, Reference.MOD_ID, OMTNames.Blocks.baseAddon);
    }
}
