package omtteam.openmodularturrets.api;

import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.Objects;

/**
 * Everything Open Modular Turrets needs to know about a turret.
 *
 * @param type         id, base tier, ammo and targeting rules
 * @param weapon       what a shot does
 * @param defaultStats default values of the turret's config entry; for turrets of other mods it is written to
 *                     {@code turrets.addon_turrets."<id>"} in {@code config/openmodularturrets.json}
 */
public record TurretDefinition(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
    public TurretDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(weapon, "weapon");
        Objects.requireNonNull(defaultStats, "defaultStats");
    }
}
