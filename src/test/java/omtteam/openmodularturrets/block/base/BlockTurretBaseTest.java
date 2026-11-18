package omtteam.openmodularturrets.block.base;

import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.util.player.Player;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** The name refresh on right-click ({@link BlockTurretBase#refreshName}). */
class BlockTurretBaseTest {
    private final UUID ownerId = UUID.randomUUID();
    private final UUID friendId = UUID.randomUUID();
    private TurretBase base;

    @BeforeEach
    void setUp() {
        base = new TurretBase(1, null);
        base.setOwner(new Player(ownerId, "owner", "team"));
        TrustedPlayer friend = new TrustedPlayer(new Player(friendId, "friend", "team"));
        friend.setAccessLevel(EnumAccessLevel.OPEN_GUI);
        base.getTrustManager().addTrustedPlayer(friend);
    }

    @Test
    void aRenamedOwnerIsStoredUnderTheNewName() {
        BlockTurretBase.refreshName(base, new PlayerInfo(ownerId, "newOwner", false));
        assertEquals("newOwner", base.getOwnerOrNull().getName());
        assertEquals("team", base.getOwnerOrNull().getTeamName());
        assertEquals("newOwner", base.getTrustManager().core().getOwner().name());
    }

    @Test
    void aRenamedTrustedPlayerIsStoredUnderTheNewName() {
        BlockTurretBase.refreshName(base, new PlayerInfo(friendId, "newFriend", false));
        assertNull(base.getTrustManager().getTrustedPlayer("friend"));
        assertEquals(EnumAccessLevel.OPEN_GUI, base.getTrustManager().getTrustedPlayer("newFriend").getAccessLevel());
    }

    @Test
    void otherPlayersChangeNothing() {
        BlockTurretBase.refreshName(base, new PlayerInfo(UUID.randomUUID(), "owner2", false));
        assertEquals("owner", base.getOwnerOrNull().getName());
        assertEquals("friend", base.getTrustManager().getTrustedPlayers().get(0).getName());
    }
}
