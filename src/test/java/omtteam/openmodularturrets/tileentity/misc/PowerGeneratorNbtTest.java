package omtteam.openmodularturrets.tileentity.misc;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerGeneratorNbtTest {
    private static NBTTagCompound item(String id, int count) {
        NBTTagCompound item = new NBTTagCompound();
        item.setString("id", id);
        item.setByte("Count", (byte) count);
        item.setShort("Damage", (short) 0);
        return item;
    }

    /** An entry of Forge's item handler list (what 3.x saved): the slot is an int. */
    private static NBTTagCompound handlerEntry(int slot, String id, int count) {
        NBTTagCompound item = item(id, count);
        item.setInteger("Slot", slot);
        return item;
    }

    /** An entry of the older slot lists: the slot is a byte. */
    private static NBTTagCompound listEntry(int slot, String id, int count) {
        NBTTagCompound item = item(id, count);
        item.setByte("Slot", (byte) slot);
        return item;
    }

    private static NBTTagList list(NBTTagCompound... entries) {
        NBTTagList list = new NBTTagList();
        for (NBTTagCompound entry : entries) {
            list.appendTag(entry);
        }
        return list;
    }

    @Test
    void readsTheItemHandlerListOf3x() {
        // 3.x TileEntityContainer.saveToNBT: the slots under "Slots" and an empty "Inventory" compound
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Slots", list(handlerEntry(0, "minecraft:coal", 12), handlerEntry(5, "minecraft:lava_bucket", 1),
                handlerEntry(6, "minecraft:redstone", 30)));
        tag.setTag("Inventory", new NBTTagCompound());

        SortedMap<Integer, NBTTagCompound> slots = PowerGeneratorNbt.readSlots(tag, GeneratorTileEntity.FUEL_SLOTS);

        assertEquals(3, slots.size());
        assertEquals("minecraft:coal", slots.get(0).getString("id"));
        assertEquals(12, slots.get(0).getByte("Count"));
        assertEquals("minecraft:lava_bucket", slots.get(5).getString("id"));
        assertEquals("minecraft:redstone", slots.get(6).getString("id"), "the redstone slot lands in fuel slot 7");
    }

    @Test
    void readsTheOlderItemLists() {
        NBTTagCompound items = new NBTTagCompound();
        items.setTag("Items", list(listEntry(2, "minecraft:coal", 1)));
        assertEquals("minecraft:coal", PowerGeneratorNbt.readSlots(items, 9).get(2).getString("id"));

        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag("Items", list(listEntry(4, "minecraft:blaze_rod", 3)));
        NBTTagCompound nested = new NBTTagCompound();
        nested.setTag("Inventory", inventory);
        assertEquals("minecraft:blaze_rod", PowerGeneratorNbt.readSlots(nested, 9).get(4).getString("id"));
    }

    @Test
    void theItemHandlerListWinsOverTheOthers() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Slots", list(handlerEntry(0, "minecraft:coal", 1)));
        tag.setTag("Items", list(listEntry(0, "minecraft:stick", 1)));
        assertEquals("minecraft:coal", PowerGeneratorNbt.readSlots(tag, 9).get(0).getString("id"));
    }

    @Test
    void slotsOutsideTheInventoryAreDropped() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Items", list(listEntry(8, "minecraft:coal", 1), listEntry(9, "minecraft:coal", 1),
                listEntry(-1, "minecraft:coal", 1)));
        SortedMap<Integer, NBTTagCompound> slots = PowerGeneratorNbt.readSlots(tag, 9);
        assertEquals(1, slots.size());
        assertTrue(slots.containsKey(8));
    }

    @Test
    void noInventoryReadsAsEmpty() {
        assertTrue(PowerGeneratorNbt.readSlots(new NBTTagCompound(), 9).isEmpty());
    }
}
