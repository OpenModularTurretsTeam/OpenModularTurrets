package omtteam.openmodularturrets.item;

import net.minecraft.nbt.CompoundTag;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.omlib.core.camo.CamoLight;
import omtteam.openmodularturrets.core.card.MemoryCardData;
import omtteam.openmodularturrets.security.SecurityNbt;

import java.util.Locale;
import java.util.Optional;

/**
 * The stored form of {@link MemoryCardData} on a memory card.
 */
public final class MemoryCardNbt {
    private static final String SOURCE = "Source";
    private static final String BASE = "Base";
    private static final String TRUST = "Trust";
    private static final String CAMO = "Camo";

    private MemoryCardNbt() {
    }

    @SuppressWarnings("ExtractMethodRecommender")
    public static CompoundTag write(MemoryCardData data) {
        CompoundTag tag = new CompoundTag();
        tag.putString(SOURCE, data.source().name().toLowerCase(Locale.ROOT));
        if (data.base() != null) {
            MemoryCardData.BaseSettings base = data.base();
            CompoundTag baseTag = new CompoundTag();
            baseTag.putString("Mode", base.mode().getSerializedName());
            baseTag.putBoolean("MultiTargeting", base.multiTargeting());
            baseTag.putBoolean("TargetPlayers", base.targetPlayers());
            baseTag.putBoolean("TargetMobs", base.targetMobs());
            baseTag.putBoolean("TargetPassive", base.targetPassive());
            baseTag.putInt("Range", base.range());
            tag.put(BASE, baseTag);
        }
        tag.put(TRUST, SecurityNbt.saveTrustedPlayers(data.trust().trustedPlayers()));
        if (data.camo() != null) {
            MemoryCardData.CamoSettings camo = data.camo();
            CompoundTag camoTag = new CompoundTag();
            if (camo.disguise() != null) camoTag.putString("Disguise", camo.disguise());
            if (camo.light() != null) {
                camoTag.putInt("LightValue", camo.light().value());
                camoTag.putInt("LightOpacity", camo.light().opacity());
            }
            tag.put(CAMO, camoTag);
        }
        return tag;
    }

    /** Empty if the tag is not valid card data. */
    public static Optional<MemoryCardData> read(CompoundTag tag) {
        MemoryCardData.Source source;
        try {
            source = MemoryCardData.Source.valueOf(NbtCompat.getString(tag, SOURCE, "").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        MemoryCardData.BaseSettings base = null;
        if (NbtCompat.hasCompound(tag, BASE)) {
            CompoundTag baseTag = NbtCompat.getCompound(tag, BASE);
            base = new MemoryCardData.BaseSettings(
                    MachineMode.bySerializedName(NbtCompat.getString(baseTag, "Mode", ""), MachineMode.INVERTED),
                    NbtCompat.getBoolean(baseTag, "MultiTargeting", false), NbtCompat.getBoolean(baseTag, "TargetPlayers", false),
                    NbtCompat.getBoolean(baseTag, "TargetMobs", false), NbtCompat.getBoolean(baseTag, "TargetPassive", false),
                    NbtCompat.getInt(baseTag, "Range", 0));
        }
        // cards written before the trust list had its own section kept it in the base settings
        var trustList = NbtCompat.hasList(tag, TRUST) ? NbtCompat.getCompoundList(tag, TRUST)
                : NbtCompat.getCompoundList(NbtCompat.getCompound(tag, BASE), "TrustedPlayers");
        MemoryCardData.TrustSettings trust = new MemoryCardData.TrustSettings(SecurityNbt.loadTrustedPlayers(trustList));
        MemoryCardData.CamoSettings camo = null;
        if (NbtCompat.hasCompound(tag, CAMO)) {
            CompoundTag camoTag = NbtCompat.getCompound(tag, CAMO);
            camo = new MemoryCardData.CamoSettings(
                    NbtCompat.hasString(camoTag, "Disguise") ? NbtCompat.getString(camoTag, "Disguise", "") : null,
                    NbtCompat.hasNumber(camoTag, "LightValue")
                            ? new CamoLight(NbtCompat.getInt(camoTag, "LightValue", 0), NbtCompat.getInt(camoTag, "LightOpacity", 0)) : null);
        }
        try {
            return Optional.of(new MemoryCardData(source, base, trust, camo));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
