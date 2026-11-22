package omtteam.openmodularturrets.gametest.platform.forge;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.gametest.TestBattery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The energy of a {@link TestBattery} on Forge, kept in the stack's NBT (key {@value #KEY}). */
final class ForgeTestBatteryEnergy implements ICapabilityProvider, IEnergyStorage {
    private static final String KEY = "Energy";
    private final ItemStack stack;
    private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

    ForgeTestBatteryEnergy(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        return ForgeCapabilities.ENERGY.orEmpty(capability, holder);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.max(0, Math.min(maxReceive, TestBattery.CAPACITY - getEnergyStored()));
        if (!simulate && received > 0) stack.getOrCreateTag().putInt(KEY, getEnergyStored() + received);
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.max(0, Math.min(maxExtract, getEnergyStored()));
        if (!simulate && extracted > 0) stack.getOrCreateTag().putInt(KEY, getEnergyStored() - extracted);
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return stack.hasTag() ? stack.getOrCreateTag().getInt(KEY) : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return TestBattery.CAPACITY;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
