package omtteam.openmodularturrets.registry;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import omtteam.omlib.util.InitHelper;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.block.base.BlockTurretBase;
import omtteam.openmodularturrets.block.misc.BlockGenerator;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;
import omtteam.openmodularturrets.tileentity.misc.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The blocks of this mod. Turrets are one {@link BlockTurret} per turret type, registered under the 3.x ids.
 */
public class ModBlocks {
    public static Block laserTurret;
    private static final Map<TurretType, Block> TURRETS = new HashMap<>();
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

    public static Block generator;

    public static void initBlocks(IForgeRegistry<Block> registry) {
        turretBase = InitHelper.registerBlock(new BlockTurretBase(), registry, ModItems.subblocks);
        generator = InitHelper.registerBlock(new BlockGenerator(), registry, ModItems.subblocks);

        disposableItemTurret = registerTurret(registry, OMTNames.Blocks.disposableItemTurret,
                TurretType.DISPOSABLE_TURRET);
        potatoCannonTurret = registerTurret(registry, OMTNames.Blocks.potatoCannonTurret,
                TurretType.POTATO_CANNON_TURRET);
        machineGunTurret = registerTurret(registry, OMTNames.Blocks.machineGunTurret, TurretType.MACHINE_GUN_TURRET);
        incendiaryTurret = registerTurret(registry, OMTNames.Blocks.incendiaryTurret, TurretType.INCENDIARY_TURRET);
        grenadeLauncherTurret = registerTurret(registry, OMTNames.Blocks.grenadeTurret, TurretType.GRENADE_TURRET);
        slowingTurret = registerTurret(registry, OMTNames.Blocks.slowingTurret, TurretType.SLOWING_TURRET);
        rocketTurret = registerTurret(registry, OMTNames.Blocks.rocketTurret, TurretType.ROCKET_TURRET);
        teleporterTurret = registerTurret(registry, OMTNames.Blocks.teleporterTurret, TurretType.TELEPORTER_TURRET);
        laserTurret = registerTurret(registry, OMTNames.Blocks.laserTurret, TurretType.LASER_TURRET);
        railGunTurret = registerTurret(registry, OMTNames.Blocks.railGunTurret, TurretType.RAILGUN_TURRET);
        plasmaTurret = registerTurret(registry, OMTNames.Blocks.plasmaTurret, TurretType.PLASMA_TURRET);
        arcTurret = registerTurret(registry, OMTNames.Blocks.arcTurret, TurretType.ARC_TURRET);
        meleeTurret = registerTurret(registry, OMTNames.Blocks.meleeTurret, TurretType.MELEE_TURRET);
        crossbowTurret = registerTurret(registry, OMTNames.Blocks.crossbowTurret, TurretType.CROSSBOW_TURRET);

        registerAddonTurrets(registry);

        leverBlock = InitHelper.registerBlock(new LeverBlock(), registry, ModItems.subblocks);
    }

    /** Registers the block of an enabled built-in turret under its 3.x id; null if the turret is disabled. */
    @Nullable
    private static Block registerTurret(IForgeRegistry<Block> registry, String name, TurretType type) {
        return registerTurret(registry, new ResourceLocation(Reference.MOD_ID, name), type);
    }

    @Nullable
    private static Block registerTurret(IForgeRegistry<Block> registry, ResourceLocation id, TurretType type) {
        if (!ConfigLogicSettings.INSTANCE.turret(type).enabled()) {
            return null;
        }
        // Forge checks the namespace against the active mod already when the registry name is set
        Block[] block = new Block[1];
        ModContainerScope.registerAs(id, () ->
                block[0] = InitHelper.registerBlock(new BlockTurret(id, type), registry, ModItems.subblocks));
        TURRETS.put(type, block[0]);
        return block[0];
    }

    /** The turret blocks of other mods' turrets, under the ids of their types. */
    private static void registerAddonTurrets(IForgeRegistry<Block> registry) {
        for (TurretDefinition turret : TurretRegistry.turrets()) {
            TurretType type = turret.type();
            if (!type.isBuiltIn()) {
                registerTurret(registry, new ResourceLocation(type.getNamespace(), type.getPath()), type);
            }
        }
    }

    /** The block of a turret type, null if the turret is disabled. */
    @Nullable
    public static Block turret(TurretType type) {
        return TURRETS.get(type);
    }

    public static void initTileEntities() {
        InitHelper.registerTileEntity(TurretBase.class, Reference.MOD_ID, OMTNames.Blocks.turretBase);
        InitHelper.registerTileEntity(TurretTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.turret);
        InitHelper.registerTileEntity(LeverTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.lever);
        InitHelper.registerTileEntity(GeneratorTileEntity.class, Reference.MOD_ID, OMTNames.Blocks.generator);
    }
}
