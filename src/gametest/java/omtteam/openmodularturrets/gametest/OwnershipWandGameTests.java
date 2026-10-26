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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.api.OMTApi;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.omlib.item.OwnershipWandItem;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.omlib.security.OwnedHost;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.omlib.security.TrustHost;

//? if >=1.21.11 {
/*import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
*///?}
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * The map maker's ownership wand: claiming and resetting, spreading to the connected blocks of the same owner, that
 * only game masters can use it, and that turrets still fire at a player holding it without affecting them. Every
 * immunity test has a control without the wand, so a test player that turrets cannot reach does not pass by accident.
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class OwnershipWandGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 40;
    private static final int EFFECT_TIMEOUT_TICKS = 200;
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
        TrustHost base = (TrustHost) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
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

    /** A slowing turret targets and fires at a player holding the wand, but does not slow them. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_immune_effect", timeoutTicks = EFFECT_TIMEOUT_TICKS)
    public void wandHolderIsFiredAtButNotSlowed(GameTestHelper helper) {
        ServerPlayer player = slowingTurretAimingAtPlayer(helper, true);
        TurretBaseLogic logic = ((TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(TurretScene.BASE))).getLogic();
        long full = logic.getEnergy().getAmount();
        helper.succeedWhen(() -> {
            helper.assertTrue(logic.getTargetIds().containsValue(player.getId()), "The wand holder is not targeted");
            helper.assertTrue(logic.getEnergy().getAmount() < full, "The turret has not fired yet");
            helper.assertTrue(!player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "The wand holder was slowed");
            removePlayer(helper, player);
        });
    }

    /** Control: without the wand the same player is slowed. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_control_effect", timeoutTicks = EFFECT_TIMEOUT_TICKS)
    public void playerWithoutWandIsSlowed(GameTestHelper helper) {
        ServerPlayer player = slowingTurretAimingAtPlayer(helper, false);
        helper.succeedWhen(() -> {
            helper.assertTrue(player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "The player was not slowed");
            removePlayer(helper, player);
        });
    }

    /**
     * Turret damage spares a wand holder in both hands: {@code canDamage} (projectiles, rays, melee, fire) and the
     * damage itself (explosions deal turret damage, which {@code LivingEntityMixin} cancels); without the wand the
     * same player takes it.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "ownership_wand_immune_damage", timeoutTicks = TIMEOUT_TICKS)
    public void wandHolderTakesNoTurretDamage(GameTestHelper helper) {
        helper.setBlock(BASE, ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(BASE));
        base.getTrustManager().setOwner(new PlayerIdentity(UUID.randomUUID(), "Owner"));
        ServerPlayer player = new TestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Target"), false);

        helper.assertTrue(OMTApi.canDamage(base, player), "Control: the player cannot be damaged without the wand");
        hurt(helper, player);
        helper.assertTrue(player.getHealth() < player.getMaxHealth(), "Control: turret damage did not hurt the player");

        player.setHealth(player.getMaxHealth());
        for (InteractionHand hand : InteractionHand.values()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            player.setItemInHand(hand, new ItemStack(ModItems.OWNERSHIP_WAND));
            helper.assertTrue(!OMTApi.canDamage(base, player), "canDamage allows the wand holder (" + hand + ")");
            hurt(helper, player);
            helper.assertTrue(player.getHealth() == player.getMaxHealth(), "Turret damage hurt the wand holder (" + hand + ")");
        }
        helper.succeed();
    }

    /**
     * Turret damage without the {@code canDamage} check, the way a turret explosion reaches the player. A grenade damage
     * type, as vanilla explosion damage scales with difficulty and deals nothing on the test server's peaceful setting.
     */
    private static void hurt(GameTestHelper helper, ServerPlayer player) {
        TurretDamage.hurt(helper.getLevel(), player, ModDamageTypes.GRENADE, 4.0F, null, null, TurretBaseLogic.NO_LOOT);
    }

    /**
     * A slowing turret with player targeting on, and a vulnerable test player (with or without the wand) added to the
     * level 5 blocks in front of it; the turret, base and player are removed when the test ends.
     */
    private static ServerPlayer slowingTurretAimingAtPlayer(GameTestHelper helper, boolean wand) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TurretType.SLOWING_TURRET, EFFECT_TIMEOUT_TICKS);
        base.getLogic().setTargetPlayers(true);
        ServerPlayer player = new TestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "Target"), false);
        if (wand) player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.OWNERSHIP_WAND));
        BlockPos pos = helper.absolutePos(TurretScene.TARGET_IN_FRONT);
        player.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        helper.getLevel().addNewPlayer(player);
        helper.runAfterDelay(EFFECT_TIMEOUT_TICKS - 1, () -> removePlayer(helper, player));
        return player;
    }

    private static void removePlayer(GameTestHelper helper, ServerPlayer player) {
        if (!player.isRemoved()) helper.getLevel().removePlayerImmediately(player, Entity.RemovalReason.DISCARDED);
        TurretScene.clear(helper);
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
        ServerPlayer player = new TestPlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), name), true);
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
        return (OwnedHost) helper.getLevel().getBlockEntity(helper.absolutePos(relative));
    }

    private static void assertOwner(GameTestHelper helper, BlockPos relative, PlayerIdentity expected) {
        PlayerIdentity owner = owned(helper, relative).getOwner();
        boolean matches = expected == null ? owner == null : owner != null && owner.is(expected.uuid());
        helper.assertTrue(matches, "Owner at " + relative + " is " + owner + ", expected " + expected);
    }

    /**
     * A fake player that can be hurt (the loaders' fake players may be invulnerable) and, optionally, counts as a game
     * master: creative abilities and operator permission level 2, without changing the server's operator list. Every
     * test uses new profiles.
     */
    private static final class TestPlayer extends FakePlayer {
        private final boolean gameMaster;

        private TestPlayer(ServerLevel level, GameProfile profile, boolean gameMaster) {
            super(level, profile);
            this.gameMaster = gameMaster;
            getAbilities().instabuild = gameMaster;
            endSpawnProtection();
        }

        /**
         * New players ignore damage for their first 60 ticks, and fake players never tick that down; the field is
         * private, and game tests run with Mojang names on every loader.
         */
        private void endSpawnProtection() {
            // 1.21.11 has no such field; there the player's isInvulnerableTo override below is enough
            //? if <1.21.11 {
            try {
                Field field = ServerPlayer.class.getDeclaredField("spawnInvulnerableTime");
                field.setAccessible(true);
                field.setInt(this, 0);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot end the spawn protection of the test player", e);
            }
            //?}
        }

        //? if >=1.21.11 {
        /*@Override
        public PermissionSet permissions() {
            return gameMaster ? LevelBasedPermissionSet.GAMEMASTER : LevelBasedPermissionSet.ALL;
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
            return false;
        }
        *///?} else {
        @Override
        public int getPermissionLevel() {
            return gameMaster ? 2 : 0;
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public boolean isInvulnerableTo(DamageSource source) {
            return false;
        }
        //?}
    }
}
