package omtteam.openmodularturrets.tileentity.base;

import net.minecraft.nbt.NBTTagCompound;
import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.util.TargetingSettings;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;

/**
 * Saves and loads the {@link TurretBaseLogic.State} of a turret base in the 3.x keys (1.20.1+ {@code TurretBaseNbt}).
 * 3.x never saved the player kills (and loaded the missing key into the kills); they are saved as {@code playerKills}.
 */
final class TurretBaseNbt {
    private TurretBaseNbt() {
    }

    static void save(TurretBaseLogic logic, NBTTagCompound tag) {
        TurretBaseLogic.State state = logic.getState();
        tag.setBoolean("active", logic.isActive());
        tag.setBoolean("redstone", state.redstone());
        tag.setBoolean("shouldConcealTurrets", logic.isConcealingTurrets());
        tag.setBoolean("multiTargeting", state.multiTargeting());
        tag.setInteger("mode", state.mode().ordinal());
        tag.setInteger("kills", state.kills());
        tag.setInteger("playerKills", state.playerKills());
        TargetingSettings.of(logic.getTargetingSettings()).writeToNBT(tag);
    }

    /** The state saved in {@code tag}, with the energy the energy keys were already read into. */
    static TurretBaseLogic.State load(NBTTagCompound tag, long energy) {
        TargetingSettings targeting = TargetingSettings.readFromNBT(tag);
        MachineMode mode = tag.hasKey("mode")
                ? MachineMode.byOrdinal(tag.getInteger("mode"), MachineMode.INVERTED) : MachineMode.INVERTED;
        return new TurretBaseLogic.State(energy, mode, tag.getBoolean("redstone"),
                targeting.isTargetPlayers(), targeting.isTargetMobs(), targeting.isTargetPassive(),
                targeting.getRange(), targeting.getMaxRange(), tag.getBoolean("multiTargeting"),
                tag.getInteger("kills"), tag.getInteger("playerKills"));
    }
}
