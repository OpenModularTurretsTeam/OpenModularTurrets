package omtteam.openmodularturrets.core.card;

import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.omlib.core.security.PlayerInfo;
import omtteam.omlib.core.security.TrustManager;

/**
 * Copying settings of a turret base to a memory card and writing them to another base, and who may do that.
 * <p>
 * The card holds the trust list and the redstone mode, which are admin settings, and copying reads them, so both
 * directions need the admin access level (the owner always has it) on bases and on camouflage traps. The camouflage
 * part of a card on a base has its own rules, see {@code CamoPermissions}.
 */
public final class MemoryCardRules {
    private MemoryCardRules() {
    }

    /** Reading the settings of a base onto a card. */
    public static boolean canCopy(TrustManager trust, PlayerInfo player) {
        return trust.isAdmin(player);
    }

    /** Writing the settings of a card to a base. */
    public static boolean canPaste(TrustManager trust, PlayerInfo player) {
        return trust.isAdmin(player);
    }

    /** The settings of a base as stored on a card. */
    public static MemoryCardData.BaseSettings capture(TurretBaseLogic logic) {
        var targeting = logic.getTargetingSettings();
        return new MemoryCardData.BaseSettings(logic.getMode(), logic.isMultiTargeting(), targeting.isTargetPlayers(),
                targeting.isTargetMobs(), targeting.isTargetPassive(), targeting.getRange());
    }

    /** The trust list of a base or trap as stored on a card. */
    public static MemoryCardData.TrustSettings captureTrust(TrustManager trust) {
        return new MemoryCardData.TrustSettings(trust.getTrustedPlayers());
    }

    /**
     * Writes card settings to a base, without permission checks (the caller checked {@link #canPaste}). The range is
     * clamped to what the target base can reach.
     */
    public static void apply(MemoryCardData.BaseSettings settings, TurretBaseLogic logic) {
        logic.setMode(settings.mode());
        logic.setMultiTargeting(settings.multiTargeting());
        logic.setTargetPlayers(settings.targetPlayers());
        logic.setTargetMobs(settings.targetMobs());
        logic.setTargetPassive(settings.targetPassive());
        logic.updateMaxRange();
        logic.setRange(Math.min(settings.range(), logic.getTargetingSettings().getMaxRange()));
    }

    /**
     * Writes a trust list to a base or trap, without permission checks. It replaces the existing list, keeps the owner
     * of the target (the owner is never part of the list) and clamps the levels to the ones the target uses.
     */
    public static void applyTrust(MemoryCardData.TrustSettings settings, TrustManager trust) {
        trust.load(trust.getOwner(), settings.trustedPlayers());
    }
}
