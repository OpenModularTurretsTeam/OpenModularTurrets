package omtteam.openmodularturrets.platform.neoforge.opencomputers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.UsernameCache;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.openmodularturrets.core.computer.PlayerNames;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Player names for computers on NeoForge: a player online on {@code server}, else one in NeoForge's
 * {@link UsernameCache} (every player who joined this server before; 1.12 used Forge's cache too). Names match without
 * regard to case. Never asks Mojang, as a computer's call runs on the server thread.
 */
final class NeoForgePlayerNames implements PlayerNames {
    private final MinecraftServer server;

    NeoForgePlayerNames(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public Optional<PlayerIdentity> find(String name) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerIdentity identity = PlayerAdapter.identity(player);
            if (identity.name().equalsIgnoreCase(name)) {
                return Optional.of(identity);
            }
        }
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return Optional.of(new PlayerIdentity(entry.getKey(), entry.getValue()));
            }
        }
        return Optional.empty();
    }
}
