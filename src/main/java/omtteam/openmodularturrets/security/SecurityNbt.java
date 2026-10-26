package omtteam.openmodularturrets.security;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.security.TrustedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Saves and loads the owner and trust list of a {@link TrustManager}.
 */
public final class SecurityNbt {
    private static final String OWNER = "Owner";
    private static final String TRUSTED_PLAYERS = "TrustedPlayers";

    private SecurityNbt() {
    }

    public static void save(TrustManager manager, CompoundTag tag) {
        if (manager.getOwner() != null) {
            tag.put(OWNER, saveIdentity(manager.getOwner()));
        }
        tag.put(TRUSTED_PLAYERS, saveTrustedPlayers(manager.getTrustedPlayers()));
    }

    public static ListTag saveTrustedPlayers(List<TrustedPlayer> trustedPlayers) {
        ListTag list = new ListTag();
        for (TrustedPlayer entry : trustedPlayers) {
            CompoundTag entryTag = saveIdentity(entry.getIdentity());
            entryTag.putString("AccessLevel", entry.getAccessLevel().getSerializedName());
            list.add(entryTag);
        }
        return list;
    }

    /** Entries without a valid UUID are dropped. */
    public static List<TrustedPlayer> loadTrustedPlayers(ListTag list) {
        List<TrustedPlayer> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = NbtCompat.getCompound(list, i);
            PlayerIdentity identity = loadIdentity(entryTag);
            if (identity != null) {
                AccessLevel level = AccessLevel.bySerializedName(NbtCompat.getString(entryTag, "AccessLevel", ""), AccessLevel.NONE);
                entries.add(new TrustedPlayer(identity, level));
            }
        }
        return entries;
    }

    /** Entries without a valid UUID are dropped. */
    public static void load(TrustManager manager, CompoundTag tag) {
        PlayerIdentity owner = NbtCompat.hasCompound(tag, OWNER) ? loadIdentity(NbtCompat.getCompound(tag, OWNER)) : null;
        manager.load(owner, loadTrustedPlayers(NbtCompat.getCompoundList(tag, TRUSTED_PLAYERS)));
    }

    private static CompoundTag saveIdentity(PlayerIdentity identity) {
        CompoundTag tag = new CompoundTag();
        NbtCompat.putUuid(tag, "UUID", identity.uuid());
        tag.putString("Name", identity.name());
        return tag;
    }

    private static PlayerIdentity loadIdentity(CompoundTag tag) {
        UUID uuid = NbtCompat.getUuid(tag, "UUID");
        return uuid != null ? new PlayerIdentity(uuid, NbtCompat.getString(tag, "Name", "")) : null;
    }
}
