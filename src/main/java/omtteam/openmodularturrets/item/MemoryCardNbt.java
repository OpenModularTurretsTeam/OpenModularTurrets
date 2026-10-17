package omtteam.openmodularturrets.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import omtteam.openmodularturrets.core.base.MachineMode;
import omtteam.openmodularturrets.core.camo.CamoLight;
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
            source = MemoryCardData.Source.valueOf(tag.getString(SOURCE).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        MemoryCardData.BaseSettings base = null;
        if (tag.contains(BASE, Tag.TAG_COMPOUND)) {
            CompoundTag baseTag = tag.getCompound(BASE);
            base = new MemoryCardData.BaseSettings(
                    MachineMode.bySerializedName(baseTag.getString("Mode"), MachineMode.INVERTED),
                    baseTag.getBoolean("MultiTargeting"), baseTag.getBoolean("TargetPlayers"),
                    baseTag.getBoolean("TargetMobs"), baseTag.getBoolean("TargetPassive"), baseTag.getInt("Range"));
        }
        // cards written before the trust list had its own section kept it in the base settings
        var trustList = tag.contains(TRUST, Tag.TAG_LIST) ? tag.getList(TRUST, Tag.TAG_COMPOUND)
                : tag.getCompound(BASE).getList("TrustedPlayers", Tag.TAG_COMPOUND);
        MemoryCardData.TrustSettings trust = new MemoryCardData.TrustSettings(SecurityNbt.loadTrustedPlayers(trustList));
        MemoryCardData.CamoSettings camo = null;
        if (tag.contains(CAMO, Tag.TAG_COMPOUND)) {
            CompoundTag camoTag = tag.getCompound(CAMO);
            camo = new MemoryCardData.CamoSettings(
                    camoTag.contains("Disguise", Tag.TAG_STRING) ? camoTag.getString("Disguise") : null,
                    camoTag.contains("LightValue", Tag.TAG_ANY_NUMERIC)
                            ? new CamoLight(camoTag.getInt("LightValue"), camoTag.getInt("LightOpacity")) : null);
        }
        try {
            return Optional.of(new MemoryCardData(source, base, trust, camo));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
