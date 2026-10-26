package omtteam.openmodularturrets.camo;

import omtteam.omlib.camo.CamoBlock;
import omtteam.omlib.camo.CamoBlock;
import omtteam.omlib.camo.CamoHost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.omlib.camo.DisguiseRules;
import org.jetbrains.annotations.Nullable;

/**
 * Which blocks may be used as a disguise, and the item form of one. Any full block works, opaque or transparent
 * (stone, glass, leaves, ice, ...).
 */
public final class CamoRules {
    /** These rules as the camouflage menu of OMLib asks the hosts for them (with this mod's texts). */
    public static final DisguiseRules RULES = new DisguiseRules() {
        @Override
        public boolean isValidDisguise(BlockState state) {
            return CamoRules.isValidDisguise(state);
        }

        @Override
        public Component invalidMessage() {
            return Component.translatable("message.openmodularturrets.camo_invalid");
        }

        @Override
        public Component slotHint() {
            return Component.translatable("tooltip.openmodularturrets.camouflage_slot");
        }
    };

    private CamoRules() {
    }

    /**
     * A full block that is drawn from a normal model, has no block entity of its own (unless it is a
     * {@link PlainLookingBlock}) and is not itself camouflaged (OMLib: a full, simple block).
     */
    public static boolean isValidDisguise(BlockState state) {
        if (state.isAir()) return false;
        Block block = state.getBlock();
        if (block instanceof CamoBlock) return false;
        if (block instanceof EntityBlock && !(block instanceof PlainLookingBlock)) return false;
        if (state.getRenderShape() != RenderShape.MODEL) return false;
        return state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    /** The disguise a held stack stands for (the item's block in its default state), if it is a valid one. */
    @Nullable
    public static BlockState disguiseFor(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem item)) return null;
        BlockState state = item.getBlock().defaultBlockState();
        return isValidDisguise(state) ? state : null;
    }

    /** Registry id of a disguise, as stored on memory cards. */
    public static String idOf(BlockState camo) {
        return BuiltInRegistries.BLOCK.getKey(camo.getBlock()).toString();
    }

    /** The disguise for a stored registry id, if the block still exists and is a valid disguise. */
    @Nullable
    public static BlockState disguiseById(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) return null;
        BlockState state = BuiltInRegistries.BLOCK.getOptional(location).map(Block::defaultBlockState).orElse(null);
        return state != null && isValidDisguise(state) ? state : null;
    }

    /** The item shown in the disguise slot of the menu. */
    public static ItemStack itemFor(@Nullable BlockState camo) {
        return camo == null ? ItemStack.EMPTY : new ItemStack(camo.getBlock());
    }

    /** Picking a camouflaged block gives the disguise, so it is not revealed (OMPD {@code getPickBlock}). */
    public static ItemStack pickItem(BlockGetter level, BlockPos pos, ItemStack fallback) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof CamoHost host && host.isCamoEnabled() && host.getCamo() != null) {
            return new ItemStack(host.getCamo().getBlock());
        }
        return fallback;
    }
}
