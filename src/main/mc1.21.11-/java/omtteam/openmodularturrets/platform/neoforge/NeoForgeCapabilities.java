package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.inventory.ItemAccess;
import omtteam.openmodularturrets.registry.ModBlockEntities;

import java.util.function.BiFunction;

/**
 * Item and energy capabilities of the turret base and the generator, on all sides, through NeoForge's transfer API
 * ({@code ResourceHandler<ItemResource>} and {@code EnergyHandler}; the same as the Fabric lookups).
 */
public final class NeoForgeCapabilities {
    private NeoForgeCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        registerItems(event, ModBlockEntities.TURRET_BASE, (base, side) -> base.getItemAccess(side));
        registerEnergy(event, ModBlockEntities.TURRET_BASE, (base, side) -> base.getEnergyAccess(side));
        registerItems(event, ModBlockEntities.GENERATOR, (generator, side) -> generator.getItemAccess(side));
        registerEnergy(event, ModBlockEntities.GENERATOR, (generator, side) -> generator.getEnergyAccess(side));
    }

    private static <T extends BlockEntity> void registerItems(RegisterCapabilitiesEvent event, BlockEntityType<T> type,
                                                              BiFunction<T, Direction, ItemAccess> access) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, type,
                (blockEntity, side) -> new NeoForgeItemHandler(access.apply(blockEntity, side)));
    }

    private static <T extends BlockEntity> void registerEnergy(RegisterCapabilitiesEvent event, BlockEntityType<T> type,
                                                               BiFunction<T, Direction, EnergyAccess> access) {
        event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (blockEntity, side) -> {
            EnergyAccess energy = access.apply(blockEntity, side);
            return energy == null ? null : NeoForgeEnergyHandler.of(energy);
        });
    }
}
