package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
        boolean hasTargeting = tag.contains("Targeting", Tag.TAG_COMPOUND);
        CompoundTag targeting = tag.getCompound("Targeting");
        return new TurretBaseLogic.State(
                tag.getLong("Energy"),
                MachineMode.bySerializedName(tag.getString("Mode"), defaults.mode()),
                tag.getBoolean("Redstone"),
                hasTargeting ? targeting.getBoolean("TargetPlayers") : defaults.targetPlayers(),
                hasTargeting ? targeting.getBoolean("TargetMobs") : defaults.targetMobs(),
                hasTargeting ? targeting.getBoolean("TargetPassive") : defaults.targetPassive(),
                targeting.getInt("Range"),
                targeting.getInt("MaxRange"),
                tag.getBoolean("MultiTargeting"),
                tag.getInt("Kills"),
                tag.getInt("PlayerKills"));
    }
}
