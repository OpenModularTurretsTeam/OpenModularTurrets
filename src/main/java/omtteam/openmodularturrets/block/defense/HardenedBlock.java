package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import omtteam.openmodularturrets.compat.BlockCodec;
import omtteam.openmodularturrets.compat.CompatEntityBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hardened block (OMPD {@code BlockHardened}), one block per tier.
 */
public class HardenedBlock extends CompatEntityBlock implements InteractiveBlock {
    private static final BlockCodec<HardenedBlock> CODEC =
            codecOf(properties -> new HardenedBlock(DefenseTier.ONE, properties));

    private final DefenseTier tier;

    public HardenedBlock(DefenseTier tier, Properties properties) {
        super(CODEC, properties);
        this.tier = tier;
    }

    public DefenseTier getTier() {
        return tier;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OwnedBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            OwnedBlockEntity.setOwnerOnPlace(level.getBlockEntity(pos) instanceof OwnedBlockEntity be ? be : null, placer);
        }
    }

    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return PassiveDefense.tryPickUp(level, pos, player, hand);
    }
}
