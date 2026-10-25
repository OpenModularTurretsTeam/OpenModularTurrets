package omtteam.openmodularturrets.gametest;

import com.mojang.authlib.GameProfile;
//? if fabric {
import net.fabricmc.fabric.api.entity.FakePlayer;
//?}
//? if neoforge {
/*import net.neoforged.neoforge.common.util.FakePlayer;
*///?}
//? if forge {
/*import net.minecraftforge.common.util.FakePlayer;
*///?}
//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if forge {
/*import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
*///?}
//? if neoforge || forge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustedPlayer;
import omtteam.openmodularturrets.item.OwnershipWandItem;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;
import omtteam.openmodularturrets.security.TrustHost;

import java.util.List;
import java.util.UUID;

/**
 * The map maker's ownership wand: claiming and resetting, spreading to the connected blocks of the same owner, and that
 * only game masters can use it.
 */
//? if neoforge || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class OwnershipWandGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 40;
    private static final BlockPos BASE = new BlockPos(1, 1, 1);
    private static final BlockPos SAME_OWNER = BASE.east();
    private static final BlockPos OTHER_OWNER = SAME_OWNER.east();
    private static final BlockPos UNOWNED = BASE.south();

    /**
     * A wand claim on a base makes the game master the owner of it and of the connected block of the same owner, with
     * an empty trust list; a connected block of another owner and an unowned one stay as they are.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_claim", timeoutTicks = TIMEOUT_TICKS)
    public void wandClaimSpreadsToTheSameOwner(GameTestHelper helper) {
        PlayerIdentity owner = new PlayerIdentity(UUID.randomUUID(), "Owner");
        PlayerIdentity other = new PlayerIdentity(UUID.randomUUID(), "Other");
        buildScene(helper, owner, other);
        TrustHost base = (TrustHost) helper.getBlockEntity(BASE);
        base.getTrustManager().load(owner, List.of(
                new TrustedPlayer(new PlayerIdentity(UUID.randomUUID(), "Friend"), AccessLevel.ADMIN)));
        ServerPlayer master = gameMaster(helper, "Master");

        rightClick(helper, master, BASE, false);

        PlayerIdentity expected = PlayerAdapter.identity(master);
        assertOwner(helper, BASE, expected);
        assertOwner(helper, SAME_OWNER, expected);
        assertOwner(helper, OTHER_OWNER, other);
        assertOwner(helper, UNOWNED, null);
        helper.assertTrue(base.getTrustManager().getTrustedPlayers().isEmpty(), "The trust list was not cleared");
        helper.succeed();
    }

    /** With spreading switched off, a sneaking wand click removes the owner of the clicked block only. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_reset", timeoutTicks = TIMEOUT_TICKS)
    public void wandResetWithoutSpreading(GameTestHelper helper) {
        PlayerIdentity owner = new PlayerIdentity(UUID.randomUUID(), "Owner");
        PlayerIdentity other = new PlayerIdentity(UUID.randomUUID(), "Other");
        buildScene(helper, owner, other);
        ServerPlayer master = gameMaster(helper, "Master");
        OwnershipWandItem.setSpreading(master.getMainHandItem(), false);

        rightClick(helper, master, BASE, true);

        assertOwner(helper, BASE, null);
        assertOwner(helper, SAME_OWNER, owner);
        helper.succeed();
    }

    /** Players who are not game masters change nothing with the wand. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_denied", timeoutTicks = TIMEOUT_TICKS)
    public void wandNeedsAGameMaster(GameTestHelper helper) {
        PlayerIdentity owner = new PlayerIdentity(UUID.randomUUID(), "Owner");
        buildScene(helper, owner, new PlayerIdentity(UUID.randomUUID(), "Other"));
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Player"));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OWNERSHIP_WAND));

        rightClick(helper, player, BASE, false);
        rightClick(helper, player, BASE, true);

        assertOwner(helper, BASE, owner);
        helper.succeed();
    }

    /** A base and a hardened block of {@code owner}, a hardened block of {@code other} behind them, an unowned one beside. */
    private static void buildScene(GameTestHelper helper, PlayerIdentity owner, PlayerIdentity other) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(0));
        helper.setBlock(SAME_OWNER, ModBlocks.HARDENED.get(0));
        helper.setBlock(OTHER_OWNER, ModBlocks.HARDENED.get(0));
        helper.setBlock(UNOWNED, ModBlocks.HARDENED.get(0));
        owned(helper, BASE).setOwner(owner);
        owned(helper, SAME_OWNER).setOwner(owner);
        owned(helper, OTHER_OWNER).setOwner(other);
    }

    /** An operator in creative mode with the wand in the main hand. */
    private static ServerPlayer gameMaster(GameTestHelper helper, String name) {
        ServerPlayer player = new GameMasterPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OWNERSHIP_WAND));
        return player;
    }

    /** A main-hand right click on the top of the block, as the server handles it. */
    private static void rightClick(GameTestHelper helper, ServerPlayer player, BlockPos relative, boolean sneaking) {
        BlockPos pos = helper.absolutePos(relative);
        player.setShiftKeyDown(sneaking);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
        player.setShiftKeyDown(false);
    }

    private static OwnedHost owned(GameTestHelper helper, BlockPos relative) {
        return (OwnedHost) helper.getBlockEntity(relative);
    }

    private static void assertOwner(GameTestHelper helper, BlockPos relative, PlayerIdentity expected) {
        PlayerIdentity owner = owned(helper, relative).getOwner();
        boolean matches = expected == null ? owner == null : owner != null && owner.is(expected.uuid());
        helper.assertTrue(matches, "Owner at " + relative + " is " + owner + ", expected " + expected);
    }

    /**
     * A fake player that counts as a game master: creative abilities and operator permission level 2, without changing
     * the server's operator list. Fake players are cached by profile, so every test uses new profiles.
     */
    private static final class GameMasterPlayer extends FakePlayer {
        private GameMasterPlayer(ServerLevel level, GameProfile profile) {
            super(level, profile);
            getAbilities().instabuild = true;
        }

        @Override
        public int getPermissionLevel() {
            return 2;
        }
    }
}
