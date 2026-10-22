package omtteam.openmodularturrets.core.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GeneratorLogicTest {
    private static final long PER_TICK = 40;

    /** A fuel slot holding {@code items} items that burn {@code ticksPerItem} ticks each. */
    private static final class Slot implements GeneratorLogic.FuelSlot {
        int items;
        final int ticksPerItem;
        int consumed;

        Slot(int items, int ticksPerItem) {
            this.items = items;
            this.ticksPerItem = ticksPerItem;
        }

        @Override
        public int consumeNext() {
            if (items == 0) return 0;
            items--;
            consumed++;
            return ticksPerItem;
        }
    }

    private GeneratorLogic logic;
    /** The only slot for the single-slot tests: two items of 3 ticks. */
    private Slot slot;

    @BeforeEach
    void setUp() {
        logic = new GeneratorLogic();
        slot = new Slot(2, 3);
    }

    private OptionalLong burn(long freeSpace) {
        return logic.burn(freeSpace, PER_TICK, 1, Arrays.asList(slot));
    }

    @Test
    void burnsOneItemAfterAnother() {
        for (int tick = 0; tick < 3; tick++) {
            assertEquals(OptionalLong.of(PER_TICK), burn(1000));
            assertEquals(1, slot.consumed, "tick " + tick);
        }
        assertEquals(0, logic.getBurnTime());
        assertEquals(OptionalLong.of(PER_TICK), burn(1000));
        assertEquals(2, slot.consumed, "the next item starts once the last one burnt out");
        assertEquals(3, logic.getBurnDuration());
        assertEquals(2, logic.getBurnTime());
    }

    @Test
    void stopsWithoutFuel() {
        slot.items = 0;
        assertFalse(burn(1000).isPresent());
        assertEquals(0, slot.consumed);
        assertFalse(logic.burn(1000, PER_TICK, 1, Collections.<Slot>emptyList()).isPresent(), "no slots at all");
    }

    @Test
    void pausesWhileTheBufferIsFull() {
        burn(1000);
        assertFalse(burn(0).isPresent());
        assertEquals(2, logic.getBurnTime(), "no fuel is wasted while full");
        assertEquals(1, slot.consumed);
        // a full buffer never starts a new item either
        logic.load(0, 3);
        assertFalse(burn(0).isPresent());
        assertEquals(1, slot.consumed);
    }

    @Test
    void neverOverfillsTheBuffer() {
        assertEquals(OptionalLong.of(15), burn(15));
    }

    @Test
    void restoresTheBurnState() {
        logic.load(5, 10);
        assertEquals(OptionalLong.of(PER_TICK), burn(1000));
        assertEquals(0, slot.consumed, "keeps burning the saved item");
        assertEquals(4, logic.getBurnTime());
        assertEquals(10, logic.getBurnDuration());
    }

    @Test
    void slotsAreUsedInOrder() {
        Slot first = new Slot(1, 2);
        Slot second = new Slot(1, 5);
        List<Slot> slots = Arrays.asList(first, second);
        logic.burn(1000, PER_TICK, 1, slots);
        assertEquals(1, first.consumed);
        assertEquals(0, second.consumed, "the second slot waits until the first is empty");
        logic.burn(1000, PER_TICK, 1, slots);
        logic.burn(1000, PER_TICK, 1, slots);
        assertEquals(1, second.consumed, "the next item comes from the second slot");
        assertEquals(5, logic.getBurnDuration());
    }

    @Test
    void emptySlotsAreSkipped() {
        Slot empty = new Slot(0, 2);
        Slot last = new Slot(1, 4);
        assertEquals(OptionalLong.of(PER_TICK), logic.burn(1000, PER_TICK, 1, Arrays.asList(empty, empty, last)));
        assertEquals(1, last.consumed);
        assertEquals(4, logic.getBurnDuration());
    }

    @Test
    void stopsWhenAllSlotsAreEmpty() {
        Slot a = new Slot(0, 2);
        Slot b = new Slot(0, 2);
        assertFalse(logic.burn(1000, PER_TICK, 1, Arrays.asList(a, b)).isPresent());
        assertEquals(0, logic.getBurnDuration());
    }

    @Test
    void multiplierDoublesPerUpgrade() {
        assertEquals(1, GeneratorLogic.multiplier(0));
        assertEquals(2, GeneratorLogic.multiplier(1));
        assertEquals(16, GeneratorLogic.multiplier(4));
        assertEquals(16, GeneratorLogic.multiplier(9), "at most 4 upgrades count");
        assertEquals(1, GeneratorLogic.multiplier(-1));
    }

    @Test
    void multipliedBurningIsFasterButYieldsTheSameEnergyPerItem() {
        Slot coal = new Slot(1, 10);
        long total = 0;
        int ticks = 0;
        OptionalLong produced;
        while ((produced = logic.burn(1000000, PER_TICK, 4, Arrays.asList(coal))).isPresent()) {
            total += produced.getAsLong();
            ticks++;
        }
        assertEquals(10 * PER_TICK, total, "one item yields its burn time times the energy per fuel tick");
        assertEquals(3, ticks, "4, 4 and the last 2 fuel ticks");
    }

    @Test
    void multipliedTicksProduceMore() {
        Slot coal = new Slot(1, 100);
        assertEquals(OptionalLong.of(16 * PER_TICK), logic.burn(1000000, PER_TICK, 16, Arrays.asList(coal)));
        assertEquals(84, logic.getBurnTime());
        assertEquals(OptionalLong.of(PER_TICK), logic.burn(1000000, PER_TICK, 1, Arrays.asList(coal)),
                "removing upgrades mid-burn just slows it down");
        assertEquals(83, logic.getBurnTime());
    }

    @Test
    void multipliedOutputIsCappedByTheBuffer() {
        Slot coal = new Slot(1, 100);
        assertEquals(OptionalLong.of(100), logic.burn(100, PER_TICK, 16, Arrays.asList(coal)));
    }
}
