package omtteam.openmodularturrets.tileentity.base;

import omtteam.omlib.util.player.OMLibSecurityEnvironment;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * The security environment of turret bases: OMLib's (teams, operators, shared owners, offline mode) plus the OMT
 * config switch {@code turretDamageTrustedPlayers}.
 */
public class OMTSecurityEnvironment extends OMLibSecurityEnvironment {
    public static final OMTSecurityEnvironment INSTANCE = new OMTSecurityEnvironment();

    protected OMTSecurityEnvironment() {
    }

    @Override
    public boolean turretsDamageTrustedPlayers() {
        return OMTConfig.TURRETS.turretDamageTrustedPlayers;
    }
}
