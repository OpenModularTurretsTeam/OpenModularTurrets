package omtteam.openmodularturrets.core.computer;

import omtteam.omlib.core.security.AccessLevel;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.TrustManager;
import omtteam.omlib.core.security.TrustResult;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.core.world.Face;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The computer commands of a turret base, ported from 1.12.2 {@code compat.computer.BaseComputerCommands} (shared
 * there by the ComputerCraft peripheral and the OpenComputers component). Arguments are plain Java values (numbers,
 * booleans, strings; each integration converts its own types), a missing or malformed argument gives an error message
 * instead of an exception, and every command first checks {@link TurretBaseLogic#acceptsComputers()}.
 * <p>
 * Trust changes act as the owner (1.12.2 checked no permission either), so an unowned base answers them with
 * {@code "No owner!"}. The {@link TrustManager} saves and syncs them.
 */
public final class BaseComputerCommands {
    /** The peripheral / component type name. */
    public static final String TYPE = "turret_base";
    public static final String NO_ACCESS = "Computer access deactivated!";
    public static final String WRONG_PARAMETERS = "Wrong parameters!";
    /** The answer to a trust change on a base without an owner (new in 1.20.1+: the changes act as the owner). */
    static final String NO_OWNER = "No owner!";
    /** {@code getOwner} of a base without an owner (1.12.2 {@code TileEntityOwnedBlock.getOwner}). */
    static final String NO_OWNER_NAME = "no_owner";
    /** OpenComputers' descriptions of the redstone modes, by ordinal. */
    private static final String[] MODE_DESCRIPTIONS = {"0 - Always on", "1 - always off", "2 - inverted",
            "3 - not inverted"};

    private final TurretBaseLogic logic;
    private final ComputerTurrets turrets;
    private final PlayerNames names;

    /**
     * @param logic   the base
     * @param turrets the turrets mounted on it
     * @param names   resolves the names given to {@code addTrustedPlayer}
     */
    public BaseComputerCommands(TurretBaseLogic logic, ComputerTurrets turrets, PlayerNames names) {
        this.logic = logic;
        this.turrets = turrets;
        this.names = names;
    }

    /**
     * The commands with their ComputerCraft names, in the order of the 3.x peripheral's method list; the two computer
     * control commands are new in 1.20.1+.
     */
    public enum Command {
        getOwner, isAttacksPlayers, setAttacksPlayers, isAttacksMobs, setAttacksMobs, isAttacksNeutrals,
        setAttacksNeutrals, getTrustedPlayers, getTrustedPlayer, addTrustedPlayer, removeTrustedPlayer,
        changeAccessLevel, getActive, getMode, getRedstone, setMode, getType, setAllAutoForceFire,
        setTurretAutoForceFire, forceShootAllTurrets, forceShootTurret, setAllYawPitch, setTurretYawPitch,
        getMaxEnergyStorage, getCurrentEnergyStorage, setComputerControl, isComputerControl
    }

    /** Runs a command by its ComputerCraft name. */
    public Object[] call(Command command, Object[] args) {
        if (!logic.acceptsComputers()) {
            return result(NO_ACCESS);
        }
        return switch (command) {
            case getOwner -> result(ownerName());
            case isAttacksPlayers -> result(logic.getTargetingSettings().isTargetPlayers());
            case setAttacksPlayers -> setTargeting(args, TargetCategory.PLAYERS);
            case isAttacksMobs -> result(logic.getTargetingSettings().isTargetMobs());
            case setAttacksMobs -> setTargeting(args, TargetCategory.MOBS);
            case isAttacksNeutrals -> result(logic.getTargetingSettings().isTargetPassive());
            case setAttacksNeutrals -> setTargeting(args, TargetCategory.NEUTRALS);
            case getTrustedPlayers -> result(trustedPlayersAsListMap());
            case getTrustedPlayer -> getTrustedPlayer(args);
            case addTrustedPlayer -> addTrustedPlayer(args);
            case removeTrustedPlayer -> removeTrustedPlayer(args);
            case changeAccessLevel -> changeAccessLevel(args);
            case getActive -> result(logic.isActive());
            case getMode -> result(logic.getMode().getSerializedName());
            case getRedstone -> result(logic.hasRedstoneSignal());
            case setMode -> setMode(args);
            case getType -> result(TYPE);
            case setAllAutoForceFire -> setAllAutoForceFire(args);
            case setTurretAutoForceFire -> setTurretAutoForceFire(args);
            case forceShootAllTurrets -> result(forceShootAllTurrets());
            case forceShootTurret -> forceShootTurret(args);
            case setAllYawPitch -> setAllYawPitch(args);
            case setTurretYawPitch -> setTurretYawPitch(args);
            case getMaxEnergyStorage -> result(logic.getEnergy().getCapacity());
            case getCurrentEnergyStorage -> result(logic.getEnergy().getAmount());
            case setComputerControl -> setComputerControl(args);
            case isComputerControl -> result(logic.isComputerControlled());
            default -> result("Nonexisting command");
        };
    }

    /** OpenComputers' {@code getMode}: the mode with its description. */
    public Object[] modeDescription() {
        if (!logic.acceptsComputers()) {
            return result(NO_ACCESS);
        }
        return result(MODE_DESCRIPTIONS[logic.getMode().ordinal()]);
    }

    private String ownerName() {
        PlayerIdentity owner = logic.getTrustManager().getOwner();
        return owner == null ? NO_OWNER_NAME : owner.name();
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
            case MOBS -> logic.setTargetMobs(enabled);
            case NEUTRALS -> logic.setTargetPassive(enabled);
            default -> logic.setTargetPlayers(enabled);
        }
        return result(true);
    }

    // ---- trusted players ----

    /** 1.12.2 {@code getTrustedPlayersAsListMap}. */
    private List<Map<String, String>> trustedPlayersAsListMap() {
        List<Map<String, String>> list = new ArrayList<>();
        for (TrustedPlayer trustedPlayer : logic.getTrustManager().getTrustedPlayers()) {
            list.add(asMap(trustedPlayer));
        }
        return list;
    }

    /** 1.12.2 {@code TrustedPlayer.asMap}: {@code name} and {@code accessLevel} as {@code "<ordinal>: <name>"}. */
    static Map<String, String> asMap(TrustedPlayer trustedPlayer) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", trustedPlayer.getName());
        AccessLevel level = trustedPlayer.getAccessLevel();
        map.put("accessLevel", level.ordinal() + ": " + level.getSerializedName());
        return map;
    }

    /** The trust list entry with this name, ignoring case (1.12.2 {@code getTrustedPlayer(String)}). */
    private Optional<TrustedPlayer> findTrustedPlayer(String name) {
        for (TrustedPlayer trustedPlayer : logic.getTrustManager().getTrustedPlayers()) {
            if (trustedPlayer.getName().equalsIgnoreCase(name)) return Optional.of(trustedPlayer);
        }
        return Optional.empty();
    }

    /** The owner as the acting player of a trust change; empty on a base without an owner. */
    private Optional<PlayerInfo> ownerAsActor() {
        PlayerIdentity owner = logic.getTrustManager().getOwner();
        return owner == null ? Optional.empty() : Optional.of(new PlayerInfo(owner.uuid(), owner.name(), false));
    }

    private Object[] getTrustedPlayer(Object[] args) {
        String name = stringArgument(args, 0);
        Optional<TrustedPlayer> trustedPlayer = name == null ? Optional.empty() : findTrustedPlayer(name);
        return trustedPlayer.isEmpty() ? result("Not found!") : result(asMap(trustedPlayer.get()));
    }

    /** {@code addTrustedPlayer(name, [accessLevel])}; without a level the player gets {@link AccessLevel#NONE}. */
    private Object[] addTrustedPlayer(Object[] args) {
        Optional<PlayerInfo> actor = ownerAsActor();
        if (actor.isEmpty()) {
            return result(NO_OWNER);
        }
        String name = stringArgument(args, 0);
        AccessLevel level = null;
        if (args.length > 1 && args[1] != null && !args[1].toString().isEmpty()) {
            level = accessLevelArgument(args, 1);
            if (level == null) {
                return result(WRONG_PARAMETERS);
            }
        }
        if (name == null) {
            return result(WRONG_PARAMETERS);
        }
        Optional<PlayerIdentity> player = names.find(name);
        TrustManager trustManager = logic.getTrustManager();
        if (player.isEmpty() || trustManager.addTrustedPlayer(actor.get(), player.get()) != TrustResult.SUCCESS) {
            return result("Name not valid!");
        }
        if (level == null) {
            return result("successfully added");
        }
        trustManager.setAccessLevel(actor.get(), player.get().uuid(), level);
        return result(true);
    }

    private Object[] removeTrustedPlayer(Object[] args) {
        Optional<PlayerInfo> actor = ownerAsActor();
        if (actor.isEmpty()) {
            return result(NO_OWNER);
        }
        String name = stringArgument(args, 0);
        if (name == null) {
            return result(WRONG_PARAMETERS);
        }
        findTrustedPlayer(name).ifPresent(trustedPlayer
                -> logic.getTrustManager().removeTrustedPlayer(actor.get(), trustedPlayer.getUuid()));
        return result(true);
    }

    private Object[] changeAccessLevel(Object[] args) {
        Optional<PlayerInfo> actor = ownerAsActor();
        if (actor.isEmpty()) {
            return result(NO_OWNER);
        }
        String name = stringArgument(args, 0);
        Optional<TrustedPlayer> trustedPlayer = name == null ? Optional.empty() : findTrustedPlayer(name);
        if (trustedPlayer.isEmpty()) {
            return result("Not found!");
        }
        AccessLevel level = accessLevelArgument(args, 1);
        if (level == null) {
            return result("Invalid Access Level!");
        }
        TrustResult changed = logic.getTrustManager().setAccessLevel(actor.get(), trustedPlayer.get().getUuid(), level);
        return result(changed == TrustResult.SUCCESS);
    }

    // ---- base ----

    private Object[] setMode(Object[] args) {
        Integer mode = intArgument(args, 0);
        if (mode == null || mode < 0 || mode >= MachineMode.values().length) {
            return result("Wrong parameters! expects number between 0 and " + (MachineMode.values().length - 1),
                    "0 - Always on, 1 - always off, 2 - inverted, 3 - not inverted");
        }
        logic.setMode(MachineMode.values()[mode]);
        return result(true);
    }

    private Object[] setComputerControl(Object[] args) {
        Boolean controlled = booleanArgument(args, 0);
        if (controlled == null) {
            return result(WRONG_PARAMETERS);
        }
        logic.setComputerControlled(controlled);
        return result(true);
    }

    // ---- turrets ----

    private Object[] setAllAutoForceFire(Object[] args) {
        Boolean state = booleanArgument(args, 0);
        if (state == null) {
            return result(WRONG_PARAMETERS);
        }
        for (Face face : turrets.faces()) {
            turrets.setAutoFire(face, state);
        }
        return result(true);
    }

    private Object[] setTurretAutoForceFire(Object[] args) {
        Face face = faceArgument(args, 0);
        Boolean state = booleanArgument(args, 1);
        if (face == null || state == null) {
            return result(WRONG_PARAMETERS);
        }
        return result(turrets.setAutoFire(face, state));
    }

    /** @return how many turrets fired */
    private int forceShootAllTurrets() {
        int shots = 0;
        for (Face face : turrets.faces()) {
            if (turrets.forceShot(face)) shots++;
        }
        return shots;
    }

    private Object[] forceShootTurret(Object[] args) {
        Face face = faceArgument(args, 0);
        return face == null ? result(WRONG_PARAMETERS) : result(turrets.forceShot(face));
    }

    private Object[] setAllYawPitch(Object[] args) {
        Float yaw = floatArgument(args, 0);
        Float pitch = floatArgument(args, 1);
        if (yaw == null || pitch == null) {
            return result(WRONG_PARAMETERS);
        }
        for (Face face : turrets.faces()) {
            turrets.setYawPitch(face, yaw, pitch);
        }
        return result("set yaw and pitch successfully");
    }

    private Object[] setTurretYawPitch(Object[] args) {
        Face face = faceArgument(args, 0);
        Float yaw = floatArgument(args, 1);
        Float pitch = floatArgument(args, 2);
        if (face == null || yaw == null || pitch == null) {
            return result(WRONG_PARAMETERS);
        }
        return result(turrets.setYawPitch(face, yaw, pitch));
    }

    // ---- arguments (each helper returns null for a missing or malformed argument) ----

    private static Object[] result(Object... values) {
        return values;
    }

    /** The argument, or null if there are fewer arguments. */
    private static Object argument(Object[] args, int index) {
        return index < args.length ? args[index] : null;
    }

    /** A non-empty string (other values as their text), or null. */
    static String stringArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        return argument == null || argument.toString().isEmpty() ? null : argument.toString();
    }

    /** A boolean, or the strings {@code "true"} / {@code "false"}; otherwise null. */
    static Boolean booleanArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        if (argument instanceof Boolean value) {
            return value;
        }
        if (argument != null && (argument.toString().equals("true") || argument.toString().equals("false"))) {
            return Boolean.parseBoolean(argument.toString());
        }
        return null;
    }

    /** A whole number (Lua numbers arrive as {@code Double}, strings are parsed), or null. */
    static Integer intArgument(Object[] args, int index) {
        Double value = doubleArgument(args, index);
        return value != null && value == Math.rint(value) && Math.abs(value) <= Integer.MAX_VALUE
                ? (Integer) value.intValue() : null;
    }

    /** A number that is finite as a {@code float} (1e39 is not), or null. */
    static Float floatArgument(Object[] args, int index) {
        Double value = doubleArgument(args, index);
        if (value == null) return null;
        float narrowed = value.floatValue();
        return Float.isFinite(narrowed) ? (Float) narrowed : null;
    }

    private static Double doubleArgument(Object[] args, int index) {
        Object argument = argument(args, index);
        if (argument instanceof Number number) {
            return number.doubleValue();
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

    /** A face index 0-5 (1.12.2 {@code EnumFacing.getIndex()}: down, up, north, south, west, east), or null. */
    static Face faceArgument(Object[] args, int index) {
        Integer face = intArgument(args, index);
        return face != null && face >= 0 && face < Face.values().length ? Face.values()[face] : null;
    }

    /** An access level 0-3 (none, open GUI, change settings, admin), or null. */
    static AccessLevel accessLevelArgument(Object[] args, int index) {
        Integer level = intArgument(args, index);
        return level != null && level >= 0 && level < AccessLevel.values().length
                ? AccessLevel.values()[level] : null;
    }
}
