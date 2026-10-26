package omtteam.openmodularturrets.platform.forge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TransferHost;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.inventory.ItemAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Items and energy of a block entity for other mods (Forge capabilities, attached in {@code AttachCapabilitiesEvent};
 * the counterpart of the Fabric lookups and NeoForge's block capabilities). Forge attaches it while the block entity is
 * constructed, so the block entity is only asked on a query, and on every query: a side keeps its {@link LazyOptional}
 * while the block entity returns the same access object, and the old one is invalidated when that changes or the block
 * entity is removed ({@link #invalidate}).
 */
final class ForgeBlockCapabilities implements ICapabilityProvider {
    private final Function<Direction, ItemAccess> items;
    private final Function<Direction, EnergyAccess> energy;
    private final SideCache itemCache = new SideCache();
    private final SideCache energyCache = new SideCache();

    /** Both functions take the side ({@code null}: none) and may return {@code null} for nothing on that side. */
    ForgeBlockCapabilities(Function<Direction, ItemAccess> items, Function<Direction, EnergyAccess> energy) {
        this.items = items;
        this.energy = energy;
    }

    /** Attaches the capabilities to every {@link TransferHost} (listens to {@code AttachCapabilitiesEvent<BlockEntity>}). */
    static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof TransferHost host) {
            ForgeBlockCapabilities capabilities = new ForgeBlockCapabilities(host::getItemAccess, host::getEnergyAccess);
            event.addCapability(OMT.id("transfer"), capabilities);
            event.addListener(capabilities::invalidate);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCache.get(side, items.apply(side), ForgeItemHandler::new).cast();
        }
        if (capability == ForgeCapabilities.ENERGY) {
            return energyCache.get(side, energy.apply(side), ForgeEnergyHandler::new).cast();
        }
        return LazyOptional.empty();
    }

    void invalidate() {
        itemCache.invalidate();
        energyCache.invalidate();
    }

    /** The {@link LazyOptional} per side (index 6: no side) and the access object it wraps. */
    private static final class SideCache {
        private final Object[] accesses = new Object[7];
        private final LazyOptional<?>[] optionals = new LazyOptional<?>[7];

        <A> LazyOptional<?> get(@Nullable Direction side, @Nullable A access, Function<A, ?> adapter) {
            int index = side == null ? 6 : side.ordinal();
            if (optionals[index] == null || accesses[index] != access) {
                if (optionals[index] != null) optionals[index].invalidate();
                accesses[index] = access;
                optionals[index] = access == null ? LazyOptional.empty() : LazyOptional.of(() -> adapter.apply(access));
            }
            return optionals[index];
        }

        void invalidate() {
            for (int index = 0; index < optionals.length; index++) {
                if (optionals[index] != null) optionals[index].invalidate();
                optionals[index] = null;
                accesses[index] = null;
            }
        }
    }
}
