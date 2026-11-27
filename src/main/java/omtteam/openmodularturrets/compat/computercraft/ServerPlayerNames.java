package omtteam.openmodularturrets.compat.computercraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.security.PlayerAdapter;
import omtteam.openmodularturrets.core.computer.PlayerNames;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * Player names for computers on both loaders: a player online on {@code server}, else one in the server's
 * {@code usercache.json} (every player who joined before and whose entry has not expired). The file is read here
 * because the profile cache only offers lookups that ask Mojang for an unknown name, and a computer's call runs on
 * the server thread; a missing or unreadable file means no cached players. Names match without regard to case.
 */
final class ServerPlayerNames implements PlayerNames {
    private static final String CACHE_FILE = "usercache.json";

    private final MinecraftServer server;

    ServerPlayerNames(MinecraftServer server) {
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
        return cached(name);
    }

    private Optional<PlayerIdentity> cached(String name) {
        Path file = server.getFile(CACHE_FILE);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            for (JsonElement entry : JsonParser.parseReader(reader).getAsJsonArray()) {
                JsonObject object = entry.getAsJsonObject();
                if (object.get("name").getAsString().equalsIgnoreCase(name)) {
                    return Optional.of(new PlayerIdentity(UUID.fromString(object.get("uuid").getAsString()),
                            object.get("name").getAsString()));
                }
            }
        } catch (IOException | RuntimeException e) {
            // unreadable or malformed cache: the same as an unknown name
            return Optional.empty();
        }
        return Optional.empty();
    }
}
