package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import omtteam.openmodularturrets.blockentity.TransferHost;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.registry.ModBlockEntities;

/**
 * Item and energy capabilities of the {@link TransferHost}s, on the sides each host exposes, through NeoForge's
 * transfer API ({@code ResourceHandler<ItemResource>} and {@code EnergyHandler}; the same as the Fabric lookups).
 */
public final class NeoForgeCapabilities {
    private NeoForgeCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        for (BlockEntityType<? extends TransferHost> type : ModBlockEntities.transferTypes()) {
            registerType(event, type);
        }
    }

    private static <T extends BlockEntity & TransferHost> void registerType(RegisterCapabilitiesEvent event,
                                                                            BlockEntityType<T> type) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, type,
                (host, side) -> new NeoForgeItemHandler(host.getItemAccess(side)));
        event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (host, side) -> {
            EnergyAccess access = host.getEnergyAccess(side);
            return access == null ? null : NeoForgeEnergyHandler.of(access);
        });
    }
}
