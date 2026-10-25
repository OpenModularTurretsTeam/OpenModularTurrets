package omtteam.openmodularturrets.gametest;

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
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.security.OwnedHost;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.List;
import java.util.UUID;

/**
 * Blocks without an owner: bases stay in the world, do not fire (unless {@code bases.unownedBasesActive} is set) and
 * take no turrets from players who did not claim them; a right-click claims them and the unowned blocks connected to
 * them. The test players are the loaders' fake players, so the claim tests turn on {@code general.fakePlayersCanClaim}
 * for the click only (in their own batch).
 */
//? if neoforge || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class OwnershipGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 100;
    /** Long enough for the laser turret to find the target and fire several times. */
    private static final int WATCH_TICKS = 60;
    private static final TurretType TYPE = TurretType.LASER_TURRET;

    /** A base without owner, ready to fire, neither drops itself nor shoots a target in front of its turret. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_unowned_inert", timeoutTicks = TIMEOUT_TICKS)
    public void unownedBaseStaysAndDoesNotFire(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TYPE, TIMEOUT_TICKS);
        base.getTrustManager().clearOwner();
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);

        helper.runAfterDelay(WATCH_TICKS, () -> {
            try {
                helper.assertBlockPresent(ModBlocks.TURRET_BASES.get(TYPE.getRequiredBaseTier() - 1), TurretScene.BASE);
                helper.assertTrue(target.getHealth() == target.getMaxHealth(), "An unowned base hurt the target");
            } finally {
                TurretScene.clear(helper);
            }
            helper.succeed();
        });
    }

    /** Only players who may use game master blocks attach turrets to an unowned base; others claim it first. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_unowned_attach", timeoutTicks = TIMEOUT_TICKS)
    public void unownedBaseTakesNoTurretsFromStrangers(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TYPE, TIMEOUT_TICKS);
        base.getTrustManager().clearOwner();
        ServerPlayer player = VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Claimer"));
        TurretBlock turret = ModBlocks.TURRETS.get(TYPE);
        Direction facing = Direction.EAST;

        TurretPlacement.Result unowned = turret.attachResult(helper.getLevel(),
                helper.absolutePos(TurretScene.BASE.relative(facing)), facing, player);
        helper.assertTrue(unowned == TurretPlacement.Result.NO_PERMISSION, "Attaching to an unowned base gave " + unowned);

        base.getTrustManager().claim(PlayerAdapter.identity(player));
        TurretPlacement.Result claimed = turret.attachResult(helper.getLevel(),
                helper.absolutePos(TurretScene.BASE.relative(facing)), facing, player);
        helper.assertTrue(claimed != null && claimed.allowed(), "Attaching after claiming gave " + claimed);
        TurretScene.succeed(helper);
    }

    /**
     * Clicking a turret on an unowned base claims the base and, through the turret and the passive blocks, everything
     * unowned connected to it; another player's base is neither taken nor walked through, and a block that is not
     * connected stays unowned.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_claim_spread", timeoutTicks = TIMEOUT_TICKS)
    public void claimSpreadsThroughConnectedBlocks(GameTestHelper helper) {
        BlockPos base = new BlockPos(1, 1, 1);
        BlockPos turret = base.above();
        BlockPos onTurret = turret.above();
        BlockPos hardened = base.east();
        BlockPos wall = hardened.east();
        BlockPos foreignBase = wall.east();
        BlockPos behindForeign = foreignBase.east();
        BlockPos apart = new BlockPos(1, 1, 5);
        helper.setBlock(base, ModBlocks.TURRET_BASES.get(0));
        helper.setBlock(turret, ModBlocks.TURRETS.get(TurretType.DISPOSABLE_TURRET));
        helper.setBlock(onTurret, ModBlocks.HARDENED.get(0));
        helper.setBlock(hardened, ModBlocks.HARDENED.get(0));
        helper.setBlock(wall, ModBlocks.WALLS.get(0));
        helper.setBlock(foreignBase, ModBlocks.TURRET_BASES.get(0));
        helper.setBlock(behindForeign, ModBlocks.HARDENED.get(0));
        helper.setBlock(apart, ModBlocks.HARDENED.get(0));
        PlayerIdentity stranger = new PlayerIdentity(UUID.randomUUID(), "Stranger");
        owned(helper, foreignBase).setOwner(stranger);
        ServerPlayer player = player(helper, "Claimer");

        withFakePlayerClaims(1024, () -> rightClick(helper, player, turret));

        PlayerIdentity claimer = PlayerAdapter.identity(player);
        for (BlockPos pos : List.of(base, onTurret, hardened, wall)) {
            assertOwner(helper, pos, claimer);
        }
        assertOwner(helper, foreignBase, stranger);
        assertOwner(helper, behindForeign, null);
        assertOwner(helper, apart, null);
        helper.setBlock(turret, Blocks.AIR);
        helper.succeed();
    }

    /** A claim walks at most {@code bases.claimSpreadLimit} blocks, nearest first. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_claim_limit", timeoutTicks = TIMEOUT_TICKS)
    public void claimStopsAtTheLimit(GameTestHelper helper) {
        for (int x = 1; x <= 6; x++) {
            helper.setBlock(new BlockPos(x, 1, 1), ModBlocks.HARDENED.get(0));
        }
        ServerPlayer player = player(helper, "Claimer");

        withFakePlayerClaims(3, () -> rightClick(helper, player, new BlockPos(1, 1, 1)));

        for (int x = 1; x <= 6; x++) {
            assertOwner(helper, new BlockPos(x, 1, 1), x <= 3 ? PlayerAdapter.identity(player) : null);
        }
        helper.succeed();
    }

    /** Without {@code general.fakePlayersCanClaim} a fake player (a machine) does not claim. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_claim_fake_player", timeoutTicks = TIMEOUT_TICKS)
    public void fakePlayersDoNotClaimByDefault(GameTestHelper helper) {
        BlockPos base = new BlockPos(1, 1, 1);
        helper.setBlock(base, ModBlocks.TURRET_BASES.get(0));
        helper.assertTrue(!OMTConfig.get().general.fakePlayersCanClaim, "fakePlayersCanClaim is on by default");

        rightClick(helper, player(helper, "Machine"), base);

        assertOwner(helper, base, null);
        helper.succeed();
    }

    /** Runs {@code click} with fake players allowed to claim and the given spread limit, then restores both. */
    private static void withFakePlayerClaims(int limit, Runnable click) {
        ConfigData config = OMTConfig.get();
        boolean fakePlayers = config.general.fakePlayersCanClaim;
        int spreadLimit = config.bases.claimSpreadLimit;
        config.general.fakePlayersCanClaim = true;
        config.bases.claimSpreadLimit = limit;
        try {
            click.run();
        } finally {
            config.general.fakePlayersCanClaim = fakePlayers;
            config.bases.claimSpreadLimit = spreadLimit;
        }
    }

    private static ServerPlayer player(GameTestHelper helper, String name) {
        return VersionCompat.fakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    }

    /** A main-hand right click on the top of the block, as the server handles it. */
    private static void rightClick(GameTestHelper helper, ServerPlayer player, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false);
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
    }

    private static OwnedHost owned(GameTestHelper helper, BlockPos relative) {
        return (OwnedHost) helper.getBlockEntity(relative);
    }

    private static void assertOwner(GameTestHelper helper, BlockPos relative, PlayerIdentity expected) {
        PlayerIdentity owner = owned(helper, relative).getOwner();
        boolean matches = expected == null ? owner == null : owner != null && owner.is(expected.uuid());
        helper.assertTrue(matches, "Owner at " + relative + " is " + owner + ", expected " + expected);
    }
}
