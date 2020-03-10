package omtteam.openmodularturrets.items;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.TurretBase;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

import static omtteam.omlib.util.GeneralUtil.*;

@MethodsReturnNonnullByDefault
public class UsableMetaItem extends Item {
    public final static String[] subNames = {
            OMTNames.Items.bulletThrowableItem, OMTNames.Items.grenadeThrowableItem, OMTNames.Items.memoryCard
    };

    public UsableMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.usableMetaItem);
        this.setUnlocalizedName(OMTNames.Items.usableMetaItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return stack.getItemDamage() == 2 && world.getTileEntity(pos) instanceof TurretBase || super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItemDamage() == 2 && player.isSneaking() && stack.hasTagCompound()) {
            //noinspection ConstantConditions
            Set<String> keySet = stack.getTagCompound().getKeySet();
            keySet.clear();
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (!itemStackIn.isEmpty() && itemStackIn.getItemDamage() == 2 && playerIn.isSneaking() && itemStackIn.hasTagCompound()) {
            //noinspection ConstantConditions
            Set<String> keySet = itemStackIn.getTagCompound().getKeySet();
            keySet.clear();
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 3; i++) {
                subItems.add(new ItemStack(ModItems.usableMetaItem, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUnlocalizedName(ItemStack itemStack) {
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
                if (hasDataStored(stack)) {
                    NBTTagCompound nbtTagCompound = getDataStored(stack);
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc1"));
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc2"));
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc3"));
                    tooltip.add("\u00A76: \u00A7b" + nbtTagCompound.getInteger("currentMaxRange"));
                    tooltip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.MODE) + ": " + getMachineModeLocalization(EnumMachineMode.values()[nbtTagCompound.getInteger("mode")]));
                    tooltip.add("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.MULTI_TARGETING) + ": " + getColoredBooleanLocalizationYesNo(nbtTagCompound.getBoolean("multiTargeting")));
                    tooltip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS) + ": " + getColoredBooleanLocalizationYesNo(nbtTagCompound.getBoolean("attacksMobs")));
                    tooltip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS) + ": " + getColoredBooleanLocalizationYesNo(nbtTagCompound.getBoolean("attacksNeutrals")));
                    tooltip.add("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS) + ": " + getColoredBooleanLocalizationYesNo(nbtTagCompound.getBoolean("attacksPlayers")));
                } else // If the stack does not have valid tag data, a default message
                {
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc1"));
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc2"));
                    tooltip.add(safeLocalize("tooltip.openmodularturrets.memory_card.desc3"));
                }
            }
        }
    }
}
