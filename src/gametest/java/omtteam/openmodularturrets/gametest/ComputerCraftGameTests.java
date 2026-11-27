package omtteam.openmodularturrets.gametest;

//? if neoforge {
/*import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
*///?}
//? if neoforge {
/*import omtteam.openmodularturrets.OMT;
*///?}
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectArguments;
import dan200.computercraft.api.peripheral.IPeripheral;
//? if fabric {
import dan200.computercraft.api.peripheral.PeripheralLookup;
//?}
//? if neoforge {
/*import dan200.computercraft.api.peripheral.PeripheralCapability;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.computercraft.TurretBasePeripheral;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.util.Arrays;
import java.util.UUID;

/**
 * The CC: Tweaked peripheral of the turret base (CC: Tweaked is on the game test server of both loaders). The
 * loader's own peripheral lookup (what a computer or wired modem asks) finds the base and names the peripheral
 * {@code turret_base}, and the methods work with arguments as CC passes them (Lua numbers as doubles). The commands
 * themselves are tested in the unit tests of {@code BaseComputerCommands} and in {@code ComputerControlGameTests}.
 */
//? if neoforge {
/*@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
*///?}
@SuppressWarnings("unused")
public class ComputerCraftGameTests implements OMTGameTests {
    private static final BlockPos TIER_FIVE = new BlockPos(1, 1, 1);
    private static final BlockPos TIER_ONE = new BlockPos(4, 1, 1);
    private static final PlayerIdentity OWNER = new PlayerIdentity(UUID.randomUUID(), "CcTestOwner");

    @GameTest(template = EMPTY_STRUCTURE, batch = "computercraft")
    public void computersFindTheTurretBasePeripheral(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(TIER_FIVE);
        TurretBaseBlockEntity base = base(helper, TIER_FIVE, ModBlocks.TURRET_BASES.size() - 1);
        try {
            for (Direction side : Direction.values()) {
                IPeripheral peripheral = lookup(helper, pos, side);
                helper.assertTrue(peripheral != null, "CC: Tweaked has no peripheral on the " + side + " side of the base");
                helper.assertTrue(BaseComputerCommands.TYPE.equals(peripheral.getType()),
                        "The peripheral is not a turret_base: " + peripheral.getType());
                helper.assertTrue(peripheral.getTarget() == base, "The peripheral's target is not the base");
                helper.assertTrue(peripheral.equals(lookup(helper, pos, Direction.UP)),
                        "Two peripherals of one base are not equal, so a computer would detach and attach again");
            }
        } finally {
            helper.setBlock(TIER_FIVE, Blocks.AIR);
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "computercraft")
    public void methodsControlTheBase(GameTestHelper helper) throws LuaException {
        TurretBaseBlockEntity base = base(helper, TIER_FIVE, ModBlocks.TURRET_BASES.size() - 1);
        TurretBaseBlockEntity tierOne = base(helper, TIER_ONE, 0);
        try {
            TurretBasePeripheral peripheral = TurretBasePeripheral.of(base);
            helper.assertTrue(peripheral != null, "No peripheral for a base on a server level");
            assertResult(helper, peripheral.getOwner(args()), OWNER.name(), "getOwner");
            assertResult(helper, peripheral.setComputerControl(args(true)), true, "setComputerControl");
            helper.assertTrue(base.getLogic().isComputerControlled(), "setComputerControl did not reach the base");
            assertResult(helper, peripheral.isComputerControl(args()), true, "isComputerControl");
            assertResult(helper, peripheral.setAttacksMobs(args(false)), true, "setAttacksMobs");
            assertResult(helper, peripheral.isAttacksMobs(args()), false, "isAttacksMobs");
            assertResult(helper, peripheral.getMaxEnergyStorage(args()), base.getLogic().getEnergy().getCapacity(),
                    "getMaxEnergyStorage");
            // neither online nor in the server's name cache
            assertResult(helper, peripheral.addTrustedPlayer(args("Never_Joined_OMT")), "Name not valid!",
                    "addTrustedPlayer of an unknown name");
            helper.assertTrue(peripheral.getMode().getResult() != null, "getMode returned nothing");

            TurretBasePeripheral denied = TurretBasePeripheral.of(tierOne);
            assertResult(helper, denied.getOwner(args()), BaseComputerCommands.NO_ACCESS, "getOwner on tier 1");
            assertResult(helper, denied.setComputerControl(args(true)), BaseComputerCommands.NO_ACCESS,
                    "setComputerControl on tier 1");
            helper.assertFalse(tierOne.getLogic().isComputerControlled(), "A tier 1 base accepted computer control");
        } finally {
            helper.setBlock(TIER_FIVE, Blocks.AIR);
            helper.setBlock(TIER_ONE, Blocks.AIR);
        }
        helper.succeed();
    }

    /** A base of the given tier index with {@link #OWNER} at {@code pos}. */
    private static TurretBaseBlockEntity base(GameTestHelper helper, BlockPos pos, int tierIndex) {
        helper.setBlock(pos, ModBlocks.TURRET_BASES.get(tierIndex));
        TurretBaseBlockEntity base = (TurretBaseBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(pos));
        base.getTrustManager().setOwner(OWNER);
        return base;
    }

    /** What a computer next to {@code pos} would get, through the loader's own lookup. */
    private static IPeripheral lookup(GameTestHelper helper, BlockPos pos, Direction side) {
//? if fabric {
        return PeripheralLookup.get().find(helper.getLevel(), pos, side);
//?}
//? if neoforge {
        /*return helper.getLevel().getCapability(PeripheralCapability.get(), pos, side);
*///?}
    }

    /** The first value of a method's result equals {@code expected} (numbers compared as doubles). */
    private static void assertResult(GameTestHelper helper, MethodResult result, Object expected, String call) {
        Object[] values = result.getResult();
        helper.assertTrue(values != null && values.length > 0, call + " returned nothing");
        Object actual = values[0];
        boolean equal = expected instanceof Number number && actual instanceof Number value
                ? number.doubleValue() == value.doubleValue()
                : expected.equals(actual);
        helper.assertTrue(equal, call + " returned " + Arrays.toString(values) + ", expected " + expected);
    }

    private static ObjectArguments args(Object... values) {
        return new ObjectArguments(values);
    }
}
