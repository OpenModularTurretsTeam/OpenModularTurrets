package omtteam.openmodularturrets.gametest;

//? if neoforge && <1.21.11 {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if (neoforge && <1.21.11) || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

/**
 * The sort button of the turret base GUI ({@link TurretBaseInventory#sortAmmo}, which uses the core's
 * {@code AmmoSort}): the order it gives, and that it gives exactly what the base's own sort of 4.0.0 gave (kept below
 * as {@link #referenceSort}) for random slot contents. Needs no world, only the item registry.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class AmmoSortGameTests implements OMTGameTests {
    private static final int ROUNDS = 2000;

    /**
     * Equal items merge (up to the slot limit), Minecraft's items come before Open Modular Turrets', each by registry
     * name; arrows with other data stay apart and keep the order in which they first appear; empty slots end up last.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ammo_sort_order")
    public void sortMergesAndOrdersTheAmmo(GameTestHelper helper) {
        SlotCapacity[] capacity = {SlotCapacity.NONE};
        int[] changes = {0};
        TurretBaseInventory inventory = inventory(capacity, changes);
        ItemStack arrowB = withData(new ItemStack(Items.ARROW, 1), "b");
        ItemStack arrowA = withData(new ItemStack(Items.ARROW, 2), "a");
        fill(inventory, arrowB, new ItemStack(ModItems.AMMO_BULLET, 10), new ItemStack(Items.ARROW, 5),
                new ItemStack(ModItems.AMMO_ROCKET, 3), ItemStack.EMPTY, new ItemStack(Items.ARROW, 60),
                new ItemStack(Items.REDSTONE, 7), new ItemStack(ModItems.AMMO_BULLET, 60), arrowA);
        changes[0] = 0;

        inventory.sortAmmo();

        ItemStack[] expected = {arrowB.copy(), new ItemStack(Items.ARROW, 64), new ItemStack(Items.ARROW, 1), arrowA.copy(),
                new ItemStack(Items.REDSTONE, 7), new ItemStack(ModItems.AMMO_BULLET, 64),
                new ItemStack(ModItems.AMMO_BULLET, 6), new ItemStack(ModItems.AMMO_ROCKET, 3), ItemStack.EMPTY};
        assertSlots(helper, inventory, expected, "sorted");
        helper.assertTrue(changes[0] == 1, "The sort reported " + changes[0] + " changes instead of 1");
        helper.succeed();
    }

    /**
     * With inventory expanders a slot holds more than a stack; when the merged items need more slots than there are
     * (after an expander was removed), nothing changes.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ammo_sort_limit")
    public void sortFollowsTheSlotLimit(GameTestHelper helper) {
        SlotCapacity[] capacity = {new SlotCapacity(2)};
        int[] changes = {0};
        TurretBaseInventory inventory = inventory(capacity, changes);
        fill(inventory, new ItemStack(Items.ARROW, 100), new ItemStack(Items.ARROW, 150), new ItemStack(Items.ENDER_PEARL, 16),
                new ItemStack(Items.ENDER_PEARL, 40));
        inventory.sortAmmo();
        assertSlots(helper, inventory, new ItemStack[]{new ItemStack(Items.ARROW, 192), new ItemStack(Items.ARROW, 58),
                new ItemStack(Items.ENDER_PEARL, 48), new ItemStack(Items.ENDER_PEARL, 8)}, "sorted with two expander stacks");

        fill(inventory, new ItemStack(Items.ARROW, 128), new ItemStack(Items.REDSTONE, 1), new ItemStack(Items.POTATO, 1),
                new ItemStack(Items.ENDER_PEARL, 1), new ItemStack(ModItems.AMMO_BULLET, 1), new ItemStack(ModItems.AMMO_ROCKET, 1),
                new ItemStack(ModItems.AMMO_GRENADE, 1), new ItemStack(ModItems.AMMO_BOLT, 1), new ItemStack(ModItems.AMMO_FERRO_SLUG, 1));
        ItemStack[] before = slots(inventory);
        capacity[0] = SlotCapacity.NONE;
        changes[0] = 0;
        inventory.sortAmmo();
        assertSlots(helper, inventory, before, "too full to sort");
        helper.assertTrue(changes[0] == 0, "A sort that changed nothing reported a change");
        helper.succeed();
    }

    /**
     * For random slot contents (Minecraft's, this mod's and, where the test server has them, other mods' items, with
     * and without extra data, stack sizes 1, 16 and 64, 0 to 3 expander stacks) the sort gives exactly the slots and
     * change reports of the sort the base had before it used the core.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ammo_sort_same")
    public void sortGivesTheSameSlotsAsBefore(GameTestHelper helper) {
        List<ItemStack> pool = new ArrayList<>(List.of(new ItemStack(Items.ARROW), new ItemStack(Items.REDSTONE),
                new ItemStack(Items.POTATO), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.DIAMOND_SWORD),
                new ItemStack(ModItems.AMMO_BULLET), new ItemStack(ModItems.AMMO_ROCKET), new ItemStack(ModItems.AMMO_GRENADE),
                withData(new ItemStack(Items.ARROW), "a"), withData(new ItemStack(Items.ARROW), "b"),
                withData(new ItemStack(ModItems.AMMO_BULLET), "a")));
        List<Item> others = BuiltInRegistries.ITEM.stream()
                .filter(item -> item != Items.AIR)
                .filter(item -> !List.of("minecraft", OMT.MOD_ID).contains(BuiltInRegistries.ITEM.getKey(item).getNamespace()))
                .limit(3).toList();
        others.forEach(item -> pool.add(new ItemStack(item)));

        Random random = new Random(42);
        for (int round = 0; round < ROUNDS; round++) {
            SlotCapacity[] capacity = {new SlotCapacity(random.nextInt(4))};
            int[] changes = {0};
            TurretBaseInventory inventory = inventory(capacity, changes);
            for (int slot = 0; slot < TurretBaseInventory.AMMO_COUNT; slot++) {
                if (random.nextInt(10) < 3) continue;
                ItemStack item = pool.get(random.nextInt(pool.size()));
                int limit = inventory.getSlotLimit(slot, item);
                inventory.setItem(slot, item.copyWithCount(1 + random.nextInt(limit)));
            }
            ItemStack[] before = slots(inventory);
            NonNullList<ItemStack> reference = NonNullList.withSize(TurretBaseInventory.AMMO_COUNT, ItemStack.EMPTY);
            for (int slot = 0; slot < before.length; slot++) reference.set(slot, before[slot].copy());
            boolean referenceChanged = referenceSort(reference, stack -> inventory.getSlotLimit(0, stack));

            changes[0] = 0;
            inventory.sortAmmo();
            String context = "round " + round + " (" + capacity[0].bonusStacks() + " expander stacks, slots before "
                    + describe(before) + ")";
            assertSlots(helper, inventory, reference.toArray(new ItemStack[0]), context);
            helper.assertTrue(changes[0] == (referenceChanged ? 1 : 0),
                    "Changes reported: " + changes[0] + ", before: " + (referenceChanged ? 1 : 0) + " in " + context);
        }
        helper.succeed();
    }

    // ---- the sort of 4.0.0 (TurretBaseInventory.sortAmmo before the core's AmmoSort), the reference ----

    /** Sorts the slots in place; false if they stayed as they were because the stacks did not fit. */
    private static boolean referenceSort(NonNullList<ItemStack> items, ToIntFunction<ItemStack> slotLimit) {
        List<ReferenceKind> kinds = new ArrayList<>();
        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;
            ReferenceKind kind = kinds.stream().filter(k -> referenceSameItem(k.item, stack)).findFirst().orElse(null);
            if (kind == null) {
                kinds.add(new ReferenceKind(stack.copyWithCount(1), stack.getCount()));
            } else {
                kind.count += stack.getCount();
            }
        }
        kinds.sort(Comparator.comparingInt((ReferenceKind kind) -> referenceNamespaceRank(kind.item))
                .thenComparing(kind -> BuiltInRegistries.ITEM.getKey(kind.item.getItem()).toString()));
        List<ItemStack> sorted = new ArrayList<>();
        for (ReferenceKind kind : kinds) {
            int limit = slotLimit.applyAsInt(kind.item);
            for (long left = kind.count; left > 0; left -= limit) {
                sorted.add(kind.item.copyWithCount((int) Math.min(left, limit)));
            }
        }
        if (sorted.size() > items.size()) return false;
        for (int i = 0; i < items.size(); i++) {
            items.set(i, i < sorted.size() ? sorted.get(i) : ItemStack.EMPTY);
        }
        return true;
    }

    private static final class ReferenceKind {
        private final ItemStack item;
        private long count;

        private ReferenceKind(ItemStack item, long count) {
            this.item = item;
            this.count = count;
        }
    }

    private static boolean referenceSameItem(ItemStack a, ItemStack b) {
        //? if >=1.21 {
        return ItemStack.isSameItemSameComponents(a, b);
        //?} else {
        /*return ItemStack.isSameItemSameTags(a, b);
        *///?}
    }

    private static int referenceNamespaceRank(ItemStack stack) {
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (namespace.equals("minecraft")) return 0;
        return namespace.equals(OMT.MOD_ID) ? 1 : 2;
    }

    // ---- helpers ----

    private static TurretBaseInventory inventory(SlotCapacity[] capacity, int[] changes) {
        return new TurretBaseInventory(() -> changes[0]++, player -> true, () -> capacity[0]);
    }

    /** Puts the stacks into the ammo slots from the first one on and empties the others. */
    private static void fill(TurretBaseInventory inventory, ItemStack... stacks) {
        for (int slot = 0; slot < TurretBaseInventory.AMMO_COUNT; slot++) {
            inventory.setItem(TurretBaseInventory.AMMO_START + slot, slot < stacks.length ? stacks[slot] : ItemStack.EMPTY);
        }
    }

    private static ItemStack withData(ItemStack stack, String value) {
        CompoundTag tag = new CompoundTag();
        tag.putString("ammo_sort_test", value);
        VersionCompat.setCustomData(stack, tag);
        return stack;
    }

    private static ItemStack[] slots(TurretBaseInventory inventory) {
        ItemStack[] slots = new ItemStack[TurretBaseInventory.AMMO_COUNT];
        for (int slot = 0; slot < slots.length; slot++) {
            slots[slot] = inventory.getItem(TurretBaseInventory.AMMO_START + slot).copy();
        }
        return slots;
    }

    /** The ammo slots hold {@code expected} (missing entries: empty), item, count and data alike. */
    private static void assertSlots(GameTestHelper helper, TurretBaseInventory inventory, ItemStack[] expected, String context) {
        ItemStack[] actual = slots(inventory);
        for (int slot = 0; slot < actual.length; slot++) {
            ItemStack want = slot < expected.length ? expected[slot] : ItemStack.EMPTY;
            if (!ItemStack.matches(actual[slot], want)) {
                helper.fail("Slot " + slot + " holds " + describe(actual[slot]) + " instead of " + describe(want)
                        + " (" + context + "); slots: " + describe(actual));
            }
        }
    }

    private static String describe(ItemStack[] stacks) {
        List<String> parts = new ArrayList<>();
        for (ItemStack stack : stacks) parts.add(describe(stack));
        return String.join(", ", parts);
    }

    private static String describe(ItemStack stack) {
        if (stack.isEmpty()) return "empty";
        String data = VersionCompat.hasCustomData(stack) ? " " + VersionCompat.getCustomData(stack) : "";
        return stack.getCount() + " " + BuiltInRegistries.ITEM.getKey(stack.getItem()) + data;
    }
}
