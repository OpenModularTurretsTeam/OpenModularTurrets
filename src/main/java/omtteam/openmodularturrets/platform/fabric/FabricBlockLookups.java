package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

/**
 * Fabric API lookups of the turret base and the generator: items and energy on all sides, as in 1.12.2.
 */
public final class FabricBlockLookups {
    private FabricBlockLookups() {
    }

    public static void register() {
        ItemStorage.SIDED.registerForBlockEntity((base, side) -> FabricItemStorages.of(base.getItemAccess(side)),
                ModBlockEntities.TURRET_BASE);
        ItemStorage.SIDED.registerForBlockEntity((generator, side) -> FabricItemStorages.of(generator.getItemAccess(side)),
                ModBlockEntities.GENERATOR);
        registerEnergy(ModBlockEntities.TURRET_BASE, (base, side) -> base.getEnergyAccess(side));
        registerEnergy(ModBlockEntities.GENERATOR, (generator, side) -> generator.getEnergyAccess(side));
    }

    private static <T extends BlockEntity> void registerEnergy(BlockEntityType<T> type,
                                                               BiFunction<T, Direction, EnergyAccess> access) {
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, side) -> {
            EnergyAccess energy = access.apply(blockEntity, side);
            return energy == null ? null : FabricEnergyStorages.of(energy);
        }, type);
    }
}
