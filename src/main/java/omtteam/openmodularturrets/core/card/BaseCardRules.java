package omtteam.openmodularturrets.core.card;

import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.machine.TargetingSettings;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;

/**
 * The turret base part of a memory card (1.20.1+ {@code MemoryCardRules.capture} / {@code apply}): mode, multi
 * targeting, target categories and range. Who may copy and paste is OMLib's {@code MemoryCardRules}.
 */
public final class BaseCardRules {
    private BaseCardRules() {
    }

    /** The settings of a base as stored on a card. */
    public static MemoryCardData.BaseSettings capture(TurretBaseLogic logic) {
        TargetingSettings targeting = logic.getTargetingSettings();
        return new MemoryCardData.BaseSettings(logic.getMode(), logic.isMultiTargeting(), targeting.isTargetPlayers(),
                targeting.isTargetMobs(), targeting.isTargetPassive(), targeting.getRange());
    }

    /**
     * Writes card settings to a base, without permission checks (the caller checked {@code canPaste}). The range is
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
}
