package omtteam.openmodularturrets.core.world;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FloodFillTest {
    private static final BlockPosition ORIGIN = new BlockPosition(0, 64, 0);

    /** A straight line of {@code length} blocks along +x, starting at {@link #ORIGIN}. */
    private static Set<BlockPosition> line(int length) {
        Set<BlockPosition> blocks = new HashSet<>();
        for (int i = 0; i < length; i++) {
            blocks.add(new BlockPosition(i, 64, 0));
        }
        return blocks;
    }

    @Test
    void startThatDoesNotJoinFindsNothing() {
        assertEquals(List.of(), FloodFill.collect(ORIGIN, pos -> false, 10));
    }

    @Test
    void nonPositiveLimitFindsNothing() {
        assertEquals(List.of(), FloodFill.collect(ORIGIN, pos -> true, 0));
    }

    @Test
    void followsFacesOnly() {
        Set<BlockPosition> blocks = Set.of(ORIGIN, ORIGIN.relative(Face.UP), new BlockPosition(1, 65, 0),
                new BlockPosition(5, 64, 0));
        List<BlockPosition> found = FloodFill.collect(ORIGIN, blocks::contains, 10);
        assertEquals(Set.of(ORIGIN, ORIGIN.relative(Face.UP), new BlockPosition(1, 65, 0)), Set.copyOf(found),
                "connected through faces, the separate block is left out");
        assertEquals(found.size(), Set.copyOf(found).size(), "no position twice");
    }

    @Test
    void diagonalNeighboursAreNotConnected() {
        Set<BlockPosition> blocks = Set.of(ORIGIN, new BlockPosition(1, 65, 0));
        assertEquals(List.of(ORIGIN), FloodFill.collect(ORIGIN, blocks::contains, 10));
    }

    @Test
    void nearestFirstAndCutAtTheLimit() {
        Set<BlockPosition> blocks = line(10);
        List<BlockPosition> found = FloodFill.collect(new BlockPosition(4, 64, 0), blocks::contains, 3);
        assertEquals(3, found.size());
        assertEquals(new BlockPosition(4, 64, 0), found.get(0));
        assertEquals(Set.of(new BlockPosition(3, 64, 0), new BlockPosition(5, 64, 0)),
                Set.copyOf(found.subList(1, 3)), "the direct neighbours come before anything further away");
    }

    @Test
    void findsAllWithinTheLimit() {
        assertEquals(line(50), Set.copyOf(FloodFill.collect(ORIGIN, line(50)::contains, 1024)));
    }

    @Test
    void asksEachPositionAtMostOnce() {
        Set<BlockPosition> blocks = new HashSet<>();
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                blocks.add(new BlockPosition(x, 64, z));
            }
        }
        Set<BlockPosition> asked = new HashSet<>();
        FloodFill.collect(ORIGIN, pos -> {
            assertTrue(asked.add(pos), "asked twice: " + pos);
            return blocks.contains(pos);
        }, 1024);
    }
}
