package omtteam.openmodularturrets.block.expander;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Item of an {@link BlockInventoryExpander}: like a slab, it stacks onto a single expander of its tier when the player
 * clicks that expander's open side, i.e. the plate's open face or the face of the block it lies against around the
 * plate (the clicked face pointing away from {@link BlockInventoryExpander#FACING} either way); otherwise it places a
 * new plate (1.20.1+ does this in {@code InventoryExpanderBlock.canBeReplaced}).
 */
public class ItemBlockInventoryExpander extends ItemBlock {
    public ItemBlockInventoryExpander(Block block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos target = mergeTarget(world, pos, facing);
        if (stack.isEmpty() || target == null) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        if (!player.canPlayerEdit(target, facing, stack)) {
            return EnumActionResult.FAIL;
        }
        IBlockState merged = world.getBlockState(target).withProperty(BlockInventoryExpander.DOUBLE, true);
        AxisAlignedBB box = merged.getBoundingBox(world, target).offset(target);
        if (world.checkNoEntityCollision(box) && world.setBlockState(target, merged, 11)) {
            SoundType sound = block.getSoundType(merged, world, target, player);
            world.playSound(player, target, sound.getPlaceSound(), SoundCategory.BLOCKS,
                    (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            stack.shrink(1);
            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, target, stack);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    /** The client asks this before it sends the click; stacking onto a plate is allowed there too (like a slab). */
    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player,
                                       ItemStack stack) {
        return mergeTarget(world, pos, side) != null || super.canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    /** The single expander of this tier a click on {@code side} of {@code pos} stacks onto, or null. */
    @Nullable
    private BlockPos mergeTarget(World world, BlockPos pos, EnumFacing side) {
        if (canMerge(world.getBlockState(pos), side)) return pos;
        BlockPos next = pos.offset(side);
        return canMerge(world.getBlockState(next), side) ? next : null;
    }

    private boolean canMerge(IBlockState state, EnumFacing side) {
        return state.getBlock() == block && !state.getValue(BlockInventoryExpander.DOUBLE)
                && side == state.getValue(BlockInventoryExpander.FACING).getOpposite();
    }
}
