package omtteam.openmodularturrets.security;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import omtteam.openmodularturrets.core.security.PlayerIdentity;

import java.util.Optional;

/**
 * Resolves player names to UUIDs on the server (online players first, then the server's name cache).
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
        return server.services().nameToIdCache().get(name)
                .filter(profile -> profile.id() != null)
                .map(PlayerLookup::identity);
    }

    private static PlayerIdentity identity(NameAndId profile) {
        return new PlayerIdentity(profile.id(), profile.name());
    }
}
