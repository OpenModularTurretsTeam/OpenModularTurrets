package omtteam.openmodularturrets.turret;

import javax.annotation.Nullable;

/**
 * The 3.x tooltip texts of a turret item that are not config values (the 14 {@code ItemBlock…Turret} classes).
 */
public final class TurretTooltip {
    private final String accuracyKey;
    @Nullable
    private final String ammoKey;
    private final int areaOfEffect;
    private final String flavourKey;

    /**
     * @param accuracyKey  translation key of the accuracy class (low, medium, high, exact)
     * @param ammoKey      translation key of the ammo, null for turrets without an ammo line
     * @param areaOfEffect blast radius shown in the tooltip
     * @param flavourKey   translation key of the flavour line
     */
    public TurretTooltip(String accuracyKey, @Nullable String ammoKey, int areaOfEffect, String flavourKey) {
        this.accuracyKey = accuracyKey;
        this.ammoKey = ammoKey;
        this.areaOfEffect = areaOfEffect;
        this.flavourKey = flavourKey;
    }

    public String accuracyKey() {
        return accuracyKey;
    }

    @Nullable
    public String ammoKey() {
        return ammoKey;
    }

    public int areaOfEffect() {
        return areaOfEffect;
    }

    public String flavourKey() {
        return flavourKey;
    }
}
