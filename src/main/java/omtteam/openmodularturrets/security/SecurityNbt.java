package omtteam.openmodularturrets.security;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.UUIDUtil;
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
        ListTag list = new ListTag();
        for (TrustedPlayer entry : manager.getTrustedPlayers()) {
            CompoundTag entryTag = saveIdentity(entry.getIdentity());
            entryTag.putString("AccessLevel", entry.getAccessLevel().getSerializedName());
            list.add(entryTag);
        }
        tag.put(TRUSTED_PLAYERS, list);
    }

    /** Entries without a valid UUID are dropped. */
    public static void load(TrustManager manager, CompoundTag tag) {
        PlayerIdentity owner = tag.getCompound(OWNER).map(SecurityNbt::loadIdentity).orElse(null);
        List<TrustedPlayer> entries = new ArrayList<>();
        ListTag list = tag.getListOrEmpty(TRUSTED_PLAYERS);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompoundOrEmpty(i);
            PlayerIdentity identity = loadIdentity(entryTag);
            if (identity != null) {
                AccessLevel level = AccessLevel.bySerializedName(entryTag.getStringOr("AccessLevel", ""), AccessLevel.NONE);
                entries.add(new TrustedPlayer(identity, level));
            }
        }
        manager.load(owner, entries);
    }

    private static CompoundTag saveIdentity(PlayerIdentity identity) {
        CompoundTag tag = new CompoundTag();
        tag.store("UUID", UUIDUtil.CODEC, identity.uuid());
        tag.putString("Name", identity.name());
        return tag;
    }

    private static PlayerIdentity loadIdentity(CompoundTag tag) {
        return tag.read("UUID", UUIDUtil.CODEC).map(uuid -> new PlayerIdentity(uuid, tag.getStringOr("Name", ""))).orElse(null);
    }
}
