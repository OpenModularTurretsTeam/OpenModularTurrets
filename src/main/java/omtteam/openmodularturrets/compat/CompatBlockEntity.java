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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Version-independent base class for all mod block entities.
 * <p>
 * Subclasses override {@link #readData} / {@link #writeData} (a plain NBT compound, saved under {@code "Data"})
 * instead of the vanilla {@code ValueInput}/{@code ValueOutput} methods. Everything written by {@link #writeData} is
 * also sent to clients (initial chunk data and {@link #syncToClient()}); override {@link #writeClientData} to send
 * less. Inventories are saved separately through {@link #readItems} / {@link #writeItems} (they need registry
 * access) and are never sent to clients this way.
 */
public abstract class CompatBlockEntity extends BlockEntity {
    private static final String DATA = "Data";

    protected CompatBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Loads state from NBT. Also called on the client for synced data.
     */
    protected abstract void readData(CompoundTag tag);

    /**
     * Saves state to NBT.
     */
    protected abstract void writeData(CompoundTag tag);

    /**
     * Data sent to clients. Defaults to the full saved data.
     */
    protected void writeClientData(CompoundTag tag) {
        writeData(tag);
    }

    /** Loads inventories saved by {@link #writeItems}. Client updates carry no items. */
    protected void readItems(ValueInput input) {
    }

    /** Saves inventories (vanilla container format). */
    protected void writeItems(ValueOutput output) {
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

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        readData(input.read(DATA, CompoundTag.CODEC).orElseGet(CompoundTag::new));
        readItems(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        CompoundTag tag = new CompoundTag();
        writeData(tag);
        output.store(DATA, CompoundTag.CODEC, tag);
        writeItems(output);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag data = new CompoundTag();
        writeClientData(data);
        CompoundTag tag = new CompoundTag();
        tag.put(DATA, data);
        return tag;
    }
}
