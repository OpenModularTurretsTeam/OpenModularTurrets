package omtteam.openmodularturrets.turret;

import omtteam.omlib.core.energy.EnergyAccess;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The parts of the energy drain turret's item choice (issue 324) that need no running game: which inventory slots it
 * drains, in which order, and when a player counts as carrying energy. Reading an item's energy needs Forge's item
 * capabilities and so a game (checked in game).
 */
class DrainableItemsTest {
    /** Armour (36 feet ... 39 head) first, then the selected hotbar slot, then the offhand (40). */
    @Test
    void drainsArmourThenTheMainHandThenTheOffhand() {
        assertArrayEquals(new int[]{36, 37, 38, 39, 0, 40}, DrainableItems.slots(0));
        assertArrayEquals(new int[]{36, 37, 38, 39, 8, 40}, DrainableItems.slots(8));
    }

    /** The rest of the inventory is never drained: only the selected one of the 36 main slots is in the list. */
    @Test
    void leavesTheRestOfTheInventoryAlone() {
        for (int selected = 0; selected < 9; selected++) {
            List<Integer> main = new ArrayList<>();
            for (int slot : DrainableItems.slots(selected)) {
                if (slot < 36) main.add(slot);
            }
            assertEquals(Collections.singletonList(selected), main, "selected slot " + selected);
        }
    }

    @Test
    void aPlayerCarriesEnergyOnlyIfAnItemGivesSome() {
        assertFalse(DrainableItems.anyExtractable(Collections.<EnergyAccess>emptyList()));
        assertFalse(DrainableItems.anyExtractable(Arrays.<EnergyAccess>asList(new Battery(0), new Battery(0))));
        assertTrue(DrainableItems.anyExtractable(Arrays.<EnergyAccess>asList(new Battery(0), new Battery(5))));
    }

    /** The check only simulates: it takes no energy. */
    @Test
    void theCheckDrainsNothing() {
        Battery battery = new Battery(5);
        assertTrue(DrainableItems.anyExtractable(Collections.<EnergyAccess>singletonList(battery)));
        assertEquals(5, battery.getAmount());
    }

    /** An item with energy that cannot be extracted (an output-less item) does not count. */
    @Test
    void energyThatCannotBeExtractedDoesNotCount() {
        Battery locked = new Battery(5);
        locked.extractable = false;
        assertFalse(DrainableItems.anyExtractable(Collections.<EnergyAccess>singletonList(locked)));
    }

    private static final class Battery implements EnergyAccess {
        private long amount;
        private boolean extractable = true;

        Battery(long amount) {
            this.amount = amount;
        }

        @Override
        public long getAmount() {
            return amount;
        }

        @Override
        public long getCapacity() {
            return 100;
        }

        @Override
        public long insert(long maxAmount, boolean simulate) {
            return 0;
        }

        @Override
        public long extract(long maxAmount, boolean simulate) {
            if (!extractable) return 0;
            long taken = Math.min(maxAmount, amount);
            if (!simulate) amount -= taken;
            return taken;
        }
    }
}
