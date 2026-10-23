package omtteam.openmodularturrets.platform.forge;

import net.minecraftforge.energy.IEnergyStorage;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.energy.EnergyStore;

/**
 * Forge view ({@link IEnergyStorage}, FE, {@code int}) of an {@link EnergyAccess}; 1 E = 1 FE, larger amounts are
 * clamped to {@link Integer#MAX_VALUE}.
 */
public final class ForgeEnergyHandler implements IEnergyStorage {
    private final EnergyAccess access;

    public ForgeEnergyHandler(EnergyAccess access) {
        this.access = access;
    }

    static int clamp(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, amount));
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return clamp(access.insert(toReceive, simulate));
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return clamp(access.extract(toExtract, simulate));
    }

    @Override
    public int getEnergyStored() {
        return clamp(access.getAmount());
    }

    @Override
    public int getMaxEnergyStored() {
        return clamp(access.getCapacity());
    }

    /** A store: its {@code maxExtract}; other views: whether anything could be taken now. */
    @Override
    public boolean canExtract() {
        return access instanceof EnergyStore store ? store.getMaxExtract() > 0 : access.extract(1, true) > 0;
    }

    /** A store: its {@code maxInsert}; other views always accept energy in principle. */
    @Override
    public boolean canReceive() {
        return !(access instanceof EnergyStore store) || store.getMaxInsert() > 0;
    }
}
