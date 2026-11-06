package omtteam.openmodularturrets.compat.opencomputers;

import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import omtteam.omlib.compat.opencomputers.AbstractOMTileEntityEnvironment;
import omtteam.openmodularturrets.compat.computer.BaseComputerCommands;
import omtteam.openmodularturrets.compat.computer.BaseComputerCommands.Command;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

/**
 * Created by nico on 09/06/17.
 * The instance of the component wrapper for a specific turret base. The callbacks keep their 3.x names; the commands
 * are {@link BaseComputerCommands}, shared with ComputerCraft.
 */
public class ManagedEnvironmentTurretBase extends AbstractOMTileEntityEnvironment<TurretBase> implements NamedBlock {
    private final BaseComputerCommands commands;

    ManagedEnvironmentTurretBase(TurretBase base) {
        super(base, BaseComputerCommands.TYPE);
        this.commands = new BaseComputerCommands(base);
    }

    @Override
    public String preferredName() {
        return BaseComputerCommands.TYPE;
    }

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
}
