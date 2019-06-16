package omtteam.openmodularturrets.api.lists;

import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TurretList {
    private static Map<String, TurretType> types = new HashMap<>();

    /**
     * Register all your turrets in this list or things will break.
     *
     * @param turretType the TurretType Instance
     */
    @ParametersAreNonnullByDefault
    public static void addTurret(TurretType turretType) {
        types.put(turretType.getInternalName(), turretType);
    }

    /**
     * Returns the corresponding TurretType instance to the internal name.
     *
     * @param turretIn the internal name of the turret (usually block registry name)
     * @return null or the corresponding TurretType
     */
    @Nullable
    public static TurretType getTurretType(String turretIn) {
        return types.get(turretIn);
    }

    public static Collection<TurretType> getAllTurrets() {
        return types.values();
    }
}
