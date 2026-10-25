package omtteam.openmodularturrets.security;

import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.security.PlayerIdentity;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Resolves player names to UUIDs on the server (online players first, then the server's profile cache).
 * Replaces OMLib {@code PlayerUtil.getPlayerFromUsernameCache}.
 * <p>
 * A name that is not in the profile cache is looked up at Mojang, a blocking web request, so the cache is asked on a
 * background thread and every player may have only one such lookup at a time; otherwise anyone could stall the
 * server thread by sending names.
 */
public final class PlayerLookup {
    /** Players with a lookup in progress; cleared when it completes, so it holds no per-world data. */
    private static final Set<UUID> PENDING = ConcurrentHashMap.newKeySet();

    private PlayerLookup() {
    }

    /**
     * Resolves {@code name} for {@code requester} and passes the result to {@code result} on the server thread: at
     * once for an online player, otherwise after a background lookup.
     *
     * @return {@code false} (and {@code result} is never called) if the requester already waits for a lookup
     */
    public static boolean findByName(MinecraftServer server, ServerPlayer requester, String name,
                                     Consumer<Optional<PlayerIdentity>> result) {
        ServerPlayer online = server.getPlayerList().getPlayerByName(name);
        if (online != null) {
            result.accept(Optional.of(PlayerAdapter.identity(online)));
            return true;
        }
        GameProfileCache cache = server.getProfileCache();
        if (cache == null) {
            result.accept(Optional.empty());
            return true;
        }
        UUID id = requester.getUUID();
        if (!PENDING.add(id)) return false;
        CompletableFuture.supplyAsync(() -> cache.get(name), Util.backgroundExecutor())
                .whenComplete((profile, error) -> {
                    PENDING.remove(id);
                    if (error != null) OMT.LOGGER.warn("Looking up player {} failed", name, error);
                    Optional<PlayerIdentity> found = error != null ? Optional.empty() : profile
                            .filter(candidate -> candidate.getId() != null)
                            .map(PlayerLookup::identity);
                    server.execute(() -> result.accept(found));
                });
        return true;
    }

    private static PlayerIdentity identity(GameProfile profile) {
        return new PlayerIdentity(profile.getId(), profile.getName());
    }
}
