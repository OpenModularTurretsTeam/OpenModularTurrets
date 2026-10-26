package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import omtteam.openmodularturrets.blockentity.TransferHost;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import team.reborn.energy.api.EnergyStorage;

/**
 * Fabric API lookups of the {@link TransferHost}s: items and energy, on the sides each host exposes.
 */
public final class FabricBlockLookups {
    private FabricBlockLookups() {
    }

    public static void register() {
        for (BlockEntityType<? extends TransferHost> type : ModBlockEntities.transferTypes()) {
            registerType(type);
        }
    }

    private static <T extends BlockEntity & TransferHost> void registerType(BlockEntityType<T> type) {
        ItemStorage.SIDED.registerForBlockEntity((host, side) -> FabricItemStorages.of(host.getItemAccess(side)), type);
        EnergyStorage.SIDED.registerForBlockEntity((host, side) -> {
            EnergyAccess access = host.getEnergyAccess(side);
            return access == null ? null : FabricEnergyStorages.of(access);
        }, type);
    }
}
