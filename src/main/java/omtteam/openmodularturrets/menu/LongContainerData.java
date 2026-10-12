package omtteam.openmodularturrets.menu;

import net.minecraft.world.inventory.ContainerData;

import java.util.function.LongSupplier;

/**
 * Syncs non-negative long values (clamped to {@code Integer.MAX_VALUE}) as container data, each split into two
 * 16-bit halves because container data is sent as shorts.
 */
public final class LongContainerData implements ContainerData {
    private final LongSupplier[] values;

    public LongContainerData(LongSupplier... values) {
        this.values = values;
    }

    /** Container data size for {@code count} values (for the client-side {@code SimpleContainerData}). */
    public static int size(int count) {
        return count * 2;
    }

    /** Reads value number {@code value} from synced data. */
    public static long read(ContainerData data, int value) {
        return (data.get(value * 2) & 0xFFFF) | ((long) (data.get(value * 2 + 1) & 0xFFFF) << 16);
    }

    @Override
    public int get(int index) {
        long value = Math.max(0, Math.min(Integer.MAX_VALUE, values[index / 2].getAsLong()));
        return (int) ((index % 2 == 0 ? value : value >> 16) & 0xFFFF);
    }

    @Override
    public void set(int index, int value) {
    }

    @Override
    public int getCount() {
        return size(values.length);
    }
}
