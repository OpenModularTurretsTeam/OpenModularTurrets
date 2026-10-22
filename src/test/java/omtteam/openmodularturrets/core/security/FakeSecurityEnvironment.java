package omtteam.openmodularturrets.core.security;

import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.SecurityEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Test double for {@link SecurityEnvironment}: teams by player name, switchable config flags. */
class FakeSecurityEnvironment implements SecurityEnvironment {
    final Map<String, String> teams = new HashMap<>();
    boolean operatorsCanAccess;
    boolean damageTrusted;

    @Override
    public Optional<String> getTeam(UUID uuid, String name) {
        return Optional.ofNullable(teams.get(name));
    }

    @Override
    public boolean operatorsCanAccessOwnedBlocks() {
        return operatorsCanAccess;
    }

    @Override
    public boolean turretsDamageTrustedPlayers() {
        return damageTrusted;
    }

    @Override
    public boolean isSharedOwner(PlayerIdentity owner, PlayerIdentity player) {
        return false;
    }

    @Override
    public boolean offlineModeSupport() {
        return false;
    }
}
