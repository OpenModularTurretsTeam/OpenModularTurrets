package omtteam.openmodularturrets.core.security;

import omtteam.openmodularturrets.core.security.DamageRules.Victim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DamageRulesTest {
    private static final PlayerInfo OWNER = player("Owner");
    private static final PlayerInfo TRUSTED = player("Trusted");
    private static final PlayerInfo TEAMMATE = player("Teammate");
    private static final PlayerInfo STRANGER = player("Stranger");

    private FakeSecurityEnvironment environment;
    private Optional<TrustManager> trust;

    private static PlayerInfo player(String name) {
        return new PlayerInfo(UUID.nameUUIDFromBytes(name.getBytes()), name, false);
    }

    @BeforeEach
    void setUp() {
        environment = new FakeSecurityEnvironment();
        environment.teams.put(OWNER.name(), "blue");
        environment.teams.put(TEAMMATE.name(), "blue");
        TrustManager manager = new TrustManager(environment, () -> {
        });
        manager.setOwner(OWNER.identity());
        assertEquals(TrustResult.SUCCESS, manager.addTrustedPlayer(OWNER, TRUSTED.identity()));
        trust = Optional.of(manager);
    }

    private boolean player(PlayerInfo player) {
        return DamageRules.canDamage(trust, new Victim.Player(player.identity()));
    }

    private boolean pet(PlayerInfo owner) {
        return DamageRules.canDamage(trust, new Victim.Pet(owner.identity()));
    }

    @Test
    void neverHitsTurretProjectiles() {
        assertFalse(DamageRules.canDamage(trust, new Victim.Projectile()));
        assertFalse(DamageRules.canDamage(Optional.empty(), new Victim.Projectile()));
    }

    @Test
    void hitsMobs() {
        assertTrue(DamageRules.canDamage(trust, new Victim.Other()));
    }

    @Test
    void sparesOwnerTeamAndTrustedPlayers() {
        assertFalse(player(OWNER));
        assertFalse(player(TEAMMATE));
        assertFalse(player(TRUSTED));
        assertTrue(player(STRANGER));
    }

    @Test
    void trustedPlayersAreHitIfConfigured() {
        environment.damageTrusted = true;
        assertTrue(player(TRUSTED));
        assertFalse(player(OWNER), "never the owner");
        assertFalse(player(TEAMMATE), "never the owner's team");
    }

    @Test
    void petsFollowTheirOwnersRules() {
        assertFalse(pet(OWNER));
        assertFalse(pet(TRUSTED));
        assertTrue(pet(STRANGER));
    }

    @Test
    void withoutBaseOnlyPlayersAndPetsAreSpared() {
        assertFalse(DamageRules.canDamage(Optional.empty(), new Victim.Player(STRANGER.identity())));
        assertFalse(DamageRules.canDamage(Optional.empty(), new Victim.Pet(STRANGER.identity())));
        assertTrue(DamageRules.canDamage(Optional.empty(), new Victim.Other()));
    }
}
