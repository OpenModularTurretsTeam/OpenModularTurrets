package omtteam.openmodularturrets.turret;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

/**
 * Created by Keridos on 17/08/17.
 * This Class is the class used for declaring targeting settings to the targeting system for turrets.
 */
public class TargetingSettings {
    private boolean targetPlayers;
    private boolean targetMobs;
    private boolean targetPassive;
    private int range;
    private int maxRange;

    private TargetingSettings() {
    }

    public TargetingSettings(boolean targetPlayers, boolean targetMobs, boolean targetPassive, int range,
                             int maxRange) {
        this.targetPlayers = targetPlayers;
        this.targetMobs = targetMobs;
        this.targetPassive = targetPassive;
        this.range = range;
        this.maxRange = maxRange;
    }

    public static TargetingSettings readFromNBT(NBTTagCompound nbtTagCompound) {
        TargetingSettings settings = new TargetingSettings();
        if (nbtTagCompound.hasKey("targetingSettings")) {
            NBTTagCompound nbt = nbtTagCompound.getCompoundTag("targetingSettings");
            settings.targetPlayers = nbt.getBoolean("targetPlayers");
            settings.targetMobs = nbt.getBoolean("targetMobs");
            settings.targetPassive = nbt.getBoolean("targetPassive");
            settings.range = nbt.getInteger("range");
            settings.maxRange = nbt.getInteger("maxRange");
        } else {
            settings.targetPlayers = nbtTagCompound.getBoolean("attacksPlayers");
            settings.targetMobs = nbtTagCompound.getBoolean("attacksMobs");
            settings.targetPassive = nbtTagCompound.getBoolean("attacksNeutrals");
            settings.range = nbtTagCompound.getInteger("currentMaxRange");
            settings.maxRange = nbtTagCompound.getInteger("upperBoundMaxRange");
        }
        return settings;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("targetPlayers", this.targetPlayers);
        nbt.setBoolean("targetMobs", this.targetMobs);
        nbt.setBoolean("targetPassive", this.targetPassive);
        nbt.setInteger("range", this.range);
        nbt.setInteger("maxRange", this.maxRange);
        nbtTagCompound.setTag("targetingSettings", nbt);
        return nbtTagCompound;
    }

    public boolean isTargetPlayers() {
        return targetPlayers;
    }

    public TargetingSettings setTargetPlayers(boolean targetPlayers) {
        this.targetPlayers = targetPlayers;
        return this;
    }

    public boolean isTargetMobs() {
        return targetMobs;
    }

    public TargetingSettings setTargetMobs(boolean targetMobs) {
        this.targetMobs = targetMobs;
        return this;
    }

    public boolean isTargetPassive() {
        return targetPassive;
    }

    public TargetingSettings setTargetPassive(boolean targetPassive) {
        this.targetPassive = targetPassive;
        return this;
    }

    public int getRange() {
        return range;
    }

    public TargetingSettings setRange(int range) {
        if (range <= maxRange && range > -1) {
            this.range = range;
        }
        return this;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public TargetingSettings setMaxRange(int maxRange) {
        this.maxRange = maxRange;
        if (this.maxRange < this.range) {
            this.range = this.maxRange;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetingSettings that = (TargetingSettings) o;
        return targetPlayers == that.targetPlayers &&
                targetMobs == that.targetMobs &&
                targetPassive == that.targetPassive &&
                range == that.range &&
                this.maxRange == that.range;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetPlayers, targetMobs, targetPassive, range, maxRange);
    }
}
