package omtteam.openmodularturrets.security;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import omtteam.openmodularturrets.core.security.AccessLevel;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.security.TrustedPlayer;

import java.util.ArrayList;
import java.util.List;

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
            CompoundTag entryTag = list.getCompound(i);
            PlayerIdentity identity = loadIdentity(entryTag);
            if (identity != null) {
                AccessLevel level = AccessLevel.bySerializedName(entryTag.getString("AccessLevel"), AccessLevel.NONE);
                entries.add(new TrustedPlayer(identity, level));
            }
        }
        return entries;
    }

    /** Entries without a valid UUID are dropped. */
    public static void load(TrustManager manager, CompoundTag tag) {
        PlayerIdentity owner = tag.contains(OWNER, Tag.TAG_COMPOUND) ? loadIdentity(tag.getCompound(OWNER)) : null;
        manager.load(owner, loadTrustedPlayers(tag.getList(TRUSTED_PLAYERS, Tag.TAG_COMPOUND)));
    }

    private static CompoundTag saveIdentity(PlayerIdentity identity) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("UUID", identity.uuid());
        tag.putString("Name", identity.name());
        return tag;
    }

    private static PlayerIdentity loadIdentity(CompoundTag tag) {
        return tag.hasUUID("UUID") ? new PlayerIdentity(tag.getUUID("UUID"), tag.getString("Name")) : null;
    }
}
