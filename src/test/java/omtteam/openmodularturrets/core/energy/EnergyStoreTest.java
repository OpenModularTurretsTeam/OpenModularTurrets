package omtteam.openmodularturrets.core.energy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnergyStoreTest {
    private int changes;
    private EnergyStore store;

    @BeforeEach
    void setUp() {
        changes = 0;
        store = new EnergyStore(1000, 100, 40, () -> changes++);
    }

    @Test
    void insertIsLimitedByMaxInsertAndFreeSpace() {
        assertEquals(100, store.insert(500, false));
        assertEquals(100, store.getAmount());
        store.generate(850);
        assertEquals(50, store.insert(100, false), "only 50 free");
        assertEquals(1000, store.getAmount());
        assertEquals(0, store.insert(100, false));
        assertEquals(0, store.insert(-5, false));
        assertEquals(3, changes, "two insertions and the generation; nothing that did not fit");
    }

    @Test
    void simulatedInsertChangesNothing() {
        assertEquals(100, store.insert(500, true));
        assertEquals(0, store.getAmount());
        assertEquals(0, changes);
    }

    @Test
    void extractIsLimitedByMaxExtractAndAmount() {
        store.generate(50);
        assertEquals(40, store.extract(500, false));
        assertEquals(10, store.getAmount());
        assertEquals(10, store.extract(500, false), "only 10 left");
        assertEquals(0, store.extract(500, false));
        assertEquals(0, store.extract(-5, false));
        assertEquals(3, changes, "the generation and two extractions");
    }

    @Test
    void simulatedExtractChangesNothing() {
        store.generate(500);
        changes = 0;
        assertEquals(40, store.extract(500, true));
        assertEquals(500, store.getAmount());
        assertEquals(0, changes);
    }

    @Test
    void generationIgnoresMaxInsert() {
        assertEquals(800, store.generate(800));
        assertEquals(200, store.generate(500), "limited by free space");
        assertEquals(0, store.generate(10));
        assertEquals(0, store.generate(-10));
        assertEquals(1000, store.getAmount());
        assertEquals(2, changes, "only real changes notify");
    }

    @Test
    void consumptionIsAllOrNothing() {
        store.generate(300);
        assertTrue(store.consume(200));
        assertEquals(100, store.getAmount());
        assertFalse(store.consume(150));
        assertEquals(100, store.getAmount());
        assertFalse(store.consume(-1));
        assertTrue(store.consume(0));
        assertEquals(2, changes, "generate and the one real consumption");
    }

    @Test
    void drainIgnoresMaxExtract() {
        store.generate(300);
        changes = 0;
        assertEquals(200, store.drain(200));
        assertEquals(100, store.drain(500), "limited by the amount");
        assertEquals(0, store.drain(10));
        assertEquals(0, store.drain(-10));
        assertEquals(0, store.getAmount());
        assertEquals(2, changes);
    }

    @Test
    void loweringTheCapacityClampsTheStoredEnergy() {
        store.generate(800);
        changes = 0;
        store.setCapacity(2000);
        assertEquals(800, store.getAmount());
        assertEquals(0, changes);
        store.setCapacity(800);
        assertEquals(800, store.getAmount());
        assertEquals(0, changes, "a capacity equal to the stored energy changes nothing");
        store.setCapacity(500);
        assertEquals(500, store.getAmount());
        assertEquals(0, store.getFreeSpace());
        assertEquals(1, changes);
        store.setCapacity(-10);
        assertEquals(0, store.getCapacity());
        assertEquals(0, store.getAmount());
    }

    @Test
    void uncheckedAmountMayExceedTheCapacityUntilItIsSet() {
        store.setAmountUnchecked(5000);
        assertEquals(5000, store.getAmount());
        assertEquals(0, store.getFreeSpace(), "free space is never negative");
        assertEquals(0, changes, "no notification");
        store.setCapacity(1000);
        assertEquals(1000, store.getAmount());
        store.setAmountUnchecked(-5);
        assertEquals(0, store.getAmount());
    }

    @Test
    void limitsCanChange() {
        store.setMaxInsert(10);
        assertEquals(10, store.getMaxInsert());
        assertEquals(10, store.insert(100, false));
        store.setMaxInsert(-1);
        assertEquals(0, store.getMaxInsert());
        assertEquals(0, store.insert(100, false));
        store.setMaxExtract(5);
        assertEquals(5, store.getMaxExtract());
        assertEquals(5, store.extract(100, false));
        store.setMaxExtract(-1);
        assertEquals(0, store.getMaxExtract());
        assertEquals(0, store.extract(100, false));
    }
}
