package omtteam.openmodularturrets.camo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * The disguise of a {@link CamoHost} and its NBT form, shared by all hosts of a mod.
 */
public final class CamoData {
    private final String key;
    private final Predicate<BlockState> valid;
    @Nullable
    private BlockState camo;

    /**
     * @param key   the name of the disguise in the block entity's NBT
     * @param valid which stored disguises are still accepted when read (air never is)
     */
    public CamoData(String key, Predicate<BlockState> valid) {
        this.key = key;
        this.valid = valid;
    }

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
            tag.put(key, NbtUtils.writeBlockState(camo));
        }
    }

    /** @return whether the disguise changed */
    public boolean read(CompoundTag tag) {
        BlockState state = null;
        if (NbtCompat.hasCompound(tag, key)) {
            BlockState read = NbtCompat.readBlockState(NbtCompat.getCompound(tag, key));
            state = !read.isAir() && valid.test(read) ? read : null;
        }
        return set(state);
    }
}
