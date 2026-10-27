package omtteam.openmodularturrets.block.defense;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import omtteam.omlib.block.InteractiveBlock;
import omtteam.omlib.block.UnownedMarker;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.defense.PassiveDefenseBlockEntity;
import omtteam.omlib.compat.VersionCompat;
//? if >=1.21.11 {
/*import net.minecraft.world.entity.InsideBlockEffectApplier;
*///?}
import org.jetbrains.annotations.Nullable;

/**
 * Razor fence (OMPD {@code BlockFence}, a pane), one block per tier. Hurts everything touching it except its owner and
 * item entities, like a cactus, for the damage of its tier.
 */
public class DefenseFenceBlock extends IronBarsBlock implements EntityBlock, InteractiveBlock {
    private final DefenseTier tier;

    public DefenseFenceBlock(DefenseTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public DefenseTier getTier() {
        return tier;
    }

    /** Mines with the configured hardness ({@link PassiveDefense#destroyProgress}). */
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return PassiveDefense.destroyProgress(super.getDestroyProgress(state, player, level, pos), tier);
    }

    /** The configured blast resistance ({@link PassiveDefense#setting}). */
    @Override
    public float getExplosionResistance() {
        return (float) PassiveDefense.setting(tier).explosionResistance;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PassiveDefenseBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            OwnedBlockEntity.setOwnerOnPlace(level.getBlockEntity(pos) instanceof OwnedBlockEntity be ? be : null, placer);
        }
    }

    //? if >=1.21.11 {
    /*@Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        onEntityInside(level, pos, entity);
    }
    *///?} else {
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        onEntityInside(level, pos, entity);
    }
    //?}

    /** OMPD {@code onEntityCollidedWithBlock}: {@code DamageSource.CACTUS}, the configured damage of the tier. */
    private void onEntityInside(Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide() || entity instanceof ItemEntity) return;
        boolean owner = entity instanceof Player player && level.getBlockEntity(pos) instanceof OwnedBlockEntity be
                && be.isOwner(player);
        if (!owner) {
            VersionCompat.hurt(entity, level.damageSources().cactus(), (float) PassiveDefense.setting(tier).fenceDamage);
        }
    }

    @Override
    public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return PassiveDefense.tryPickUp(level, pos, player, hand);
    }

    /** Sparkles while the block has no owner ({@link UnownedMarker}). */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        UnownedMarker.animateTick(level, pos, random);
    }
}
