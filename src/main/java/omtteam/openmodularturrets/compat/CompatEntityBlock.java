package omtteam.openmodularturrets.compat;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
//? if >=1.21.11 {
/*import net.minecraft.world.level.redstone.Orientation;
*///?}
//? if >=1.21 {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.LevelReader;
//?}

/**
 * Version-independent base class for all mod blocks with a block entity (the counterpart of {@link CompatBlockEntity}).
 * <p>
 * It hides what differs between 1.20.1 and 1.21 for blocks:
 * <ul>
 *     <li>the block codec (1.21 only): create one with {@link #codecOf} in a static field and pass it to the
 *     constructor</li>
 *     <li>pick block ({@code getCloneItemStack} takes a {@code LevelReader} on 1.21, a {@code BlockGetter} on
 *     1.20.1, and takes a flag on 1.21.11): override {@link #pickItem} instead</li>
 *     <li>neighbour updates ({@code neighborChanged} gets an {@code Orientation} instead of the neighbour's position on
 *     1.21.11): override {@link #onNeighborChanged}</li>
 *     <li>light absorption ({@code getLightBlock} and {@code propagatesSkylightDown} lost their level and position
 *     parameters on 1.21.11): override {@link #lightAbsorbed} / {@link #skylightPropagates}</li>
 * </ul>
 */
public abstract class CompatEntityBlock extends BaseEntityBlock {
    private final BlockCodec<?> blockCodec;

    protected CompatEntityBlock(BlockCodec<?> blockCodec, Properties properties) {
        super(properties);
        this.blockCodec = blockCodec;
    }

    /**
     * The codec of a block that is created from its properties only.
     *
     * @param factory the block's constructor
     */
    protected static <T extends BaseEntityBlock> BlockCodec<T> codecOf(Function<Properties, T> factory) {
        //? if >=1.21 {
        return new BlockCodec<>(simpleCodec(factory));
        //?} else {
        /*return new BlockCodec<>();
        *///?}
    }

    /**
     * The codec of a block that is created from one value and its properties (a tier, a turret type).
     *
     * @param name    the key of the value in the codec
     * @param field   how the value is stored
     * @param getter  reads the value from the block
     * @param factory the block's constructor
     */
    protected static <T extends BaseEntityBlock, A> BlockCodec<T> codecOf(String name, Codec<A> field, Function<T, A> getter,
                                                                          BiFunction<A, Properties, T> factory) {
        //? if >=1.21 {
        return new BlockCodec<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                field.fieldOf(name).forGetter(getter),
                propertiesCodec()
        ).apply(instance, factory)));
        //?} else {
        /*return new BlockCodec<>();
        *///?}
    }

    //? if >=1.21 {
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return blockCodec.codec();
    }
    //?}

    /**
     * What picking the block gives (middle click), e.g. a camouflage instead of the block itself.
     *
     * @param picked the item vanilla would give
     * @return the block's own item by default
     */
    protected ItemStack pickItem(BlockGetter level, BlockPos pos, ItemStack picked) {
        return picked;
    }

    /**
     * A neighbouring block changed (or the block was told to check).
     *
     * @param neighbourPos the position of the neighbour that changed, {@code null} on 1.21.11, which does not tell
     */
    protected void onNeighborChanged(BlockState state, Level level, BlockPos pos, @Nullable BlockPos neighbourPos) {
    }

    //? if >=1.21.11 {
    /*@Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock,
                                   @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, orientation, movedByPiston);
        onNeighborChanged(state, level, pos, null);
    }
    *///?} else {
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos,
                                boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighbourBlock, neighbourPos, movedByPiston);
        onNeighborChanged(state, level, pos, neighbourPos);
    }
    //?}

    /** How much light the block absorbs (0 to 15); {@code vanilla} is what the block would absorb by default. */
    protected int lightAbsorbed(BlockState state, int vanilla) {
        return vanilla;
    }

    /** Whether sky light passes through the block; {@code vanilla} is the default. */
    protected boolean skylightPropagates(BlockState state, boolean vanilla) {
        return vanilla;
    }

    //? if >=1.21.11 {
    /*@Override
    protected int getLightBlock(BlockState state) {
        return lightAbsorbed(state, super.getLightBlock(state));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return skylightPropagates(state, super.propagatesSkylightDown(state));
    }
    *///?} else {
    @Override
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return lightAbsorbed(state, super.getLightBlock(state, level, pos));
    }

    @Override
    @SuppressWarnings({"deprecation", "RedundantSuppression"})
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return skylightPropagates(state, super.propagatesSkylightDown(state, level, pos));
    }
    //?}

    //? if >=1.21.11 {
    /*@Override
    protected @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return pickItem(level, pos, super.getCloneItemStack(level, pos, state, includeData));
    }
    *///?} else if >=1.21 {
    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return pickItem(level, pos, super.getCloneItemStack(level, pos, state));
    }
    //?} else {
    /*@Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return pickItem(level, pos, super.getCloneItemStack(level, pos, state));
    }
    *///?}
}
