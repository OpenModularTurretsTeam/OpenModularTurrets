package omtteam.openmodularturrets.gametest;

//? if neoforge && <1.21.11 {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if (neoforge && <1.21.11) || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
//? if >=1.21.11 {
/*import net.minecraft.core.UUIDUtil;
*///?}
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.network.TrustPacketHandler;
import omtteam.omlib.network.TrustPackets;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.omlib.security.TrustHost;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.List;
import java.util.UUID;

/**
 * The packets of the trusted player screen as the server handles them (OMLib's {@code TrustPacketHandler}): only admins
 * of a block within reach change its trust list, players are added by name, and access levels change one step at a
 * time within the block's levels. Disowning is tested in {@link OwnershipGameTests}. The players are the loaders' fake
 * players. On Fabric a player added by name is put on the server's player list for the request only ({@code online}):
 * vanilla's mock players log in for real, and mods on the NeoForge test server send them packets that NeoForge refuses.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class TrustListGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 100;
    /**
     * How long a test that expects no entry waits for a name lookup on a background thread to come back to the server
     * thread (it took more than 20 ticks in a run with several game test servers at once).
     */
    private static final int LOOKUP_TICKS = 60;
    private static final BlockPos BASE = new BlockPos(1, 1, 1);

    /** The owner adds an online player by name (surrounding spaces ignored); the new entry has no access yet. */
    //? if fabric {
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void ownerAddsAnOnlinePlayerByName(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer friend = playerAt(helper, "Online_Friend");

        online(helper, friend, () -> TrustPacketHandler.handleAdd(
                new TrustPackets.Add(helper.absolutePos(BASE), " Online_Friend "), owner));

        TrustedPlayer entry = trust.getTrustedPlayer(friend.getUUID()).orElse(null);
        helper.assertTrue(entry != null, "Online_Friend was not added");
        helper.assertTrue(entry.getAccessLevel() == AccessLevel.NONE, "New entry has " + entry.getAccessLevel());
        helper.assertTrue(trust.getTrustedPlayers().size() == 1, "Trust list is " + trust.getTrustedPlayers().size() + " long");
        helper.succeed();
    }
    //?}

    /**
     * A trusted player below admin cannot add anyone. The request is refused before the name is looked up; on 1.21.11 the
     * lookup would find the name (see {@link #namesOfOfflinePlayersAreLookedUp}).
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_add_not_admin", timeoutTicks = TIMEOUT_TICKS)
    public void playersBelowAdminCannotAdd(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer settings = trusted(helper, trust, owner, "Settings", AccessLevel.CHANGE_SETTINGS);
        TrustPacketHandler.handleAdd(new TrustPackets.Add(helper.absolutePos(BASE), "Offline_c41e"), settings);

        helper.runAfterDelay(LOOKUP_TICKS, () -> {
            helper.assertTrue(trust.getTrustedPlayers().size() == 1, "A player below admin added Offline_c41e");
            helper.succeed();
        });
    }

    /**
     * A name of a player who is not online is looked up on a background thread and added when the answer is back on the
     * server thread. The game test server runs in offline mode: on 1.21.11 its name cache gives the name's offline UUID
     * (as an offline-mode server does), before 1.21.11 it has no profile cache and the name is not found. An empty name is
     * not looked up at all.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_add_offline", timeoutTicks = TIMEOUT_TICKS)
    public void namesOfOfflinePlayersAreLookedUp(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        TrustPacketHandler.handleAdd(new TrustPackets.Add(helper.absolutePos(BASE), "   "), owner);
        TrustPacketHandler.handleAdd(new TrustPackets.Add(helper.absolutePos(BASE), "Offline_7f3a"), owner);

        //? if >=1.21.11 {
        /*PlayerIdentity expected = new PlayerIdentity(UUIDUtil.createOfflinePlayerUUID("Offline_7f3a"), "Offline_7f3a");
        helper.succeedWhen(() -> {
            List<TrustedPlayer> entries = trust.getTrustedPlayers();
            helper.assertTrue(entries.size() == 1 && entries.get(0).getIdentity().equals(expected),
                    "Trust list is " + entries.stream().map(TrustedPlayer::getIdentity).toList() + ", expected " + expected);
        });
        *///?} else {
        helper.runAfterDelay(LOOKUP_TICKS, () -> {
            List<TrustedPlayer> entries = trust.getTrustedPlayers();
            helper.assertTrue(entries.isEmpty(), "Trust list is " + entries.stream().map(TrustedPlayer::getIdentity).toList());
            helper.succeed();
        });
        //?}
    }

    /** The owner removes a trusted player; a player below admin cannot. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void onlyAdminsRemoveTrustedPlayers(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer gui = trusted(helper, trust, owner, "Gui", AccessLevel.OPEN_GUI);
        ServerPlayer friend = trusted(helper, trust, owner, "Friend", AccessLevel.NONE);

        TrustPacketHandler.handleRemove(new TrustPackets.Remove(helper.absolutePos(BASE), friend.getUUID()), gui);
        helper.assertTrue(trust.getTrustedPlayer(friend.getUUID()).isPresent(), "A player below admin removed a trusted player");

        TrustPacketHandler.handleRemove(new TrustPackets.Remove(helper.absolutePos(BASE), friend.getUUID()), owner);
        helper.assertTrue(trust.getTrustedPlayer(friend.getUUID()).isEmpty(), "The owner did not remove the trusted player");
        helper.assertTrue(trust.getTrustedPlayer(gui.getUUID()).isPresent(), "Another player was removed too");
        helper.succeed();
    }

    /** Raising and lowering go one step at a time; other steps are ignored, and the levels stop at both ends. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void accessLevelsChangeOneStepAtATime(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer friend = trusted(helper, trust, owner, "Friend", AccessLevel.NONE);

        changeAccess(helper, owner, friend, 1);
        assertLevel(helper, trust, friend, AccessLevel.OPEN_GUI);
        changeAccess(helper, owner, friend, 2);
        changeAccess(helper, owner, friend, 0);
        assertLevel(helper, trust, friend, AccessLevel.OPEN_GUI);
        changeAccess(helper, owner, friend, 1);
        changeAccess(helper, owner, friend, 1);
        changeAccess(helper, owner, friend, 1);
        assertLevel(helper, trust, friend, AccessLevel.ADMIN);
        changeAccess(helper, owner, friend, -1);
        assertLevel(helper, trust, friend, AccessLevel.CHANGE_SETTINGS);
        for (int i = 0; i < 4; i++) {
            changeAccess(helper, owner, friend, -1);
        }
        assertLevel(helper, trust, friend, AccessLevel.NONE);
        helper.succeed();
    }

    /** A player below admin changes no access level, not even their own. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void playersBelowAdminCannotChangeAccess(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer settings = trusted(helper, trust, owner, "Settings", AccessLevel.CHANGE_SETTINGS);
        ServerPlayer friend = trusted(helper, trust, owner, "Friend", AccessLevel.NONE);

        changeAccess(helper, settings, friend, 1);
        changeAccess(helper, settings, settings, 1);
        assertLevel(helper, trust, friend, AccessLevel.NONE);
        assertLevel(helper, trust, settings, AccessLevel.CHANGE_SETTINGS);
        helper.succeed();
    }

    /** The camouflage trap only knows trusted players and admins: one step up makes an admin. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void trapLevelsStepBetweenTrustedAndAdmin(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.CAMO_TRAP);
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer friend = trusted(helper, trust, owner, "Friend", AccessLevel.NONE);

        changeAccess(helper, owner, friend, 1);
        assertLevel(helper, trust, friend, AccessLevel.ADMIN);
        changeAccess(helper, owner, friend, -1);
        assertLevel(helper, trust, friend, AccessLevel.NONE);
        helper.succeed();
    }

    /** Requests from players more than the container distance away are ignored. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "trust_list", timeoutTicks = TIMEOUT_TICKS)
    public void requestsFromFarAwayAreIgnored(GameTestHelper helper) {
        TrustManager trust = base(helper, ModBlocks.TURRET_BASES.get(0));
        ServerPlayer owner = owner(helper, trust);
        ServerPlayer friend = trusted(helper, trust, owner, "Friend", AccessLevel.NONE);
        BlockPos pos = helper.absolutePos(BASE);
        owner.moveTo(pos.getX() + 9.5D, pos.getY(), pos.getZ() + 0.5D);

        changeAccess(helper, owner, friend, 1);
        assertLevel(helper, trust, friend, AccessLevel.NONE);
        TrustPacketHandler.handleRemove(new TrustPackets.Remove(pos, friend.getUUID()), owner);
        helper.assertTrue(trust.getTrustedPlayer(friend.getUUID()).isPresent(), "Removed from far away");
        TrustPacketHandler.handleDisown(new TrustPackets.Disown(pos), owner);
        helper.assertTrue(trust.getOwner() != null, "Disowned from far away");
        helper.succeed();
    }

    /** Places {@code block} at {@link #BASE} and returns its trust list. */
    private static TrustManager base(GameTestHelper helper, Block block) {
        helper.setBlock(BASE, block);
        return ((TrustHost) helper.getLevel().getBlockEntity(helper.absolutePos(BASE))).getTrustManager();
    }

    /** A fake player next to the block who owns it. */
    private static ServerPlayer owner(GameTestHelper helper, TrustManager trust) {
        ServerPlayer owner = playerAt(helper, "Owner");
        trust.setOwner(PlayerAdapter.identity(owner));
        return owner;
    }

    /** A fake player next to the block, on its trust list with {@code level}. */
    private static ServerPlayer trusted(GameTestHelper helper, TrustManager trust, ServerPlayer owner, String name, AccessLevel level) {
        ServerPlayer player = playerAt(helper, name);
        PlayerIdentity identity = PlayerAdapter.identity(player);
        trust.addTrustedPlayer(PlayerAdapter.info(owner), identity);
        trust.setAccessLevel(PlayerAdapter.info(owner), identity.uuid(), level);
        return player;
    }

    /** A fake player standing on top of the block, within reach for packets. */
    private static ServerPlayer playerAt(GameTestHelper helper, String name) {
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
        BlockPos pos = helper.absolutePos(BASE);
        player.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
        return player;
    }

    //? if fabric {
    /**
     * Runs {@code request} with {@code player} on the server's player list, where name lookups find online players. The
     * request is handled at once on the server thread, so no tick sees the player on the list. Fabric only: NeoForge and
     * Forge return a read-only view of the list.
     */
    private static void online(GameTestHelper helper, ServerPlayer player, Runnable request) {
        List<ServerPlayer> players = helper.getLevel().getServer().getPlayerList().getPlayers();
        players.add(player);
        try {
            request.run();
        } finally {
            players.remove(player);
        }
    }
    //?}

    private static void changeAccess(GameTestHelper helper, ServerPlayer actor, ServerPlayer target, int delta) {
        TrustPacketHandler.handleChangeAccess(new TrustPackets.ChangeAccess(helper.absolutePos(BASE), target.getUUID(), delta), actor);
    }

    private static void assertLevel(GameTestHelper helper, TrustManager trust, ServerPlayer player, AccessLevel expected) {
        AccessLevel level = trust.getTrustedPlayer(player.getUUID()).map(TrustedPlayer::getAccessLevel).orElse(null);
        helper.assertTrue(level == expected, player.getGameProfile() + " has " + level + ", expected " + expected);
    }
}
