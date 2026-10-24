package omtteam.openmodularturrets.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;
import omtteam.openmodularturrets.core.world.Neighbour;
import omtteam.openmodularturrets.core.world.Vector3;
import org.junit.jupiter.api.Test;

/**
 * equals and hashCode of the core value classes (the Java 8 stand-ins for records). {@code NULL_FIELDS}: the
 * constructors reject or copy their arguments, so those fields are never null.
 */
class EqualsContractTest {
    @Test
    void ammoDraw() {
        EqualsVerifier.forClass(AmmoDraw.class).verify();
    }

    @Test
    void baseState() {
        EqualsVerifier.forClass(TurretBaseLogic.State.class).verify();
    }

    @Test
    void turretStats() {
        EqualsVerifier.forClass(TurretStats.class).verify();
    }

    @Test
    void ammoType() {
        EqualsVerifier.forClass(AmmoType.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void projectileSplit() {
        EqualsVerifier.forClass(ProjectileMath.Split.class).verify();
    }

    @Test
    void angles() {
        EqualsVerifier.forClass(TurretMath.Angles.class).verify();
    }

    @Test
    void blockPosition() {
        EqualsVerifier.forClass(BlockPosition.class).verify();
    }

    @Test
    void box() {
        EqualsVerifier.forClass(Box.class).verify();
    }

    @Test
    void neighbourTurret() {
        EqualsVerifier.forClass(Neighbour.Turret.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void vector3() {
        EqualsVerifier.forClass(Vector3.class).verify();
    }
}
