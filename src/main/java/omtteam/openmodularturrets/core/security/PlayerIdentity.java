package omtteam.openmodularturrets.core.security;

import java.util.Objects;
import java.util.UUID;

/**
 * A player as stored by a base (owner or trusted player). The UUID is the identity; the name is the last known name
 * and only used for display.
 */
public record PlayerIdentity(UUID uuid, String name) {
    public PlayerIdentity {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(name, "name");
    }

    public boolean is(UUID other) {
        return uuid.equals(other);
    }

    /** Same identity, possibly with a different (newer) name. */
    public PlayerIdentity withName(String newName) {
        return new PlayerIdentity(uuid, newName);
    }
}
