package omtteam.openmodularturrets.security;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import omtteam.openmodularturrets.core.security.PlayerIdentity;

import java.util.Optional;

/**
 * Resolves player names to UUIDs on the server (online players first, then the server's profile cache).
 * Replaces OMLib {@code PlayerUtil.getPlayerFromUsernameCache}.
 */
public final class PlayerLookup {
    private PlayerLookup() {
    }

    public static Optional<PlayerIdentity> findByName(MinecraftServer server, String name) {
        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) {
            return Optional.of(PlayerAdapter.identity(online));
        }
        GameProfileCache cache = server.getProfileCache();
        if (cache == null) return Optional.empty();
        return cache.get(name)
                .filter(profile -> profile.getId() != null)
                .map(PlayerLookup::identity);
    }

    private static PlayerIdentity identity(GameProfile profile) {
        return new PlayerIdentity(profile.getId(), profile.getName());
    }
}
