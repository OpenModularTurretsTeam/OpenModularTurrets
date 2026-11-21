package omtteam.openmodularturrets.platform.neoforge.opencomputers;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.BaseComputerTurrets;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands.Command;
import omtteam.openmodularturrets.core.computer.PlayerNames;

/**
 * The component {@code turret_base} of one turret base (1.12: {@code ManagedEnvironmentTurretBase}). The callbacks keep
 * their 1.12 names and docs (plus {@code setComputerControl} / {@code isComputerControl}); the commands are
 * {@link BaseComputerCommands}. The callbacks are not direct, so OpenComputers runs them on the server thread. The
 * environment keeps no state of its own: the node's data is saved by {@link AbstractManagedEnvironment}.
 */
public final class TurretBaseEnvironment extends AbstractManagedEnvironment implements NamedBlock {
    private final BaseComputerCommands commands;

    TurretBaseEnvironment(TurretBaseBlockEntity base, PlayerNames names) {
        setNode(Network.newNode(this, Visibility.Network)
                .withComponent(BaseComputerCommands.TYPE, Visibility.Network)
                .create());
        this.commands = new BaseComputerCommands(base.getLogic(), new BaseComputerTurrets(base), names);
    }

    @Override
    public String preferredName() {
        return BaseComputerCommands.TYPE;
    }

    /** Above OpenComputers' own drivers that also match a base (its energy driver, priority 0), so the name is ours. */
    @Override
    public int priority() {
        return 10;
    }

    /** The arguments as plain Java values (OpenComputers passes strings as byte arrays). */
    private static Object[] plain(Arguments args) {
        Object[] values = new Object[args.count()];
        for (int i = 0; i < values.length; i++) {
            values[i] = args.isString(i) ? args.checkString(i) : args.checkAny(i);
        }
        return values;
    }

    private Object[] call(Command command, Arguments args) {
        return commands.call(command, plain(args));
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():string; returns owner of turret base.")
    public Object[] getOwner(Context context, Arguments args) {
        return call(Command.getOwner, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack hostile mobs.")
    public Object[] isAttacksMobs(Context context, Arguments args) {
        return call(Command.isAttacksMobs, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:boolean):void; set attack hostile mobs or not.")
    public Object[] setAttacksMobs(Context context, Arguments args) {
        return call(Command.setAttacksMobs, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack neutral mobs.")
    public Object[] isAttacksNeutrals(Context context, Arguments args) {
        return call(Command.isAttacksNeutrals, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:boolean):void; set attack neutral mobs or not.")
    public Object[] setAttacksNeutrals(Context context, Arguments args) {
        return call(Command.setAttacksNeutrals, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack players.")
    public Object[] isAttacksPlayers(Context context, Arguments args) {
        return call(Command.isAttacksPlayers, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:boolean):void; sets attack players or not.")
    public Object[] setAttacksPlayers(Context context, Arguments args) {
        return call(Command.setAttacksPlayers, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():table; returns a table of all trusted players on this base.")
    public Object[] getTrustedPlayers(Context context, Arguments args) {
        return call(Command.getTrustedPlayers, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():table; tries to return a specific trusted player from this base")
    public Object[] getTrustedPlayer(Context context, Arguments args) {
        return call(Command.getTrustedPlayer, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(name:String, [accessLevel:Integer]):boolean;" +
            " adds trusted player to trustlist. Can return error.")
    public Object[] addTrustedPlayer(Context context, Arguments args) {
        return call(Command.addTrustedPlayer, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(name:String):string; removes trusted player from trust list.")
    public Object[] removeTrustedPlayer(Context context, Arguments args) {
        return call(Command.removeTrustedPlayer, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(name:String, accessLevel:Integer):string; change players access level. Can return error.")
    public Object[] changeTrustedPlayerAccess(Context context, Arguments args) {
        return call(Command.changeAccessLevel, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():int; returns maximum energy storage.")
    public Object[] getMaxEnergyStorage(Context context, Arguments args) {
        return call(Command.getMaxEnergyStorage, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():int; returns current energy stored.")
    public Object[] getCurrentEnergyStorage(Context context, Arguments args) {
        return call(Command.getCurrentEnergyStorage, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; returns if the turret is currently active.")
    public Object[] getActive(Context context, Arguments args) {
        return call(Command.getActive, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:int):void; toggles turret redstone inversion state.")
    public Object[] setMode(Context context, Arguments args) {
        return call(Command.setMode, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():int; shows base mode.")
    public Object[] getMode(Context context, Arguments args) {
        return commands.modeDescription();
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; shows redstone state.")
    public Object[] getRedstone(Context context, Arguments args) {
        return call(Command.getRedstone, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(yaw:double, pitch:double):void;" +
            " Set yaw and pitch for all turrets (deact. auto targ. before).")
    public Object[] setAllYawPitch(Context context, Arguments args) {
        return call(Command.setAllYawPitch, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(side:int, yaw:double, pitch:double):boolean;" +
            " Set yaw and pitch for a turret (deact. auto targ. before).")
    public Object[] setTurretYawPitch(Context context, Arguments args) {
        return call(Command.setTurretYawPitch, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:boolean):void; Enable auto firing for all Turrets (deact. auto targ. before).")
    public Object[] setAllAutoForceFire(Context context, Arguments args) {
        return call(Command.setAllAutoForceFire, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(side:int, state:boolean):boolean;" +
            " Enable auto firing for specified Turret (deact. auto targ. before).")
    public Object[] setTurretAutoForceFire(Context context, Arguments args) {
        return call(Command.setTurretAutoForceFire, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():int; Try to shoot all turrets, returns number of successful shots")
    public Object[] forceShootAll(Context context, Arguments args) {
        return call(Command.forceShootAllTurrets, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(side:int):boolean; Try to shoot specified turret, returns true if successful")
    public Object[] forceShootTurret(Context context, Arguments args) {
        return call(Command.forceShootTurret, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function(state:boolean):boolean;" +
            " a computer controls the turrets: they no longer target on their own.")
    public Object[] setComputerControl(Context context, Arguments args) {
        return call(Command.setComputerControl, args);
    }

    @SuppressWarnings("unused")
    @Callback(doc = "function():boolean; returns if a computer controls the turrets.")
    public Object[] isComputerControl(Context context, Arguments args) {
        return call(Command.isComputerControl, args);
    }
}
