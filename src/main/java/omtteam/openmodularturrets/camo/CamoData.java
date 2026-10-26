package omtteam.openmodularturrets.camo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
import org.jetbrains.annotations.Nullable;

/**
 * The disguise of a {@link CamoHost} and its NBT form, shared by all hosts.
 */
public final class CamoData {
    private static final String KEY = "Camo";

    @Nullable
    private BlockState camo;

    @Nullable
    public BlockState get() {
        return camo;
    }

    /** @return whether the disguise changed */
    public boolean set(@Nullable BlockState state) {
        if (camo == state) return false;
        camo = state;
        return true;
    }

    public void write(CompoundTag tag) {
        if (camo != null) {
            tag.put(KEY, NbtUtils.writeBlockState(camo));
        }
    }

    /** @return whether the disguise changed */
    public boolean read(CompoundTag tag) {
        BlockState state = null;
        if (NbtCompat.hasCompound(tag, KEY)) {
            BlockState read = NbtCompat.readBlockState(NbtCompat.getCompound(tag, KEY));
            state = read.isAir() ? null : read;
        }
        return set(state);
    }

    /** Makes the client redraw the block after its disguise changed (the model reads it while meshing the chunk). */
    public static void remeshIfChanged(CompatBlockEntity blockEntity, boolean changed) {
        if (changed) {
            blockEntity.remesh();
        }
    }
}
