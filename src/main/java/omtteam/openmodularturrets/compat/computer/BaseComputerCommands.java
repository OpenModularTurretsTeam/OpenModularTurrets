package omtteam.openmodularturrets.compat.computer;

import net.minecraft.util.EnumFacing;
import omtteam.omlib.api.permission.EnumAccessLevel;
import omtteam.omlib.api.permission.TrustedPlayer;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;

/**
 * The computer commands of a turret base, shared by the ComputerCraft peripheral and the OpenComputers component.
 * Arguments are plain Java values (numbers, booleans, strings; each integration converts its own types), a missing or
 * malformed argument gives an error message instead of an exception, and every command first checks
 * {@link TurretBase#isComputerAccessible()}.
 */
public final class BaseComputerCommands {
    /** The peripheral / component type name. */
    public static final String TYPE = "turret_base";
    public static final String NO_ACCESS = "Computer access deactivated!";
    public static final String WRONG_PARAMETERS = "Wrong parameters!";
    /** OpenComputers' descriptions of the redstone modes, by ordinal. */
    private static final String[] MODE_DESCRIPTIONS = {"0 - Always on", "1 - always off", "2 - inverted",
            "3 - not inverted"};

    private final TurretBase base;

    public BaseComputerCommands(TurretBase base) {
        this.base = base;
    }

    /** The commands with their ComputerCraft names, in the order of the 3.x peripheral's method list. */
    public enum Command {
        getOwner, isAttacksPlayers, setAttacksPlayers, isAttacksMobs, setAttacksMobs, isAttacksNeutrals,
        setAttacksNeutrals, getTrustedPlayers, getTrustedPlayer, addTrustedPlayer, removeTrustedPlayer,
        changeAccessLevel, getActive, getMode, getRedstone, setMode, getType, setAllAutoForceFire,
        setTurretAutoForceFire, forceShootAllTurrets, forceShootTurret, setAllYawPitch, setTurretYawPitch,
        getMaxEnergyStorage, getCurrentEnergyStorage
    }

    /** Runs a command by its ComputerCraft name. */
    public Object[] call(Command command, Object[] args) {
        if (!base.isComputerAccessible()) {
            return result(NO_ACCESS);
        }
        switch (command) {
            case getOwner:
                return result(base.getOwner().getName());
            case isAttacksPlayers:
                return result(base.isAttacksPlayers());
            case setAttacksPlayers:
                return setTargeting(args, TargetCategory.PLAYERS);
            case isAttacksMobs:
                return result(base.isAttacksMobs());
            case setAttacksMobs:
                return setTargeting(args, TargetCategory.MOBS);
            case isAttacksNeutrals:
                return result(base.isAttacksNeutrals());
            case setAttacksNeutrals:
                return setTargeting(args, TargetCategory.NEUTRALS);
            case getTrustedPlayers:
                return result(base.getTrustManager().getTrustedPlayersAsListMap());
            case getTrustedPlayer:
                return getTrustedPlayer(args);
            case addTrustedPlayer:
                return addTrustedPlayer(args);
            case removeTrustedPlayer:
                return removeTrustedPlayer(args);
            case changeAccessLevel:
                return changeAccessLevel(args);
            case getActive:
                return result(base.isActive());
            case getMode:
                return result(base.getMode().getName());
            case getRedstone:
                return result(base.getRedstone());
            case setMode:
                return setMode(args);
            case getType:
                return result(TYPE);
            case setAllAutoForceFire:
                return setAllAutoForceFire(args);
            case setTurretAutoForceFire:
                return setTurretAutoForceFire(args);
            case forceShootAllTurrets:
                return result(base.forceShootAllTurrets());
            case forceShootTurret:
                return forceShootTurret(args);
            case setAllYawPitch:
                return setAllYawPitch(args);
            case setTurretYawPitch:
                return setTurretYawPitch(args);
            case getMaxEnergyStorage:
                return result(base.getMaxEnergyStored(EnumFacing.DOWN));
            case getCurrentEnergyStorage:
                return result(base.getEnergyStored(EnumFacing.DOWN));
            default:
                return result("Nonexisting command");
        }
    }

    /** OpenComputers' {@code getMode}: the mode with its description. */
    public Object[] modeDescription() {
        if (!base.isComputerAccessible()) {
            return result(NO_ACCESS);
        }
        return result(MODE_DESCRIPTIONS[base.getMode().ordinal()]);
    }

    private enum TargetCategory {
        MOBS, NEUTRALS, PLAYERS
    }

    private Object[] setTargeting(Object[] args, TargetCategory category) {
        Boolean enabled = booleanArgument(args, 0);
        if (enabled == null) {
            return result(WRONG_PARAMETERS);
        }
        switch (category) {
            case MOBS:
                base.setAttacksMobs(enabled);
                break;
            case NEUTRALS:
                base.setAttacksNeutrals(enabled);
                break;
            default:
                base.setAttacksPlayers(enabled);
        }
        return result(true);
    }

    private Object[] getTrustedPlayer(Object[] args) {
        String name = stringArgument(args, 0);
        TrustedPlayer trustedPlayer = name == null ? null : base.getTrustManager().getTrustedPlayer(name);
        return trustedPlayer == null ? result("Not found!") : result(trustedPlayer.asMap());
    }

    /** {@code addTrustedPlayer(name, [accessLevel])}; without a level the player gets the profile's default. */
    private Object[] addTrustedPlayer(Object[] args) {
        String name = stringArgument(args, 0);
        EnumAccessLevel level = null;
        if (args.length > 1 && args[1] != null && !args[1].toString().isEmpty()) {
            level = accessLevelArgument(args, 1);
            if (level == null) {
                return result(WRONG_PARAMETERS);
            }
        }
        if (name == null) {
            return result(WRONG_PARAMETERS);
        }
        if (!base.getTrustManager().addTrustedPlayer(name)) {
            return result("Name not valid!");
        }
        if (level == null) {
            return result("successfully added");
        }
        base.getTrustManager().changePermission(name, level);
        return result(true);
    }

    private Object[] removeTrustedPlayer(Object[] args) {
        String name = stringArgument(args, 0);
        if (name == null) {
            return result(WRONG_PARAMETERS);
        }
        base.getTrustManager().removeTrustedPlayer(name);
        return result(true);
    }

    private Object[] changeAccessLevel(Object[] args) {
        String name = stringArgument(args, 0);
        if (name == null || base.getTrustManager().getTrustedPlayer(name) == null) {
            return result("Not found!");
        }
        EnumAccessLevel level = accessLevelArgument(args, 1);
        if (level == null) {
            return result("Invalid Access Level!");
        }
        return result(base.getTrustManager().changePermission(name, level));
    }

    private Object[] setMode(Object[] args) {
        Integer mode = intArgument(args, 0);
        if (mode == null || mode < 0 || mode >= EnumMachineMode.values().length) {
            return result("Wrong parameters! expects number between 0 and " + (EnumMachineMode.values().length - 1),
                    "0 - Always on, 1 - always off, 2 - inverted, 3 - not inverted");
        }
        base.setMode(EnumMachineMode.values()[mode]);
        return result(true);
    }

    private Object[] setAllAutoForceFire(Object[] args) {
        Boolean state = booleanArgument(args, 0);
        if (state == null) {
            return result(WRONG_PARAMETERS);
        }
        base.setAllTurretsForceFire(state);
        return result(true);
    }

    private Object[] setTurretAutoForceFire(Object[] args) {
        EnumFacing face = faceArgument(args, 0);
        Boolean state = booleanArgument(args, 1);
        if (face == null || state == null) {
            return result(WRONG_PARAMETERS);
        }
        return result(base.setTurretForceFire(face, state));
    }

    private Object[] forceShootTurret(Object[] args) {
        EnumFacing face = faceArgument(args, 0);
        return face == null ? result(WRONG_PARAMETERS) : result(base.forceShootTurret(face));
    }

    private Object[] setAllYawPitch(Object[] args) {
        Float yaw = floatArgument(args, 0);
        Float pitch = floatArgument(args, 1);
        if (yaw == null || pitch == null) {
            return result(WRONG_PARAMETERS);
        }
        base.setAllTurretsYawPitch(yaw, pitch);
        return result("set yaw and pitch successfully");
    }

    private Object[] setTurretYawPitch(Object[] args) {
        EnumFacing face = faceArgument(args, 0);
        Float yaw = floatArgument(args, 1);
        Float pitch = floatArgument(args, 2);
        if (face == null || yaw == null || pitch == null) {
            return result(WRONG_PARAMETERS);
        }
        return result(base.setTurretYawPitch(face, yaw, pitch));
    }

    // ---- arguments ----

    private static Object[] result(Object... values) {
        return values;
    }

    @Nullable
    private static Object argument(Object[] args, int index) {
        return index < args.length ? args[index] : null;
    }

    @Nullable
    static String stringArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        return argument == null || argument.toString().isEmpty() ? null : argument.toString();
    }

    /** A boolean, or the strings {@code "true"} / {@code "false"}; otherwise null. */
    @Nullable
    static Boolean booleanArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        if (argument instanceof Boolean) {
            return (Boolean) argument;
        }
        if (argument != null && (argument.toString().equals("true") || argument.toString().equals("false"))) {
            return Boolean.parseBoolean(argument.toString());
        }
        return null;
    }

    /** A whole number (Lua numbers arrive as {@code Double}, strings are parsed), or null. */
    @Nullable
    static Integer intArgument(Object[] args, int index) {
        Double value = doubleArgument(args, index);
        return value != null && value == Math.rint(value) && Math.abs(value) <= Integer.MAX_VALUE
                ? (Integer) value.intValue() : null;
    }

    @Nullable
    static Float floatArgument(Object[] args, int index) {
        Double value = doubleArgument(args, index);
        return value == null || value.isNaN() || value.isInfinite() ? null : (Float) value.floatValue();
    }

    @Nullable
    private static Double doubleArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        if (argument instanceof Number) {
            return ((Number) argument).doubleValue();
        }
        if (argument == null) {
            return null;
        }
        try {
            return Double.parseDouble(argument.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** A face index 0-5 ({@link EnumFacing#getIndex()}), or null. */
    @Nullable
    static EnumFacing faceArgument(Object[] args, int index) {
        Integer face = intArgument(args, index);
        return face != null && face >= 0 && face < EnumFacing.VALUES.length ? EnumFacing.byIndex(face) : null;
    }

    @Nullable
    static EnumAccessLevel accessLevelArgument(Object[] args, int index) {
        Integer level = intArgument(args, index);
        return level != null && level >= 0 && level < EnumAccessLevel.values().length
                ? EnumAccessLevel.values()[level] : null;
    }
}
