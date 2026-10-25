package omtteam.openmodularturrets.compat.computercraft;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import omtteam.openmodularturrets.compat.BaseComputerCommands;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * The ComputerCraft peripheral of a turret base (until 4.0.0 the base itself was the peripheral); the commands are
 * {@link BaseComputerCommands}, with the 3.x method names and order.
 */
public class TurretBasePeripheral implements IPeripheral {
    private static final BaseComputerCommands.Command[] COMMANDS = BaseComputerCommands.Command.values();

    private final TurretBase base;
    private final BaseComputerCommands commands;

    TurretBasePeripheral(TurretBase base) {
        this.base = base;
        this.commands = new BaseComputerCommands(base);
    }

    @Nonnull
    @Override
    public String getType() {
        return BaseComputerCommands.TYPE;
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        String[] names = new String[COMMANDS.length];
        for (int i = 0; i < COMMANDS.length; i++) {
            names[i] = COMMANDS[i].name();
        }
        return names;
    }

    @Override
    @ParametersAreNonnullByDefault
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
        if (method < 0 || method >= COMMANDS.length) {
            return new Object[]{"Nonexisting command"};
        }
        return commands.call(COMMANDS[method], arguments);
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
    }

    /** Two peripherals are the same if they wrap the same base. */
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof TurretBasePeripheral && ((TurretBasePeripheral) other).base == base;
    }
}
