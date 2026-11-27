package omtteam.openmodularturrets.compat.computercraft;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.MinecraftServer;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.BaseComputerTurrets;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands;
import omtteam.openmodularturrets.core.computer.BaseComputerCommands.Command;
import omtteam.openmodularturrets.core.computer.PlayerNames;
import org.jetbrains.annotations.Nullable;

/**
 * The peripheral {@code turret_base} of one turret base for CC: Tweaked (the same on Fabric and NeoForge; only its
 * registration is per loader, see {@code platform/<loader>/computercraft}). The methods have the names, arguments and
 * results of the 1.12.2 peripheral; the OpenComputers component's names ({@code TurretBaseEnvironment}:
 * {@code changeTrustedPlayerAccess}, {@code forceShootAll}) work as aliases. The commands are
 * {@link BaseComputerCommands}, so whether a computer may use the base is up to it
 * ({@code TurretBaseLogic.acceptsComputers()}, checked by every command). Every method runs on the server thread, as it
 * changes or reads the world. The peripheral keeps no state: a computer attached to two sides of one base uses the same
 * commands on the same base.
 */
public final class TurretBasePeripheral implements IPeripheral {
    private final TurretBaseBlockEntity base;
    private final BaseComputerCommands commands;

    /**
     * @param base  the base
     * @param names resolves the names given to {@code addTrustedPlayer}
     */
    public TurretBasePeripheral(TurretBaseBlockEntity base, PlayerNames names) {
        this.base = base;
        this.commands = new BaseComputerCommands(base.getLogic(), new BaseComputerTurrets(base), names);
    }

    /**
     * A peripheral for the base, or null when its level is no server's (a computer only exists on a server).
     */
    @Nullable
    public static TurretBasePeripheral of(TurretBaseBlockEntity base) {
        if (base.getLevel() == null) {
            return null;
        }
        MinecraftServer server = base.getLevel().getServer();
        return server == null ? null : new TurretBasePeripheral(base, new ServerPlayerNames(server));
    }

    @Override
    public String getType() {
        return BaseComputerCommands.TYPE;
    }

    /** Two peripherals of one base are the same peripheral, so a computer does not detach and attach again. */
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other || other instanceof TurretBasePeripheral peripheral && peripheral.base == base;
    }

    @Override
    public Object getTarget() {
        return base;
    }

    private MethodResult call(Command command, IArguments args) throws LuaException {
        return MethodResult.of((Object[]) commands.call(command, args.getAll()));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getOwner(IArguments args) throws LuaException {
        return call(Command.getOwner, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult isAttacksMobs(IArguments args) throws LuaException {
        return call(Command.isAttacksMobs, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setAttacksMobs(IArguments args) throws LuaException {
        return call(Command.setAttacksMobs, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult isAttacksNeutrals(IArguments args) throws LuaException {
        return call(Command.isAttacksNeutrals, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setAttacksNeutrals(IArguments args) throws LuaException {
        return call(Command.setAttacksNeutrals, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult isAttacksPlayers(IArguments args) throws LuaException {
        return call(Command.isAttacksPlayers, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setAttacksPlayers(IArguments args) throws LuaException {
        return call(Command.setAttacksPlayers, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getTrustedPlayers(IArguments args) throws LuaException {
        return call(Command.getTrustedPlayers, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getTrustedPlayer(IArguments args) throws LuaException {
        return call(Command.getTrustedPlayer, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult addTrustedPlayer(IArguments args) throws LuaException {
        return call(Command.addTrustedPlayer, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult removeTrustedPlayer(IArguments args) throws LuaException {
        return call(Command.removeTrustedPlayer, args);
    }

    @LuaFunction(value = {"changeAccessLevel", "changeTrustedPlayerAccess"}, mainThread = true)
    public MethodResult changeAccessLevel(IArguments args) throws LuaException {
        return call(Command.changeAccessLevel, args);
    }

    /** {@code getType()} of the 1.12.2 peripheral (not {@link #getType()}, which CC reads as the peripheral type). */
    @LuaFunction(value = "getType", mainThread = true)
    public MethodResult typeName(IArguments args) throws LuaException {
        return call(Command.getType, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getMaxEnergyStorage(IArguments args) throws LuaException {
        return call(Command.getMaxEnergyStorage, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getCurrentEnergyStorage(IArguments args) throws LuaException {
        return call(Command.getCurrentEnergyStorage, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getActive(IArguments args) throws LuaException {
        return call(Command.getActive, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setMode(IArguments args) throws LuaException {
        return call(Command.setMode, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getMode() {
        return MethodResult.of((Object[]) commands.modeDescription());
    }

    @LuaFunction(mainThread = true)
    public MethodResult getRedstone(IArguments args) throws LuaException {
        return call(Command.getRedstone, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setAllYawPitch(IArguments args) throws LuaException {
        return call(Command.setAllYawPitch, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setTurretYawPitch(IArguments args) throws LuaException {
        return call(Command.setTurretYawPitch, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setAllAutoForceFire(IArguments args) throws LuaException {
        return call(Command.setAllAutoForceFire, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setTurretAutoForceFire(IArguments args) throws LuaException {
        return call(Command.setTurretAutoForceFire, args);
    }

    @LuaFunction(value = {"forceShootAllTurrets", "forceShootAll"}, mainThread = true)
    public MethodResult forceShootAllTurrets(IArguments args) throws LuaException {
        return call(Command.forceShootAllTurrets, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult forceShootTurret(IArguments args) throws LuaException {
        return call(Command.forceShootTurret, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult setComputerControl(IArguments args) throws LuaException {
        return call(Command.setComputerControl, args);
    }

    @LuaFunction(mainThread = true)
    public MethodResult isComputerControl(IArguments args) throws LuaException {
        return call(Command.isComputerControl, args);
    }
}
