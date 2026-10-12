package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Hardened wall (OMPD {@code BlockWall}, an OMLib cobblestone-wall clone), one block per tier, with the hardened
 * block texture.
 */
public class DefenseWallBlock extends WallBlock implements EntityBlock, InteractiveBlock {
    private final DefenseTier tier;

    public DefenseWallBlock(DefenseTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public DefenseTier getTier() {
        return tier;
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
