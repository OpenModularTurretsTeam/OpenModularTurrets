package omtteam.openmodularturrets.registry;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.blockentity.misc.LeverBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;
import java.util.stream.Stream;

/**
 * All block entity types and their API lookups (items via Transfer API, energy via Team Reborn Energy).
 */
public final class ModBlockEntities {
    public static final BlockEntityType<TurretBaseBlockEntity> TURRET_BASE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("turret_base"),
            BlockEntityType.Builder.of(TurretBaseBlockEntity::new, ModBlocks.TURRET_BASES.toArray(Block[]::new)).build(null));

    public static final BlockEntityType<TurretBlockEntity> TURRET = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("turret"),
            BlockEntityType.Builder.of(TurretBlockEntity::new, ModBlocks.TURRETS.values().toArray(Block[]::new)).build(null));

    /** OMPD hardened blocks, walls and fences (owner only). */
    public static final BlockEntityType<OwnedBlockEntity> OWNED = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("owned_block"),
            BlockEntityType.Builder.of(OwnedBlockEntity::new, Stream.of(ModBlocks.HARDENED, ModBlocks.WALLS, ModBlocks.FENCES)
                    .flatMap(List::stream).toArray(Block[]::new)).build(null));
    public static final BlockEntityType<CamoTrapBlockEntity> CAMO_TRAP = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("camo_trap"),
            BlockEntityType.Builder.of(CamoTrapBlockEntity::new, ModBlocks.CAMO_TRAP).build(null));
    public static final BlockEntityType<LeverBlockEntity> LEVER = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("lever"),
            BlockEntityType.Builder.of(LeverBlockEntity::new, ModBlocks.LEVER).build(null));
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, OMT.id("generator"),
            BlockEntityType.Builder.of(GeneratorBlockEntity::new, ModBlocks.GENERATOR).build(null));

    private ModBlockEntities() {
    }

    public static void init() {
        // Exposed on all sides, like oldmod
        ItemStorage.SIDED.registerForBlockEntity((base, side) -> base.getAmmoStorage(), TURRET_BASE);
        EnergyStorage.SIDED.registerForBlockEntity((base, side) -> base.getEnergyStorage(), TURRET_BASE);
        ItemStorage.SIDED.registerForBlockEntity((generator, side) -> generator.getItemStorage(), GENERATOR);
        EnergyStorage.SIDED.registerForBlockEntity((generator, side) -> generator.getEnergyStorage(), GENERATOR);
    }
}
