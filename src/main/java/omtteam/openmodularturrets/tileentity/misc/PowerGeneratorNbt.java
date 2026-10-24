package omtteam.openmodularturrets.tileentity.misc;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The inventory of a 3.x power generator, in any of the formats of OMLib's 3.x {@code TileEntityContainer}: the item
 * handler list under {@code Slots} (what 3.x wrote, next to an empty {@code Inventory} compound), or a slot list under
 * {@code Items} or {@code Inventory.Items} (older layouts).
 */
final class PowerGeneratorNbt {
    private PowerGeneratorNbt() {
    }

    /** The item tags of the slots below {@code slots}, by slot index. */
    static SortedMap<Integer, NBTTagCompound> readSlots(NBTTagCompound tag, int slots) {
        NBTTagList list;
        if (tag.hasKey("Slots", Constants.NBT.TAG_LIST)) {
            list = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        } else if (tag.hasKey("Items", Constants.NBT.TAG_LIST)) {
            list = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        } else {
            list = tag.getCompoundTag("Inventory").getTagList("Items", Constants.NBT.TAG_COMPOUND);
        }
        SortedMap<Integer, NBTTagCompound> items = new TreeMap<>();
        for (NBTBase entry : list) {
            NBTTagCompound item = (NBTTagCompound) entry;
            int slot = item.getByte("Slot") & 255;
            if (slot < slots) {
                items.put(slot, item);
            }
        }
        return items;
    }
}
