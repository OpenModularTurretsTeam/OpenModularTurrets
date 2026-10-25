package omtteam.openmodularturrets.compat;

import net.minecraft.util.EnumFacing;
import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.compat.OMLibModCompatibility;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.compat.BaseComputerCommands.Command;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseComputerCommandsTest {
    private boolean savedComputerCraftLoaded;
    private TurretBase base;
    private BaseComputerCommands commands;

    @BeforeEach
    void setUp() {
        savedComputerCraftLoaded = OMLibModCompatibility.ComputerCraftLoaded;
        OMLibModCompatibility.ComputerCraftLoaded = true; // tier 5 bases are computer accessible with a computer mod
        base = new TurretBase(5, null);
        base.setOwner(new Player(UUID.randomUUID(), "owner", "team"));
        TrustedPlayer trusted = new TrustedPlayer(new Player(UUID.randomUUID(), "friend", "team"));
        trusted.setAccessLevel(EnumAccessLevel.NONE);
        base.getTrustManager().addTrustedPlayer(trusted);
        commands = new BaseComputerCommands(base);
    }

    @AfterEach
    void tearDown() {
        OMLibModCompatibility.ComputerCraftLoaded = savedComputerCraftLoaded;
    }

    private EnumAccessLevel level(String name) {
        return base.getTrustManager().getTrustedPlayer(name).getAccessLevel();
    }

    /** 3.x set the level on a copy of the trusted player, so the change was lost. */
    @Test
    void changeAccessLevelIsSaved() {
        assertArrayEquals(new Object[]{true}, commands.call(Command.changeAccessLevel, new Object[]{"friend", 3.0}));
        assertEquals(EnumAccessLevel.ADMIN, level("friend"));
        commands.call(Command.changeAccessLevel, new Object[]{"friend", "1"});
        assertEquals(EnumAccessLevel.OPEN_GUI, level("friend"));
    }

    @Test
    void invalidAccessLevelChangesNothing() {
        assertArrayEquals(new Object[]{"Invalid Access Level!"},
                commands.call(Command.changeAccessLevel, new Object[]{"friend", 4.0}));
        assertArrayEquals(new Object[]{"Invalid Access Level!"},
                commands.call(Command.changeAccessLevel, new Object[]{"friend", 1.5}));
        assertArrayEquals(new Object[]{"Invalid Access Level!"}, commands.call(Command.changeAccessLevel, new Object[]{"friend"}));
        assertEquals(EnumAccessLevel.NONE, level("friend"));
        assertArrayEquals(new Object[]{"Not found!"}, commands.call(Command.changeAccessLevel, new Object[]{"stranger", 1.0}));
    }

    @Test
    void missingOrWrongArgumentsGiveAnErrorInsteadOfAnException() {
        Object[] wrong = {BaseComputerCommands.WRONG_PARAMETERS};
        assertArrayEquals(wrong, commands.call(Command.setAttacksMobs, new Object[0]));
        assertArrayEquals(wrong, commands.call(Command.setAttacksMobs, new Object[]{"yes"}));
        assertArrayEquals(wrong, commands.call(Command.setTurretYawPitch, new Object[]{6.0, 10.0, 20.0}));
        assertArrayEquals(wrong, commands.call(Command.setTurretYawPitch, new Object[]{1.0, "up", 20.0}));
        assertArrayEquals(wrong, commands.call(Command.forceShootTurret, new Object[]{-1.0}));
        assertArrayEquals(wrong, commands.call(Command.removeTrustedPlayer, new Object[0]));
    }

    @Test
    void setTargetingAcceptsBooleansAndStrings() {
        commands.call(Command.setAttacksMobs, new Object[]{false});
        assertFalse(base.isAttacksMobs());
        commands.call(Command.setAttacksMobs, new Object[]{"true"});
        assertTrue(base.isAttacksMobs());
    }

    @Test
    void withoutComputerAccessNothingRuns() {
        OMLibModCompatibility.ComputerCraftLoaded = false;
        assertArrayEquals(new Object[]{BaseComputerCommands.NO_ACCESS},
                commands.call(Command.changeAccessLevel, new Object[]{"friend", 3.0}));
        assertEquals(EnumAccessLevel.NONE, level("friend"));
    }

    @Test
    void arguments() {
        assertEquals(Integer.valueOf(2), BaseComputerCommands.intArgument(new Object[]{2.0}, 0));
        assertEquals(Integer.valueOf(3), BaseComputerCommands.intArgument(new Object[]{"3"}, 0));
        assertNull(BaseComputerCommands.intArgument(new Object[]{2.5}, 0));
        assertNull(BaseComputerCommands.intArgument(new Object[0], 0));
        assertEquals(EnumFacing.EAST, BaseComputerCommands.faceArgument(new Object[]{5.0}, 0));
        assertNull(BaseComputerCommands.faceArgument(new Object[]{6.0}, 0));
        assertNull(BaseComputerCommands.floatArgument(new Object[]{Double.NaN}, 0));
        assertEquals(EnumAccessLevel.ADMIN, BaseComputerCommands.accessLevelArgument(new Object[]{3.0}, 0));
        assertNull(BaseComputerCommands.booleanArgument(new Object[]{1.0}, 0));
    }
}
