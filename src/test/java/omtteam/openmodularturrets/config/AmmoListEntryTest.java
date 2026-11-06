package omtteam.openmodularturrets.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmoListEntryTest {
    @Test
    void itemWithDamage() {
        AmmoListEntry e = AmmoListEntry.parse("minecraft:cobblestone,2");
        assertEquals("minecraft", e.modId);
        assertEquals("cobblestone", e.itemName);
        assertEquals(-1, e.meta);
        assertEquals(2, e.damage);
    }

    @Test
    void itemWithMetaDefaultsToDamageOne() {
        AmmoListEntry e = AmmoListEntry.parse("minecraft:planks:3");
        assertEquals(3, e.meta);
        assertEquals(1, e.damage);
    }

    @Test
    void oreEntry() {
        AmmoListEntry e = AmmoListEntry.parse("ore:plankWood,3");
        assertEquals("plankWood", e.oreName);
        assertNull(e.modId);
        assertEquals(3, e.damage);
    }

    @Test
    void invalidLines() {
        assertThrows(IllegalArgumentException.class, () -> AmmoListEntry.parse("ore:"));
        assertThrows(IllegalArgumentException.class, () -> AmmoListEntry.parse("cobblestone"));
    }
}
