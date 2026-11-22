package omtteam.openmodularturrets.tileentity.base;

import omtteam.omlib.util.player.OMLibSecurityEnvironment;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * The security environment of turret bases: OMLib's (teams, operators, shared owners, offline mode) plus the OMT
 * config switch {@code turretDamageTrustedPlayers}. It overrides {@link #damageTrustedPlayers()}, the core's name, not
 * OMLib's deprecated {@code turretsDamageTrustedPlayers()} hook (that one is only for OMT 4.0.1).
 */
public class OMTSecurityEnvironment extends OMLibSecurityEnvironment {
    public static final OMTSecurityEnvironment INSTANCE = new OMTSecurityEnvironment();

    protected OMTSecurityEnvironment() {
    }

    @Override
    public boolean damageTrustedPlayers() {
        return OMTConfig.TURRETS.turretDamageTrustedPlayers;
    }
}
