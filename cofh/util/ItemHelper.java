package cofh.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Contains various helper functions to assist with {@link Item} and {@link ItemStack} manipulation and interaction.
 * 
 * @author King Lemming
 * 
 */
public final class ItemHelper {

	public static final String BLOCK = "block";
	public static final String ORE = "ore";
	public static final String DUST = "dust";
	public static final String INGOT = "ingot";
	public static final String NUGGET = "nugget";
	public static final String LOG = "log";

	private ItemHelper() {

	}

	public static ItemStack cloneStack(Item item, int stackSize) {

		if (item == null) {
			return null;
		}
		ItemStack stack = new ItemStack(item, stackSize);

		return stack;
	}

	public static ItemStack cloneStack(ItemStack stack, int stackSize) {

		if (stack == null) {
			return null;
		}
		ItemStack retStack = stack.copy();
		retStack.stackSize = stackSize;

		return retStack;
	}

	public static ItemStack copyTag(ItemStack container, ItemStack other) {

		if (other != null && other.stackTagCompound != null) {
			container.stackTagCompound = (NBTTagCompound) other.stackTagCompound.copy();
		}
		return container;
	}

	public static NBTTagCompound setItemStackTagName(NBTTagCompound tag, String name) {

		if (name == "") {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound("tag");
		}
		if (!tag.hasKey("display")) {
			tag.setCompoundTag("display", new NBTTagCompound());
		}
		tag.getCompoundTag("display").setString("Name", name);

		return tag;
	}

	public static String getNameFromItemStack(ItemStack stack) {

		if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("display")) {
			return "";
		}
		return stack.stackTagCompound.getCompoundTag("display").getString("Name");
	}

	public static ItemStack consumeItem(ItemStack stack) {

		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem()) {
				return stack.getItem().getContainerItemStack(stack);
			} else {
				return null;
			}
		}
		stack.splitStack(1);
		return stack;
	}

	/**
	 * Gets a vanilla CraftingManager result.
	 */
	public static ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {

		ItemStack[] dmgItems = new ItemStack[2];
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null) {
				if (dmgItems[0] == null) {
					dmgItems[0] = inv.getStackInSlot(i);
				} else {
					dmgItems[1] = inv.getStackInSlot(i);
					break;
				}
			}
		}

		if (dmgItems[0] == null || Item.itemsList[dmgItems[0].itemID] == null) {
			return null;
		} else if (dmgItems[1] != null && dmgItems[0].itemID == dmgItems[1].itemID && dmgItems[0].stackSize == 1 && dmgItems[1].stackSize == 1
				&& Item.itemsList[dmgItems[0].itemID].isRepairable()) {
			Item theItem = Item.itemsList[dmgItems[0].itemID];
			int var13 = theItem.getMaxDamage() - dmgItems[0].getItemDamageForDisplay();
			int var8 = theItem.getMaxDamage() - dmgItems[1].getItemDamageForDisplay();
			int var9 = var13 + var8 + theItem.getMaxDamage() * 5 / 100;
			int var10 = Math.max(0, theItem.getMaxDamage() - var9);
			return new ItemStack(dmgItems[0].itemID, 1, var10);
		} else {
			IRecipe recipe;
			for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); ++i) {
				recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

				if (recipe.matches(inv, world)) {
					return recipe.getCraftingResult(inv);
				}
			}
			return null;
		}
	}

	/**
	 * Get a hashcode based on the ItemStack's ID and Metadata. As both of these are shorts, this should be collision-free for non-NBT sensitive ItemStacks.
	 * 
	 * @param stack
	 *            The ItemStack to get a hashcode for.
	 * @return The hashcode.
	 */
	public static int getHashCode(ItemStack stack) {

		return stack.getItemDamage() | stack.itemID << 16;
	}

	/**
	 * Get a hashcode based on an ID and Metadata pair. As both of these are shorts, this should be collision-free if NBT is not involved.
	 * 
	 * @param id
	 *            ID value to use.
	 * @param metadata
	 *            Metadata value to use.
	 * @return The hashcode.
	 */
	public static int getHashCode(int id, int metadata) {

		return metadata | id << 16;
	}

	/**
	 * Extract the ID from a hashcode created from one of the getHashCode() methods in this class.
	 */
	public static int getIDFromHashCode(int hashCode) {

		return hashCode >> 16;
	}

	/**
	 * Extract the Metadata from a hashcode created from one of the getHashCode() methods in this class.
	 */
	public static int getMetaFromHashCode(int hashCode) {

		return hashCode & 0xFF;
	}

	/* ORE DICT FUNCTIONS */
	public static boolean hasOreName(ItemStack stack) {

		return !getOreName(stack).equals("Unknown");
	}

	public static String getOreName(ItemStack stack) {

		return OreDictionary.getOreName(OreDictionary.getOreID(stack));
	}

	public static boolean isOreID(ItemStack stack, int oreID) {

		return OreDictionary.getOreID(stack) == oreID;
	}

	public static boolean isOreName(ItemStack stack, String oreName) {

		return OreDictionary.getOreName(OreDictionary.getOreID(stack)).equals(oreName);
	}

	public static boolean oreNameExists(String oreName) {

		return !OreDictionary.getOres(oreName).isEmpty();
	}

	public static ItemStack getOre(String oreName) {

		if (!oreNameExists(oreName)) {
			return null;
		}
		return cloneStack(OreDictionary.getOres(oreName).get(0), 1);
	}

	public static boolean isBlock(ItemStack stack) {

		return getOreName(stack).startsWith(BLOCK);
	}

	public static boolean isOre(ItemStack stack) {

		return getOreName(stack).startsWith(ORE);
	}

	public static boolean isDust(ItemStack stack) {

		return getOreName(stack).startsWith(DUST);
	}

	public static boolean isIngot(ItemStack stack) {

		return getOreName(stack).startsWith(INGOT);
	}

	public static boolean isNugget(ItemStack stack) {

		return getOreName(stack).startsWith(NUGGET);
	}

	public static boolean isLog(ItemStack stack) {

		return getOreName(stack).startsWith(LOG);
	}

	/**
	 * Determine if a player is holding a registered Fluid Container.
	 */
	public static final boolean isPlayerHoldingFluidContainer(EntityPlayer player) {

		return FluidContainerRegistry.isContainer(player.getCurrentEquippedItem());
	}

	public static final boolean isPlayerHoldingFluidContainerItem(EntityPlayer player) {

		return FluidHelper.isPlayerHoldingFluidContainerItem(player);
	}

	public static final boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		return EnergyHelper.isPlayerHoldingEnergyContainerItem(player);
	}

	/**
	 * Determine if a player is holding an ItemStack of a specific Item type.
	 */
	public static final boolean isPlayerHoldingItem(Item item, EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return item == null ? equipped == null : item.equals(equipped);
	}

	/**
	 * Determine if a player is holding an ItemStack with a specific Item ID, Metadata, and NBT.
	 */
	public static final boolean isPlayerHoldingItemStack(ItemStack stack, EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem() : null;
		return stack == null ? equipped == null : equipped != null && stack.isItemEqual(equipped) && ItemStack.areItemStackTagsEqual(stack, equipped);
	}

	public static final boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {

		return ItemStack.areItemStacksEqual(stackA, stackB);
	}

	public static final boolean areItemStackEqualNoNull(ItemStack stackA, ItemStack stackB) {

		return stackA.isItemEqual(stackB)
				&& (stackA.stackTagCompound != null ? stackB.stackTagCompound != null && stackA.stackTagCompound.equals(stackB.stackTagCompound)
						: stackB.stackTagCompound != null ? false : true);
	}

	public static boolean areItemStacksEqualNoNBT(ItemStack stackA, ItemStack stackB) {

		if (stackB == null) {
			return false;
		}
		return stackA.itemID == stackB.itemID
				&& (stackA.getItemDamage() == OreDictionary.WILDCARD_VALUE ? true : stackB.getItemDamage() == OreDictionary.WILDCARD_VALUE ? true : stackA
						.getHasSubtypes() == false ? true : stackB.getItemDamage() == stackA.getItemDamage());
	}

	public static boolean craftingEquivalent(ItemStack checked, ItemStack source, String oreDict, ItemStack output) {

		return output == null ? areItemStacksEqualNoNBT(checked, source) ? true : oreDict == null ? false : oreDict.equals("Unknown") ? false : getOreName(
				checked).equalsIgnoreCase(oreDict) : isBlacklist(output) ? areItemStacksEqualNoNBT(checked, source)
				: areItemStacksEqualNoNBT(checked, source) ? true : oreDict == null ? false : oreDict.equals("Unknown") ? false : getOreName(checked)
						.equalsIgnoreCase(oreDict);
	}

	public static boolean isBlacklist(ItemStack output) {

		return output.itemID == Block.stairsWoodBirch.blockID || output.itemID == Block.stairsWoodJungle.blockID
				|| output.itemID == Block.stairsWoodOak.blockID || output.itemID == Block.stairsWoodSpruce.blockID || output.itemID == Block.planks.blockID
				|| output.itemID == Block.woodSingleSlab.blockID;
	}

	public static String getItemNBTString(ItemStack theItem, String nbtKey, String invalidReturn) {

		return theItem.stackTagCompound != null ? theItem.stackTagCompound.hasKey(nbtKey) ? theItem.stackTagCompound.getString(nbtKey) : invalidReturn
				: invalidReturn;
	}

	public static Item getItemFromStack(ItemStack theStack) {

		return theStack == null ? null : theStack.getItem();
	}

	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB) {

		return stackA == null ? stackB == null ? true : false : stackB != null && stackA.itemID == stackB.itemID
				&& (stackA.getHasSubtypes() == false || stackA.getItemDamage() == stackB.getItemDamage());
	}

	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB) {

		return stackA == null ? stackB == null ? true : false : stackB != null && stackA.itemID == stackB.itemID;
	}

	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		return stackA == null ? stackB == null ? true : false : stackB != null && stackA.itemID == stackB.itemID
				&& stackA.getItemDamage() == stackB.getItemDamage() && (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		return stackA == null ? stackB == null ? true : false : stackB != null && stackA.itemID == stackB.itemID
				&& (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	public static boolean doOreIDsMatch(ItemStack stackA, ItemStack stackB) {

		return OreDictionary.getOreID(stackA) >= 0 && OreDictionary.getOreID(stackA) == OreDictionary.getOreID(stackB);
	}

	public static boolean doNBTsMatch(NBTTagCompound nbtA, NBTTagCompound nbtB) {

		return nbtA == null ? nbtB == null ? true : false : nbtB == null ? false : nbtA.equals(nbtB);
	}

	/**
	 * Adds Inventory information to ItemStacks which themselves hold things. Called in addInformation().
	 */
	public static void addInventoryInformation(ItemStack stack, List list) {

		addInventoryInformation(stack, list, 0, Integer.MAX_VALUE);
	}

	public static void addInventoryInformation(ItemStack stack, List list, int minSlot, int maxSlot) {

		if (stack.stackTagCompound.hasKey("Inventory") && stack.stackTagCompound.getTagList("Inventory").tagCount() > 0) {

			if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
				list.add(StringHelper.shiftForInfo);
			}
			if (!StringHelper.isShiftKeyDown()) {
				return;
			}
			list.add(StringHelper.localize("info.cofh.contents") + ":");
			NBTTagList nbtList = stack.stackTagCompound.getTagList("Inventory");
			ItemStack curStack;
			ItemStack curStack2;

			ArrayList<ItemStack> containedItems = new ArrayList<ItemStack>();

			boolean[] visited = new boolean[nbtList.tagCount()];

			for (int i = 0; i < nbtList.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) nbtList.tagAt(i);
				int slot = tag.getInteger("Slot");

				if (visited[i] || slot < minSlot || slot > maxSlot) {
					continue;
				}
				visited[i] = true;
				curStack = ItemStack.loadItemStackFromNBT(tag);

				if (curStack == null) {
					continue;
				}
				containedItems.add(curStack);
				for (int j = 0; j < nbtList.tagCount(); j++) {
					NBTTagCompound tag2 = (NBTTagCompound) nbtList.tagAt(j);
					int slot2 = tag.getInteger("Slot");

					if (visited[j] || slot < minSlot || slot > maxSlot) {
						continue;
					}
					curStack2 = ItemStack.loadItemStackFromNBT(tag2);

					if (curStack2 == null) {
						continue;
					}
					if (itemsEqualWithMetadata(curStack, curStack2)) {
						curStack.stackSize += curStack2.stackSize;
						visited[j] = true;
					}
				}
			}
			for (ItemStack item : containedItems) {
				int maxStackSize = item.getMaxStackSize();

				if (!StringHelper.displayStackCount || item.stackSize < maxStackSize || maxStackSize == 1) {
					list.add("    " + StringHelper.BRIGHT_GREEN + item.stackSize + " " + StringHelper.GRAY + item.getDisplayName());
				} else {
					if (item.stackSize % maxStackSize != 0) {
						list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + "+" + item.stackSize % maxStackSize
								+ " " + StringHelper.GRAY + item.getDisplayName());
					} else {
						list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + " " + StringHelper.GRAY
								+ item.getDisplayName());
					}
				}
			}
		}
	}

}
