package omtteam.openmodularturrets.core.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorLogicTest {
    private static final long PER_TICK = 40;

    private GeneratorLogic logic;
    /** Items in the fuel slot, each burning {@link #ticksPerItem} ticks. */
    private int items;
    private int ticksPerItem;
    private int consumed;

    @BeforeEach
    void setUp() {
        logic = new GeneratorLogic();
        items = 2;
        ticksPerItem = 3;
        consumed = 0;
    }

    private int consumeNext() {
        if (items == 0) return 0;
        items--;
        consumed++;
        return ticksPerItem;
    }

    private OptionalLong burn(long freeSpace) {
        return logic.burn(freeSpace, PER_TICK, this::consumeNext);
    }

    @Test
    void burnsOneItemAfterAnother() {
        for (int tick = 0; tick < 3; tick++) {
            assertEquals(OptionalLong.of(PER_TICK), burn(1000));
            assertEquals(1, consumed, "tick " + tick);
        }
        assertEquals(0, logic.getBurnTime());
        assertEquals(OptionalLong.of(PER_TICK), burn(1000));
        assertEquals(2, consumed, "the next item starts once the last one burnt out");
        assertEquals(3, logic.getBurnDuration());
        assertEquals(2, logic.getBurnTime());
    }

    @Test
    void stopsWithoutFuel() {
        items = 0;
        assertTrue(burn(1000).isEmpty());
        assertEquals(0, consumed);
    }

    @Test
    void pausesWhileTheBufferIsFull() {
        burn(1000);
        assertTrue(burn(0).isEmpty());
        assertEquals(2, logic.getBurnTime(), "no fuel is wasted while full");
        assertEquals(1, consumed);
        // a full buffer never starts a new item either
        logic.load(0, 3);
        assertTrue(burn(0).isEmpty());
        assertEquals(1, consumed);
    }

    @Test
    void neverOverfillsTheBuffer() {
        assertEquals(OptionalLong.of(15), burn(15));
    }

    @Test
    void restoresTheBurnState() {
        logic.load(5, 10);
        assertEquals(OptionalLong.of(PER_TICK), burn(1000));
        assertEquals(0, consumed, "keeps burning the saved item");
        assertEquals(4, logic.getBurnTime());
        assertEquals(10, logic.getBurnDuration());
    }
}
