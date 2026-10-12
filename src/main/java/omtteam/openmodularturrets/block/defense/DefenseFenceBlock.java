package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.blockentity.defense.OwnedBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Razor fence (OMPD {@code BlockFence}, a pane), one block per tier. Hurts everything touching it except its owner and
 * item entities, like a cactus.
 */
public class DefenseFenceBlock extends IronBarsBlock implements EntityBlock, InteractiveBlock {
    /** OMPD: {@code DamageSource.CACTUS}, 5 damage. */
    private static final float DAMAGE = 5.0F;

    private final DefenseTier tier;

    public DefenseFenceBlock(DefenseTier tier, Properties properties) {
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

    /** OMPD {@code onEntityCollidedWithBlock}. */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide() || entity instanceof ItemEntity) return;
        boolean owner = entity instanceof Player player && level.getBlockEntity(pos) instanceof OwnedBlockEntity be
                && be.isOwner(player);
        if (!owner) {
            entity.hurt(level.damageSources().cactus(), DAMAGE);
        }
    }

    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return PassiveDefense.tryPickUp(level, pos, player, hand);
    }
}
