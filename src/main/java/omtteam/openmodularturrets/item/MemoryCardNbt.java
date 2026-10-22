package omtteam.openmodularturrets.item;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import omtteam.omlib.api.permission.TrustedPlayersManager;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.machine.MachineMode;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.util.TargetingSettings;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

/**
 * The stored form of {@link MemoryCardData} on a memory card (1.20.1+ {@code MemoryCardNbt}), in the item tag
 * {@code data} like 3.x. Cards written by 3.x (targeting settings, {@code multiTargeting}, {@code mode} and
 * {@code trustedPlayers} directly in {@code data}) are read as cards from a turret base.
 */
public final class MemoryCardNbt {
    private static final String SOURCE = "Source";
    private static final String BASE = "Base";
    private static final String TRUST = "Trust";
    private static final String CAMO = "Camo";

    private MemoryCardNbt() {
    }

    public static NBTTagCompound write(MemoryCardData data) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(SOURCE, data.source().name().toLowerCase(Locale.ROOT));
        MemoryCardData.BaseSettings base = data.base();
        if (base != null) {
            NBTTagCompound baseTag = new NBTTagCompound();
            baseTag.setString("Mode", base.mode().getSerializedName());
            baseTag.setBoolean("MultiTargeting", base.multiTargeting());
            baseTag.setBoolean("TargetPlayers", base.targetPlayers());
            baseTag.setBoolean("TargetMobs", base.targetMobs());
            baseTag.setBoolean("TargetPassive", base.targetPassive());
            baseTag.setInteger("Range", base.range());
            tag.setTag(BASE, baseTag);
        }
        tag.setTag(TRUST, writeTrust(data.trust().trustedPlayers()));
        MemoryCardData.CamoSettings camo = data.camo();
        if (camo != null) {
            NBTTagCompound camoTag = new NBTTagCompound();
            if (camo.disguise() != null) {
                camoTag.setString("Disguise", camo.disguise());
            }
            if (camo.light() != null) {
                camoTag.setInteger("LightValue", camo.light().value());
                camoTag.setInteger("LightOpacity", camo.light().opacity());
            }
            tag.setTag(CAMO, camoTag);
        }
        return tag;
    }

    /** Null if the tag is no valid card data. */
    @Nullable
    public static MemoryCardData read(NBTTagCompound tag) {
        if (!tag.hasKey(SOURCE, Constants.NBT.TAG_STRING)) {
            return readThreeX(tag);
        }
        MemoryCardData.Source source;
        try {
            source = MemoryCardData.Source.valueOf(tag.getString(SOURCE).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
        MemoryCardData.BaseSettings base = null;
        if (tag.hasKey(BASE, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound baseTag = tag.getCompoundTag(BASE);
            base = new MemoryCardData.BaseSettings(
                    MachineMode.bySerializedName(baseTag.getString("Mode"), MachineMode.INVERTED),
                    baseTag.getBoolean("MultiTargeting"), baseTag.getBoolean("TargetPlayers"),
                    baseTag.getBoolean("TargetMobs"), baseTag.getBoolean("TargetPassive"),
                    baseTag.getInteger("Range"));
        }
        MemoryCardData.TrustSettings trust = new MemoryCardData.TrustSettings(readTrust(tag.getCompoundTag(TRUST)));
        MemoryCardData.CamoSettings camo = null;
        if (tag.hasKey(CAMO, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound camoTag = tag.getCompoundTag(CAMO);
            camo = new MemoryCardData.CamoSettings(
                    camoTag.hasKey("Disguise", Constants.NBT.TAG_STRING) ? camoTag.getString("Disguise") : null,
                    camoTag.hasKey("LightValue", Constants.NBT.TAG_ANY_NUMERIC)
                            ? new CamoLight(camoTag.getInteger("LightValue"), camoTag.getInteger("LightOpacity"))
                            : null);
        }
        try {
            return new MemoryCardData(source, base, trust, camo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** A 3.x card: base settings and trust list of a turret base. */
    private static MemoryCardData readThreeX(NBTTagCompound tag) {
        TargetingSettings targeting = TargetingSettings.readFromNBT(tag);
        MachineMode mode = tag.hasKey("mode")
                ? MachineMode.byOrdinal(tag.getInteger("mode"), MachineMode.INVERTED) : MachineMode.INVERTED;
        MemoryCardData.BaseSettings base = new MemoryCardData.BaseSettings(mode, tag.getBoolean("multiTargeting"),
                targeting.isTargetPlayers(), targeting.isTargetMobs(), targeting.isTargetPassive(),
                targeting.getRange());
        return new MemoryCardData(MemoryCardData.Source.TURRET_BASE, base,
                new MemoryCardData.TrustSettings(readTrust(tag.getCompoundTag("trustedPlayers"))), null);
    }

    /** The trust list in the format of {@link TrustedPlayersManager} (which also reads its older formats). */
    private static NBTTagCompound writeTrust(List<TrustedPlayer> players) {
        TrustedPlayersManager manager = new TrustedPlayersManager(null);
        manager.core().load(null, players);
        return manager.writeToNBT(new NBTTagCompound());
    }

    private static List<TrustedPlayer> readTrust(NBTTagCompound tag) {
        TrustedPlayersManager manager = new TrustedPlayersManager(null);
        manager.readFromNBT(tag);
        return manager.core().getTrustedPlayers();
    }
}
