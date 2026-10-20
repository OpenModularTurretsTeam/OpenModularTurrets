package omtteam.openmodularturrets.blockentity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.registry.ModBlockEntities;

/**
 * The crank's animation state (1.12.2 {@code LeverTileEntity}): one full turn of 30° per tick after each use.
 * The server tracks it to ignore uses during the turn; clients start it from a block event.
 */
public class LeverBlockEntity extends CompatBlockEntity {
    /** Block event id that starts the turn on clients. */
    public static final int EVENT_TURN = 1;
    /** 30° per tick, 12 ticks per turn. */
    private static final float DEGREES_PER_TICK = 30F;

    private float rotation;
    private float prevRotation;
    private boolean turning;

    public LeverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LEVER, pos, state);
    }

    public boolean isTurning() {
        return turning;
    }

    public void startTurning() {
        turning = true;
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, LeverBlockEntity lever) {
        lever.prevRotation = lever.rotation;
        if (!lever.turning) return;
        lever.rotation += DEGREES_PER_TICK;
        if (lever.rotation >= 360F) {
            lever.turning = false;
            lever.rotation = 0F;
            lever.prevRotation = 0F;
        }
    }

    /** Interpolated crank angle in degrees. */
    public float getRotation(float partialTick) {
        return prevRotation + (rotation - prevRotation) * partialTick;
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == EVENT_TURN) {
            startTurning();
            return true;
        }
        return super.triggerEvent(id, param);
    }

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
    }
}
