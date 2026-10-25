package omtteam.openmodularturrets.core.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Finds the blocks connected to a start block through their faces, e.g. the owned blocks a claim spreads to.
 */
public final class FloodFill {
    private FloodFill() {
    }

    /**
     * Positions connected to {@code start} through faces where {@code joins} is true, breadth-first (nearest first),
     * at most {@code limit}. {@code start} is included if it joins; if it does not, the result is empty.
     * {@code joins} is asked at most once per position.
     */
    public static List<BlockPosition> collect(BlockPosition start, Predicate<BlockPosition> joins, int limit) {
        List<BlockPosition> found = new ArrayList<>();
        if (limit <= 0 || !joins.test(start)) return found;
        Set<BlockPosition> seen = new HashSet<>();
        Queue<BlockPosition> queue = new ArrayDeque<>();
        seen.add(start);
        queue.add(start);
        while (!queue.isEmpty() && found.size() < limit) {
            BlockPosition pos = queue.remove();
            found.add(pos);
            for (Face face : Face.values()) {
                BlockPosition next = pos.relative(face);
                if (seen.add(next) && joins.test(next)) {
                    queue.add(next);
                }
            }
        }
        return found;
    }
}
