package omtteam.openmodularturrets.turret;

import omtteam.openmodularturrets.config.TurretSetting;
import omtteam.openmodularturrets.reference.OMTNames;

import java.util.HashMap;
import java.util.Map;

/**
 * The 3.x turret type: block registry name and config section. Its rules (tier, ammo, targeting) are the core
 * {@link omtteam.openmodularturrets.core.turret.TurretType} of the same id, see {@link #getCore()}.
 */
public class TurretType {
    /** 3.x block ids that differ from the core id (the relativistic turret is the core's slowing turret). */
    private static final Map<String, omtteam.openmodularturrets.core.turret.TurretType> RENAMED = new HashMap<>();

    static {
        RENAMED.put(OMTNames.Blocks.disposableItemTurret,
                omtteam.openmodularturrets.core.turret.TurretType.DISPOSABLE_TURRET);
        RENAMED.put(OMTNames.Blocks.railGunTurret, omtteam.openmodularturrets.core.turret.TurretType.RAILGUN_TURRET);
        RENAMED.put(OMTNames.Blocks.relativisticTurret,
                omtteam.openmodularturrets.core.turret.TurretType.SLOWING_TURRET);
    }

    private final String internalName;
    private final TurretSetting settings;
    private final omtteam.openmodularturrets.core.turret.TurretType core;

    public TurretType(String internalName, TurretSetting settings) {
        this.internalName = internalName;
        this.settings = settings;
        this.core = toCore(internalName);
    }

    /** The core type of a 3.x turret block id. */
    public static omtteam.openmodularturrets.core.turret.TurretType toCore(String internalName) {
        omtteam.openmodularturrets.core.turret.TurretType renamed = RENAMED.get(internalName);
        if (renamed != null) return renamed;
        return omtteam.openmodularturrets.core.turret.TurretType.byId(internalName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown turret type " + internalName));
    }

    public String getInternalName() {
        return internalName;
    }

    public TurretSetting getSettings() {
        return settings;
    }

    /** The core turret type with the rules of this turret. */
    public omtteam.openmodularturrets.core.turret.TurretType getCore() {
        return core;
    }
}
