package omtteam.openmodularturrets.security;

import omtteam.omlib.security.OwnershipSettings;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * The mod's ownership rules, read from the config each time they are asked.
 */
public final class OMTOwnershipSettings implements OwnershipSettings {
    public static final OMTOwnershipSettings INSTANCE = new OMTOwnershipSettings();

    private OMTOwnershipSettings() {
    }

    @Override
    public boolean operatorsCanAccessOwnedBlocks() {
        return OMTConfig.get().general.canOPAccessOwnedBlocks;
    }

    @Override
    public boolean damageTrustedPlayers() {
        return OMTConfig.get().turrets.turretDamageTrustedPlayers;
    }

    @Override
    public int claimSpreadLimit() {
        return OMTConfig.get().bases.claimSpreadLimit;
    }

    @Override
    public boolean fakePlayersCanClaim() {
        return OMTConfig.get().general.fakePlayersCanClaim;
    }
}
