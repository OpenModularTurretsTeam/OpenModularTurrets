package omtteam.openmodularturrets.gametest.platform.neoforge;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.gametest.OMTGameTests;
import omtteam.openmodularturrets.platform.neoforge.opencomputers.TurretBaseDriver;
import omtteam.openmodularturrets.platform.neoforge.opencomputers.TurretBaseEnvironment;
import omtteam.openmodularturrets.registry.ModBlocks;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * The OpenComputers: Rebooted component of the turret base (NeoForge 1.21.1 only: OpenComputers is on the NeoForge
 * GameTest server). OpenComputers' own driver lookup (what its Adapter asks) finds the base and names the merged
 * component {@code turret_base}, and the callbacks of the environment work with arguments as OpenComputers passes them
 * (Lua strings as byte arrays, numbers as doubles). The commands themselves are tested in the unit tests of
 * {@code BaseComputerCommands} and in {@code ComputerControlGameTests}.
 */
@GameTestHolder(OMT.MOD_ID)
@PrefixGameTestTemplate(false)
@SuppressWarnings("unused")
public class OpenComputersGameTests implements OMTGameTests {
    private static final BlockPos TIER_FIVE = new BlockPos(1, 1, 1);
    private static final BlockPos TIER_ONE = new BlockPos(4, 1, 1);
    private static final PlayerIdentity OWNER = new PlayerIdentity(UUID.randomUUID(), "OcTestOwner");

    @GameTest(template = EMPTY_STRUCTURE, batch = "opencomputers")
    public void adapterFindsTheTurretBaseComponent(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(TIER_FIVE);
        base(helper, TIER_FIVE, ModBlocks.TURRET_BASES.size() - 1);
        try {
            DriverBlock driver = Driver.driverFor(helper.getLevel(), pos, Direction.UP);
            helper.assertTrue(driver != null, "OpenComputers has no driver for the turret base");
            ManagedEnvironment environment = driver.createEnvironment(helper.getLevel(), pos, Direction.UP);
            helper.assertTrue(environment != null, "The driver made no environment for the turret base");
            helper.assertTrue(environment.node() instanceof Component component && component.name().equals("turret_base"),
                    "The component is not named turret_base: " + environment.node());
        } finally {
            helper.setBlock(TIER_FIVE, Blocks.AIR);
        }
        helper.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE, batch = "opencomputers")
    public void callbacksControlTheBase(GameTestHelper helper) {
        TurretBaseBlockEntity base = base(helper, TIER_FIVE, ModBlocks.TURRET_BASES.size() - 1);
        TurretBaseBlockEntity tierOne = base(helper, TIER_ONE, 0);
        try {
            TurretBaseEnvironment environment = environment(helper, TIER_FIVE);
            assertResult(helper, environment.getOwner(null, args()), OWNER.name(), "getOwner");
            assertResult(helper, environment.setComputerControl(null, args(true)), true, "setComputerControl");
            helper.assertTrue(base.getLogic().isComputerControlled(), "setComputerControl did not reach the base");
            assertResult(helper, environment.isComputerControl(null, args()), true, "isComputerControl");
            assertResult(helper, environment.setAttacksMobs(null, args(false)), true, "setAttacksMobs");
            assertResult(helper, environment.isAttacksMobs(null, args()), false, "isAttacksMobs");
            // names arrive as byte arrays and must be read as strings
            PlayerIdentity friend = new PlayerIdentity(UUID.randomUUID(), "OcTestFriend");
            base.getTrustManager().addTrustedPlayer(new PlayerInfo(OWNER.uuid(), OWNER.name(), false), friend);
            Object[] entry = environment.getTrustedPlayer(null, args(bytes("octestfriend")));
            helper.assertTrue(entry != null && entry.length > 0 && entry[0] instanceof Map<?, ?> map
                    && friend.name().equals(map.get("name")), "getTrustedPlayer returned " + Arrays.toString(entry));
            assertResult(helper, environment.removeTrustedPlayer(null, args(bytes(friend.name()))), true, "removeTrustedPlayer");
            helper.assertTrue(base.getTrustManager().getTrustedPlayer(friend.uuid()).isEmpty(),
                    "removeTrustedPlayer did not reach the trust list");
            // neither online nor in NeoForge's name cache
            assertResult(helper, environment.addTrustedPlayer(null, args(bytes("Never_Joined_OMT"))), "Name not valid!",
                    "addTrustedPlayer of an unknown name");
            assertResult(helper, environment.getMaxEnergyStorage(null, args()), base.getLogic().getEnergy().getCapacity(),
                    "getMaxEnergyStorage");

            TurretBaseEnvironment denied = environment(helper, TIER_ONE);
            assertResult(helper, denied.getOwner(null, args()), "Computer access deactivated!", "getOwner on tier 1");
            assertResult(helper, denied.setComputerControl(null, args(true)), "Computer access deactivated!",
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

    private static TurretBaseEnvironment environment(GameTestHelper helper, BlockPos pos) {
        ManagedEnvironment environment = new TurretBaseDriver().createEnvironment(helper.getLevel(), helper.absolutePos(pos),
                Direction.UP);
        helper.assertTrue(environment instanceof TurretBaseEnvironment, "The driver made no turret base environment at " + pos);
        return (TurretBaseEnvironment) environment;
    }

    /** The first value of a callback's result equals {@code expected} (numbers compared as doubles). */
    private static void assertResult(GameTestHelper helper, Object[] result, Object expected, String call) {
        helper.assertTrue(result != null && result.length > 0, call + " returned nothing");
        Object actual = result[0];
        boolean equal = expected instanceof Number number && actual instanceof Number value
                ? number.doubleValue() == value.doubleValue()
                : expected.equals(actual);
        helper.assertTrue(equal, call + " returned " + Arrays.toString(result) + ", expected " + expected);
    }

    private static byte[] bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static Arguments args(Object... values) {
        return new TestArguments(values);
    }

    /**
     * Arguments as OpenComputers hands them to a callback: Lua strings are byte arrays (also read by
     * {@link #checkString}), numbers are doubles, booleans are booleans.
     */
    private record TestArguments(Object[] values) implements Arguments {

        @Override
        public int count() {
            return values.length;
        }

        @Override
        public Object checkAny(int index) {
            if (index < 0 || index >= values.length) {
                throw new IllegalArgumentException("bad argument #" + (index + 1) + " (value expected)");
            }
            return values[index];
        }

        @Override
        public boolean checkBoolean(int index) {
            if (checkAny(index) instanceof Boolean value) {
                return value;
            }
            throw new IllegalArgumentException("bad argument #" + (index + 1) + " (boolean expected)");
        }

        @Override
        public int checkInteger(int index) {
            return (int) checkDouble(index);
        }

        @Override
        public long checkLong(int index) {
            return (long) checkDouble(index);
        }

        @Override
        public double checkDouble(int index) {
            if (checkAny(index) instanceof Number value) {
                return value.doubleValue();
            }
            throw new IllegalArgumentException("bad argument #" + (index + 1) + " (number expected)");
        }

        @Override
        public String checkString(int index) {
            Object value = checkAny(index);
            if (value instanceof String text) {
                return text;
            }
            if (value instanceof byte[] bytes) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            throw new IllegalArgumentException("bad argument #" + (index + 1) + " (string expected)");
        }

        @Override
        public byte[] checkByteArray(int index) {
            if (checkAny(index) instanceof byte[] bytes) {
                return bytes;
            }
            return checkString(index).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Map checkTable(int index) {
            if (checkAny(index) instanceof Map table) {
                return table;
            }
            throw new IllegalArgumentException("bad argument #" + (index + 1) + " (table expected)");
        }

        @Override
        public ItemStack checkItemStack(int index) {
            throw new UnsupportedOperationException("no item stacks in these tests");
        }

        @Override
        public Object optAny(int index, Object def) {
            return present(index) ? checkAny(index) : def;
        }

        @Override
        public boolean optBoolean(int index, boolean def) {
            return present(index) ? checkBoolean(index) : def;
        }

        @Override
        public int optInteger(int index, int def) {
            return present(index) ? checkInteger(index) : def;
        }

        @Override
        public long optLong(int index, long def) {
            return present(index) ? checkLong(index) : def;
        }

        @Override
        public double optDouble(int index, double def) {
            return present(index) ? checkDouble(index) : def;
        }

        @Override
        public String optString(int index, String def) {
            return present(index) ? checkString(index) : def;
        }

        @Override
        public byte[] optByteArray(int index, byte[] def) {
            return present(index) ? checkByteArray(index) : def;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Map optTable(int index, Map def) {
            return present(index) ? checkTable(index) : def;
        }

        @Override
        public ItemStack optItemStack(int index, ItemStack def) {
            return present(index) ? checkItemStack(index) : def;
        }

        @Override
        public boolean isBoolean(int index) {
            return present(index) && values[index] instanceof Boolean;
        }

        @Override
        public boolean isInteger(int index) {
            return isDouble(index);
        }

        @Override
        public boolean isLong(int index) {
            return isDouble(index);
        }

        @Override
        public boolean isDouble(int index) {
            return present(index) && values[index] instanceof Number;
        }

        @Override
        public boolean isString(int index) {
            return present(index) && (values[index] instanceof String || values[index] instanceof byte[]);
        }

        @Override
        public boolean isByteArray(int index) {
            return isString(index);
        }

        @Override
        public boolean isTable(int index) {
            return present(index) && values[index] instanceof Map;
        }

        @Override
        public boolean isItemStack(int index) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return values.clone();
        }

        @Override
        public Iterator<Object> iterator() {
            return Arrays.asList(values).iterator();
        }

        private boolean present(int index) {
            return index >= 0 && index < values.length && values[index] != null;
        }
    }
}
