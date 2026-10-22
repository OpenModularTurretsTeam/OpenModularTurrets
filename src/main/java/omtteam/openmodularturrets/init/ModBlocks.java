package omtteam.openmodularturrets.init;

import net.minecraft.block.Block;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.blocks.BlockBaseAttachment;
import omtteam.openmodularturrets.blocks.BlockPowerAttachment;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.LeverBlock;
import omtteam.openmodularturrets.blocks.turretheads.BlockTurret;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.handler.config.TurretSetting;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.BaseAddon;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.PowerGenerator;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretTileEntity;

import javax.annotation.Nullable;

/**
 * The blocks of this mod. Turrets are one {@link BlockTurret} per turret type, registered under the 3.x ids.
 */
public class ModBlocks {
    public static Block laserTurret;
    public static Block turretBase;
    public static Block disposableItemTurret;
    public static Block potatoCannonTurret;
    public static Block incendiaryTurret;
    public static Block machineGunTurret;
    public static Block grenadeLauncherTurret;
    public static Block slowingTurret;
    public static Block teleporterTurret;
    public static Block rocketTurret;
    public static Block railGunTurret;
    public static Block plasmaTurret;
    public static Block arcTurret;
    public static Block meleeTurret;
    public static Block crossbowTurret;
    public static Block leverBlock;

    public static Block baseAddon;
    public static Block powerGenerator;

    public static void initBlocks(IForgeRegistry<Block> registry) {
        turretBase = InitHelper.registerBlock(new BlockTurretBase(), registry, ModItems.subblocks);
        powerGenerator = InitHelper.registerBlock(new BlockPowerAttachment(), registry, ModItems.subblocks);
        baseAddon = InitHelper.registerBlock(new BlockBaseAttachment(), registry, ModItems.subblocks);

        disposableItemTurret = registerTurret(registry, OMTNames.Blocks.disposableItemTurret,
                OMTConfig.TURRETS.disposable_turret, TurretType.DISPOSABLE_TURRET);
        potatoCannonTurret = registerTurret(registry, OMTNames.Blocks.potatoCannonTurret,
                OMTConfig.TURRETS.potato_cannon_turret, TurretType.POTATO_CANNON_TURRET);
        machineGunTurret = registerTurret(registry, OMTNames.Blocks.machineGunTurret,
                OMTConfig.TURRETS.machine_gun_turret, TurretType.MACHINE_GUN_TURRET);
        incendiaryTurret = registerTurret(registry, OMTNames.Blocks.incendiaryTurret,
                OMTConfig.TURRETS.incendiary_turret, TurretType.INCENDIARY_TURRET);
        grenadeLauncherTurret = registerTurret(registry, OMTNames.Blocks.grenadeTurret,
                OMTConfig.TURRETS.grenade_turret, TurretType.GRENADE_TURRET);
        slowingTurret = registerTurret(registry, OMTNames.Blocks.slowingTurret,
                OMTConfig.TURRETS.slowing_turret, TurretType.SLOWING_TURRET);
        rocketTurret = registerTurret(registry, OMTNames.Blocks.rocketTurret,
                OMTConfig.TURRETS.rocket_turret, TurretType.ROCKET_TURRET);
        teleporterTurret = registerTurret(registry, OMTNames.Blocks.teleporterTurret,
                OMTConfig.TURRETS.teleporter_turret, TurretType.TELEPORTER_TURRET);
        laserTurret = registerTurret(registry, OMTNames.Blocks.laserTurret,
                OMTConfig.TURRETS.laser_turret, TurretType.LASER_TURRET);
        railGunTurret = registerTurret(registry, OMTNames.Blocks.railGunTurret,
                OMTConfig.TURRETS.railgun_turret, TurretType.RAILGUN_TURRET);
        plasmaTurret = registerTurret(registry, OMTNames.Blocks.plasmaTurret,
                OMTConfig.TURRETS.plasma_turret, TurretType.PLASMA_TURRET);
        arcTurret = registerTurret(registry, OMTNames.Blocks.arcTurret,
                OMTConfig.TURRETS.arc_turret, TurretType.ARC_TURRET);
        meleeTurret = registerTurret(registry, OMTNames.Blocks.meleeTurret,
                OMTConfig.TURRETS.melee_turret, TurretType.MELEE_TURRET);
        crossbowTurret = registerTurret(registry, OMTNames.Blocks.crossbowTurret,
                OMTConfig.TURRETS.crossbow_turret, TurretType.CROSSBOW_TURRET);

        leverBlock = InitHelper.registerBlock(new LeverBlock(), registry, ModItems.subblocks);
    }

    /** Registers the block of an enabled turret and its 3.x turret type; null if the turret is disabled. */
    @Nullable
    private static Block registerTurret(IForgeRegistry<Block> registry, String name, TurretSetting setting,
                                        TurretType type) {
        if (!setting.enabled) {
            return null;
        }
        TurretList.addTurret(new omtteam.openmodularturrets.turret.TurretType(name, setting));
        return InitHelper.registerBlock(new BlockTurret(name, type), registry, ModItems.subblocks);
    }

    public static void initTileEntities() {
        InitHelper.registerTileEntity(TurretBase.class, Reference.MOD_ID, OMTNames.Blocks.turretBase);
        InitHelper.registerTileEntity(TurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.turret);
        InitHelper.registerTileEntity(LeverTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.lever);
        InitHelper.registerTileEntity(BaseAddon.class, Reference.MOD_ID, OMTNames.Blocks.baseAddon);
        InitHelper.registerTileEntity(PowerGenerator.class, Reference.MOD_ID, OMTNames.Blocks.powerGenerator);
    }
}
