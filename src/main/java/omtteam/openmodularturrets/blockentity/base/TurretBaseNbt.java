package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.nbt.CompoundTag;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;

/**
 * Saves and loads {@link TurretBaseLogic.State}.
 */
public final class TurretBaseNbt {
    private TurretBaseNbt() {
    }

    public static void save(TurretBaseLogic.State state, CompoundTag tag) {
        tag.putLong("Energy", state.energy());
        tag.putString("Mode", state.mode().getSerializedName());
        tag.putBoolean("Redstone", state.redstone());
        CompoundTag targeting = new CompoundTag();
        targeting.putBoolean("TargetPlayers", state.targetPlayers());
        targeting.putBoolean("TargetMobs", state.targetMobs());
        targeting.putBoolean("TargetPassive", state.targetPassive());
        targeting.putInt("Range", state.range());
        targeting.putInt("MaxRange", state.maxRange());
        tag.put("Targeting", targeting);
        tag.putBoolean("MultiTargeting", state.multiTargeting());
        tag.putInt("Kills", state.kills());
        tag.putInt("PlayerKills", state.playerKills());
    }

    public static TurretBaseLogic.State load(CompoundTag tag) {
        TurretBaseLogic.State defaults = TurretBaseLogic.State.defaults();
        boolean hasTargeting = tag.getCompound("Targeting").isPresent();
        CompoundTag targeting = tag.getCompoundOrEmpty("Targeting");
        return new TurretBaseLogic.State(
                tag.getLongOr("Energy", 0),
                MachineMode.bySerializedName(tag.getStringOr("Mode", ""), defaults.mode()),
                tag.getBooleanOr("Redstone", false),
                hasTargeting ? targeting.getBooleanOr("TargetPlayers", false) : defaults.targetPlayers(),
                hasTargeting ? targeting.getBooleanOr("TargetMobs", false) : defaults.targetMobs(),
                hasTargeting ? targeting.getBooleanOr("TargetPassive", false) : defaults.targetPassive(),
                targeting.getIntOr("Range", 0),
                targeting.getIntOr("MaxRange", 0),
                tag.getBooleanOr("MultiTargeting", false),
                tag.getIntOr("Kills", 0),
                tag.getIntOr("PlayerKills", 0));
    }
}
