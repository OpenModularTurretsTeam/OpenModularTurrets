package omtteam.openmodularturrets.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import omtteam.openmodularturrets.OMT;
import omtteam.omlib.blockentity.TransferHost;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.LeverBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.omlib.compat.VersionCompat;

import java.util.List;
import java.util.stream.Stream;

/**
 * All block entity types. Their item and energy access is registered by the loader ({@code platform/<loader>}).
 */
public final class ModBlockEntities {
    public static final BlockEntityType<TurretBaseBlockEntity> TURRET_BASE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("turret_base"),
            VersionCompat.blockEntityType(TurretBaseBlockEntity::new, ModBlocks.TURRET_BASES.toArray(Block[]::new)));

    public static final BlockEntityType<TurretBlockEntity> TURRET = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("turret"),
            VersionCompat.blockEntityType(TurretBlockEntity::new, ModBlocks.TURRETS.values().toArray(Block[]::new)));

    /** OMPD hardened blocks, walls and fences (owner only). */
    public static final BlockEntityType<OwnedBlockEntity> OWNED = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("owned_block"),
            VersionCompat.blockEntityType(OwnedBlockEntity::new, Stream.of(ModBlocks.HARDENED, ModBlocks.WALLS, ModBlocks.FENCES)
                    .flatMap(List::stream).toArray(Block[]::new)));
    public static final BlockEntityType<CamoTrapBlockEntity> CAMO_TRAP = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("camo_trap"),
            VersionCompat.blockEntityType(CamoTrapBlockEntity::new, ModBlocks.CAMO_TRAP));
    public static final BlockEntityType<LeverBlockEntity> LEVER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("lever"),
            VersionCompat.blockEntityType(LeverBlockEntity::new, ModBlocks.LEVER));
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("generator"),
            VersionCompat.blockEntityType(GeneratorBlockEntity::new, ModBlocks.GENERATOR));

    private ModBlockEntities() {
    }

    /** Loads the class, registering all block entity types. */
    public static void init() {
    }

    /** The block entities whose items and energy other blocks may use; each loader registers them. */
    public static List<BlockEntityType<? extends TransferHost>> transferTypes() {
        return List.of(TURRET_BASE, GENERATOR);
    }
}
