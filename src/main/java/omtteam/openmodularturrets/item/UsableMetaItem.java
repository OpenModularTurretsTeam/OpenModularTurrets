package omtteam.openmodularturrets.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;


@MethodsReturnNonnullByDefault
public class UsableMetaItem extends Item {
    public final static String[] subNames = {
            OMTNames.Items.bulletThrowableItem, OMTNames.Items.grenadeThrowableItem, OMTNames.Items.memoryCard
    };

    public UsableMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.usableMetaItem);
        this.setTranslationKey(OMTNames.Items.usableMetaItem);
    }

    /**
     * Memory card on a turret base or camouflage trap: sneaking copies its settings, a click with a filled card writes
     * them (before the block's own right-click, see {@link MemoryCardInteraction}).
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
                                           float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity target = world.getTileEntity(pos);
        if (stack.getItemDamage() != 2 || !MemoryCardInteraction.isTarget(target)
                || !player.isSneaking() && !hasDataStored(stack)) {
            return EnumActionResult.PASS;
        }
        if (player instanceof EntityPlayerMP) {
            MemoryCardInteraction.use((EntityPlayerMP) player, this, stack, target, player.isSneaking());
        }
        return EnumActionResult.SUCCESS;
    }

    /** Sneak-click on any other block clears the card. */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == 2 && player.isSneaking() && hasDataStored(stack)) {
            clearCard(stack, player, world);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    /** Sneak-click in the air clears the card. */
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.getItemDamage() == 2 && playerIn.isSneaking() && hasDataStored(stack)) {
            clearCard(stack, playerIn, worldIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @SuppressWarnings("ConstantConditions")
    private static void clearCard(ItemStack stack, EntityPlayer player, World world) {
        if (!world.isRemote) {
            stack.getTagCompound().removeTag("data");
            player.sendStatusMessage(new TextComponentTranslation("message.openmodularturrets.card_cleared"), true);
        }
    }

    /**
     * Only the memory card: the throwable bullet and grenade (meta 0 and 1) have no function and no recipe; they stay
     * registered for 3.x worlds.
     */
    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            subItems.add(new ItemStack(this, 1, 2));
        }
    }

    /** Memory cards do not stack (1.20.1+). */
    @Override
    @ParametersAreNonnullByDefault
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItemDamage() == 2 ? 1 : super.getItemStackLimit(stack);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getTranslationKey(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean hasDataStored(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("data");
    }

    @SuppressWarnings("ConstantConditions")
    public NBTTagCompound getDataStored(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getCompoundTag("data") : null;
    }

    /** The card's settings, null if it is empty or holds no valid data. */
    @Nullable
    public MemoryCardData getCardData(ItemStack stack) {
        return hasDataStored(stack) ? MemoryCardNbt.read(getDataStored(stack)) : null;
    }

    public void setCardData(ItemStack stack, MemoryCardData data) {
        setDataStored(stack, MemoryCardNbt.write(data));
    }

    @SuppressWarnings("ConstantConditions")
    public void setDataStored(ItemStack stack, NBTTagCompound nbtTagCompound) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setTag("data", nbtTagCompound);
        } else {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setTag("data", nbtTagCompound);
            stack.setTagCompound(tagCompound);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            if (stack.getItemDamage() == 2) {
                MemoryCardTooltip.append(getCardData(stack), tooltip);
            }
        }
    }
}
