package omtteam.openmodularturrets.blockentity.base;

import net.minecraft.nbt.CompoundTag;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
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
        boolean hasTargeting = NbtCompat.hasCompound(tag, "Targeting");
        CompoundTag targeting = NbtCompat.getCompound(tag, "Targeting");
        return new TurretBaseLogic.State(
                NbtCompat.getLong(tag, "Energy", 0),
                MachineMode.bySerializedName(NbtCompat.getString(tag, "Mode", ""), defaults.mode()),
                NbtCompat.getBoolean(tag, "Redstone", false),
                hasTargeting ? NbtCompat.getBoolean(targeting, "TargetPlayers", false) : defaults.targetPlayers(),
                hasTargeting ? NbtCompat.getBoolean(targeting, "TargetMobs", false) : defaults.targetMobs(),
                hasTargeting ? NbtCompat.getBoolean(targeting, "TargetPassive", false) : defaults.targetPassive(),
                NbtCompat.getInt(targeting, "Range", 0),
                NbtCompat.getInt(targeting, "MaxRange", 0),
                NbtCompat.getBoolean(tag, "MultiTargeting", false),
                NbtCompat.getInt(tag, "Kills", 0),
                NbtCompat.getInt(tag, "PlayerKills", 0));
    }
}
