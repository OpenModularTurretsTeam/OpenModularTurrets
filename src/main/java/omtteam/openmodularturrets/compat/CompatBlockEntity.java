package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?}

/**
 * Version-independent base class for all mod block entities.
 * <p>
 * Subclasses override {@link #readData} / {@link #writeData} instead of the vanilla NBT methods, whose
 * signatures differ between 1.20.1 and 1.21. Everything written by {@link #writeData} is also sent to
 * clients (initial chunk data and {@link #syncToClient()}); override {@link #writeClientData} to send less.
 */
public abstract class CompatBlockEntity extends BlockEntity {
    protected CompatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Loads state from NBT. Also called on the client for synced data.
     */
    protected abstract void readData(CompoundTag tag, HolderLookup.Provider registries);

    /**
     * Saves state to NBT.
     */
    protected abstract void writeData(CompoundTag tag, HolderLookup.Provider registries);

    /**
     * Data sent to clients. Defaults to the full saved data.
     */
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
        writeData(tag, registries);
    }

    /**
     * Marks the block entity dirty and sends its client data to all tracking players.
     */
    public void syncToClient() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //? if >=1.21 {
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readData(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeData(tag, registries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        writeClientData(tag, registries);
        return tag;
    }
    //?} else {
    /*@Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readData(tag, registries());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writeData(tag, registries());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        writeClientData(tag, registries());
        return tag;
    }

    // Only passed through on 1.20.1; the level may not be set yet while loading.
    private HolderLookup.Provider registries() {
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }
    *///?}
}
