package omtteam.openmodularturrets.core.turret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmmoTypeTest {
    @Test
    void equalIdsAreEqualValues() {
        AmmoType shell = AmmoType.of("othermod", "shell");
        assertEquals(AmmoType.of("othermod", "shell"), shell);
        assertEquals(AmmoType.of("othermod", "shell").hashCode(), shell.hashCode());
        assertNotEquals(AmmoType.of("othermod", "bullet"), shell);
        assertNotEquals(AmmoType.of("thirdmod", "shell"), shell);
    }

    @Test
    void namesComeFromTheId() {
        AmmoType shell = AmmoType.of("othermod", "shell");
        assertEquals("othermod", shell.getNamespace());
        assertEquals("shell", shell.getPath());
        assertEquals("shell", shell.getSerializedName());
        assertEquals("othermod:shell", shell.getId());
        assertEquals("othermod:shell", shell.toString());
        assertEquals("openmodularturrets:bullet", AmmoType.BULLET.getId());
    }

    @Test
    void invalidIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> AmmoType.of("Other Mod", "shell"));
        assertThrows(IllegalArgumentException.class, () -> AmmoType.of("othermod", "Shell"));
        assertThrows(IllegalArgumentException.class, () -> AmmoType.of("othermod", ""));
        assertThrows(NullPointerException.class, () -> AmmoType.of(null, "shell"));
    }
}
