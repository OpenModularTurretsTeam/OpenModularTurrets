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
//? if <1.21.11 {
import net.minecraft.gametest.framework.GameTest;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.core.world.Face;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.BaseComputerTurrets;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands.Command;
import omtteam.openmodularturrets.core.computer.PlayerNames;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The computer commands ({@link BaseComputerCommands}, shared by the OpenComputers component) on a real base, with its
 * turrets through {@link BaseComputerTurrets}: which bases accept computers, computer control (no own targeting),
 * aiming and firing the turrets, and trust changes. The arguments are passed as a computer passes them (Lua numbers
 * as {@code Double}).
 */
//? if (neoforge && <1.21.11) || forge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings({"unused", "RedundantCast"})
public class ComputerControlGameTests implements OMTGameTests {
    private static final int TIMEOUT_TICKS = 200;
    /** How long a computer-controlled turret is watched for not firing at a target in range. */
    private static final int WATCH_TICKS = 60;
    /** The face of the turret in {@link TurretScene} (on top of the base), as a computer passes it. */
    private static final Double UP = (double) Face.UP.ordinal();
    /** Aim of the forced shots: east, down into the floor, so the projectiles stay in the scene. */
    private static final Double YAW = 90.0D;
    private static final Double PITCH = -30.0D;
    /** The cheapest turret on a base with addon slots (tier 2), so the serial port is needed. */
    private static final TurretType TURRET = TurretType.MACHINE_GUN_TURRET;

    private static final PlayerIdentity FRIEND = new PlayerIdentity(UUID.randomUUID(), "Computer_Friend");
    /** Knows only {@link #FRIEND} (any case), like the server's player list and name cache. */
    private static final PlayerNames NAMES = name -> name.equalsIgnoreCase(FRIEND.name()) ? Optional.of(FRIEND)
            : Optional.empty();

    /**
     * Tier 1 bases never accept computers, tier 2 to 4 only with a serial port addon, tier 5 always; a refused base
     * answers every command with {@link BaseComputerCommands#NO_ACCESS} and changes nothing.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "computer_commands", timeoutTicks = TIMEOUT_TICKS)
    public void onlyTierFiveOrASerialPortAcceptComputers(GameTestHelper helper) {
        TurretBaseBlockEntity tierOne = base(helper, new BlockPos(1, 1, 1), ModBlocks.TURRET_BASES.get(0));
        TurretBaseBlockEntity tierTwo = base(helper, new BlockPos(3, 1, 1), ModBlocks.TURRET_BASES.get(1));
        TurretBaseBlockEntity serialPort = base(helper, new BlockPos(5, 1, 1), ModBlocks.TURRET_BASES.get(1));
        serialPort.getInventory().setItem(TurretBaseInventory.ADDON_START, new ItemStack(ModItems.ADDON_SERIAL_PORT));
        TurretBaseBlockEntity tierFive = base(helper, new BlockPos(1, 1, 3), ModBlocks.TURRET_BASES.get(4));

        for (TurretBaseBlockEntity refused : new TurretBaseBlockEntity[]{tierOne, tierTwo}) {
            assertAnswer(helper, commands(refused).call(Command.getOwner, new Object[0]), BaseComputerCommands.NO_ACCESS);
            assertAnswer(helper, commands(refused).call(Command.setComputerControl, new Object[]{true}),
                    BaseComputerCommands.NO_ACCESS);
            helper.assertFalse(refused.getLogic().isComputerControlled(),
                    "A tier " + refused.getTier() + " base without serial port was put under computer control");
        }
        for (TurretBaseBlockEntity accepted : new TurretBaseBlockEntity[]{serialPort, tierFive}) {
            assertAnswer(helper, commands(accepted).call(Command.getType, new Object[0]), BaseComputerCommands.TYPE);
            assertAnswer(helper, commands(accepted).call(Command.setComputerControl, new Object[]{true}), true);
            helper.assertTrue(accepted.getLogic().isComputerControlled(),
                    "setComputerControl did not reach the tier " + accepted.getTier() + " base");
            assertAnswer(helper, commands(accepted).call(Command.isComputerControl, new Object[0]), true);
        }
        helper.succeed();
    }

    /** Under computer control the base does not target a mob in range, so its turret neither turns nor fires. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "computer_control_targeting", timeoutTicks = TIMEOUT_TICKS)
    public void computerControlStopsTargeting(GameTestHelper helper) {
        TurretBaseBlockEntity base = controlledScene(helper);
        int ammo = ammo(base);
        LivingEntity target = TurretScene.spawnTarget(helper, TurretScene.TARGET_IN_FRONT);
        TurretScene.onEachTick(helper, () -> {
            helper.assertTrue(base.getLogic().getTarget(Face.UP).isEmpty(), "A computer-controlled base picked a target");
            helper.assertTrue(ammo(base) == ammo, "A computer-controlled turret fired on its own");
            helper.assertTrue(target.getHealth() == target.getMaxHealth(), "The target was damaged");
            if (helper.getTick() >= WATCH_TICKS) {
                TurretScene.succeed(helper);
            }
        });
    }

    /**
     * {@code setTurretYawPitch} aims the turret, {@code forceShootTurret} fires one shot along the aim once the turret
     * is ready: a projectile appears at the turret and one bullet is used.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "computer_force_shot", timeoutTicks = TIMEOUT_TICKS)
    public void forceShootTurretFires(GameTestHelper helper) {
        TurretBaseBlockEntity base = controlledScene(helper);
        BaseComputerCommands commands = commands(base);
        assertAnswer(helper, commands.call(Command.setTurretYawPitch, new Object[]{UP, YAW, PITCH}), true);
        assertAnswer(helper, commands.call(Command.forceShootTurret, new Object[]{(double) Face.DOWN.ordinal()}), false);
        int ammo = ammo(base);
        TurretScene.succeedWhen(helper, () -> {
            Object[] answer = commands.call(Command.forceShootTurret, new Object[]{UP});
            helper.assertTrue(Boolean.TRUE.equals(answer[0]), "forceShootTurret answered " + Arrays.toString(answer));
            helper.assertFalse(projectilesAt(helper).isEmpty(), "The forced shot spawned no projectile");
            helper.assertTrue(ammo(base) < ammo, "The forced shot used no ammo");
        });
    }

    /** With auto fire on, a computer-controlled turret fires along its aim whenever it is ready. */
    @GameTest(template = EMPTY_STRUCTURE, batch = "computer_auto_fire", timeoutTicks = TIMEOUT_TICKS)
    public void autoFireFiresRepeatedly(GameTestHelper helper) {
        TurretBaseBlockEntity base = controlledScene(helper);
        BaseComputerCommands commands = commands(base);
        assertAnswer(helper, commands.call(Command.setAllYawPitch, new Object[]{YAW, PITCH}),
                "set yaw and pitch successfully");
        assertAnswer(helper, commands.call(Command.setTurretAutoForceFire, new Object[]{UP, true}), true);
        TurretBlockEntity turret = base.getTurret(Face.UP).orElseThrow();
        helper.assertTrue(turret.isAutoFire(), "setTurretAutoForceFire did not reach the turret");
        int ammo = ammo(base);
        TurretScene.succeedWhen(helper, () -> helper.assertTrue(ammo - ammo(base) >= 3,
                "Auto fire used " + (ammo - ammo(base)) + " bullets, expected at least 3"));
    }

    /**
     * Trust changes through the commands act as the owner and reach the base's trust list: a known name is added with
     * the given access level, its level changes, and it is removed again (names in any case); an unknown name is
     * refused.
     */
    @GameTest(template = EMPTY_STRUCTURE, batch = "computer_commands", timeoutTicks = TIMEOUT_TICKS)
    public void trustChangesReachTheTrustManager(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper, new BlockPos(1, 1, 1), ModBlocks.TURRET_BASES.get(4));
        TrustManager trust = base.getTrustManager();
        trust.setOwner(new PlayerIdentity(UUID.randomUUID(), "ComputerOwner"));
        BaseComputerCommands commands = commands(base);

        assertAnswer(helper, commands.call(Command.addTrustedPlayer, new Object[]{"Nobody_Known"}), "Name not valid!");
        helper.assertTrue(trust.getTrustedPlayers().isEmpty(), "An unknown name was added");

        assertAnswer(helper, commands.call(Command.addTrustedPlayer,
                new Object[]{"computer_friend", (double) AccessLevel.CHANGE_SETTINGS.ordinal()}), true);
        assertLevel(helper, trust, AccessLevel.CHANGE_SETTINGS);

        assertAnswer(helper, commands.call(Command.changeAccessLevel,
                new Object[]{"COMPUTER_FRIEND", (double) AccessLevel.OPEN_GUI.ordinal()}), true);
        assertLevel(helper, trust, AccessLevel.OPEN_GUI);

        assertAnswer(helper, commands.call(Command.removeTrustedPlayer, new Object[]{"Computer_Friend"}), true);
        helper.assertTrue(trust.getTrustedPlayer(FRIEND.uuid()).isEmpty(), "removeTrustedPlayer did not remove the player");
        helper.succeed();
    }

    /**
     * {@link TurretScene} with a {@link #TURRET} on a tier 2 base with a serial port addon, under computer control.
     */
    private static TurretBaseBlockEntity controlledScene(GameTestHelper helper) {
        TurretBaseBlockEntity base = TurretScene.build(helper, TURRET, TIMEOUT_TICKS);
        base.getInventory().setItem(TurretBaseInventory.ADDON_START, new ItemStack(ModItems.ADDON_SERIAL_PORT));
        assertAnswer(helper, commands(base).call(Command.setComputerControl, new Object[]{true}), true);
        return base;
    }

    /** A base without owner and turrets. */
    private static TurretBaseBlockEntity base(GameTestHelper helper, BlockPos pos, Block block) {
        helper.setBlock(pos, block);
        return (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(pos));
    }

    private static BaseComputerCommands commands(TurretBaseBlockEntity base) {
        return new BaseComputerCommands(base.getLogic(), new BaseComputerTurrets(base), NAMES);
    }

    private static int ammo(TurretBaseBlockEntity base) {
        return base.getInventory().getItem(TurretBaseInventory.AMMO_START).getCount();
    }

    /** The turret projectiles around the scene's turret. */
    private static List<TurretProjectile> projectilesAt(GameTestHelper helper) {
        return helper.getLevel().getEntitiesOfClass(TurretProjectile.class,
                new AABB(helper.absolutePos(TurretScene.TURRET)).inflate(3.0D));
    }

    private static void assertAnswer(GameTestHelper helper, Object[] answer, Object expected) {
        helper.assertTrue(answer.length >= 1 && expected.equals(answer[0]),
                "Expected " + expected + ", the command answered " + Arrays.toString(answer));
    }

    private static void assertLevel(GameTestHelper helper, TrustManager trust, AccessLevel expected) {
        Optional<AccessLevel> level = trust.getTrustedPlayer(FRIEND.uuid()).map(TrustedPlayer::getAccessLevel);
        helper.assertTrue(level.equals(Optional.of(expected)), "Computer_Friend has " + level + ", expected " + expected);
    }
}
