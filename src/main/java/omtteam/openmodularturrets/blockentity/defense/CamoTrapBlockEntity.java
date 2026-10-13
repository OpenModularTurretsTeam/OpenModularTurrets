package omtteam.openmodularturrets.blockentity.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

/**
 * The camouflage trap (OMPD {@code TileEntityCamo}): owner plus the block it looks like. Without camouflage it shows
 * its own texture.
 */
public class CamoTrapBlockEntity extends OwnedBlockEntity {
    @Nullable
    private BlockState camo;

    public CamoTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMO_TRAP, pos, state);
    }

    /** The disguise, or {@code null} for the trap's own look. */
    @Nullable
    public BlockState getCamo() {
        return camo;
    }

    public void setCamo(@Nullable BlockState camo) {
        this.camo = camo;
        syncToClient();
    }

    @Override
    protected void readData(CompoundTag tag) {
        super.readData(tag);
        BlockState previous = camo;
        camo = tag.getCompound("Camo").map(camoTag -> NbtUtils.readBlockState(BuiltInRegistries.BLOCK, camoTag)).orElse(null);
        // the renderer draws the camouflage; make the client re-render the block when it changed
        if (level != null && level.isClientSide() && previous != camo) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    protected void writeData(CompoundTag tag) {
        super.writeData(tag);
        if (camo != null) {
            tag.put("Camo", NbtUtils.writeBlockState(camo));
        }
    }
}
