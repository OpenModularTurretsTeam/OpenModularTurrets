package omtteam.openmodularturrets.core.world;

import omtteam.omlib.core.world.BlockPosition;
import omtteam.omlib.core.world.Face;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The small world value types the core builds on (turret positions, entity queries, aiming). */
class WorldTypesTest {
    @Test
    void relativeStepsOneBlockTowardsTheFace() {
        BlockPosition pos = new BlockPosition(10, 64, -5);
        assertEquals(new BlockPosition(11, 64, -5), pos.relative(Face.EAST));
        assertEquals(new BlockPosition(10, 63, -5), pos.relative(Face.DOWN));
        assertEquals(new BlockPosition(10, 64, -6), pos.relative(Face.NORTH));
    }

    @Test
    void boxEdgesAreInside() {
        Box box = Box.around(new BlockPosition(0, 0, 0), 2);
        assertTrue(box.contains(-2, -2, -2));
        assertTrue(box.contains(2, 2, 2));
        assertFalse(box.contains(2.01, 0, 0));
        assertFalse(box.contains(0, -2.01, 0));
        assertFalse(box.contains(0, 0, 2.01));
    }

    @Test
    void vectorArithmetic() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        assertEquals(new Vector3(-3, -3, -3), a.subtract(b));
        assertEquals(new Vector3(-3, 6, -3), a.cross(b));
        assertEquals(new Vector3(0, 0, 1), new Vector3(1, 0, 0).cross(new Vector3(0, 1, 0)));
    }
}
