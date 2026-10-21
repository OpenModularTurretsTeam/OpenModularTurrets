package omtteam.openmodularturrets.core.security;

import java.util.Objects;
import java.util.UUID;

/**
 * An entry of a base's trust list. Ported from OMLib {@code TrustedPlayer}; new entries start at
 * {@link AccessLevel#NONE}.
 */
public final class TrustedPlayer {
    private PlayerIdentity identity;
    private AccessLevel accessLevel;

    public TrustedPlayer(PlayerIdentity identity, AccessLevel accessLevel) {
        this.identity = Objects.requireNonNull(identity, "identity");
        this.accessLevel = Objects.requireNonNull(accessLevel, "accessLevel");
    }

    public PlayerIdentity getIdentity() {
        return identity;
    }

    public UUID getUuid() {
        return identity.uuid();
    }

    public String getName() {
        return identity.name();
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = Objects.requireNonNull(accessLevel, "accessLevel");
    }

    void setName(String name) {
        this.identity = identity.withName(name);
    }

    /** A copy with the same identity and level. */
    public TrustedPlayer copy() {
        return new TrustedPlayer(identity, accessLevel);
    }
}
