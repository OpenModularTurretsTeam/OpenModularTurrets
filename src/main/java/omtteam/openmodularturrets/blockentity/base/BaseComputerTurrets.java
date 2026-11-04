package omtteam.openmodularturrets.blockentity.base;

import omtteam.omlib.core.world.Face;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.core.computer.ComputerTurrets;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * The turrets of a turret base for the computer commands (1.12.2 {@code TurretBase.setTurretYawPitch},
 * {@code setTurretForceFire} and {@code forceShootTurret}): the turret block entities on its faces
 * ({@link TurretBaseBlockEntity#getTurret}).
 */
public final class BaseComputerTurrets implements ComputerTurrets {
    private final TurretBaseBlockEntity base;

    public BaseComputerTurrets(TurretBaseBlockEntity base) {
        this.base = base;
    }

    @Override
    public boolean setYawPitch(Face face, float yaw, float pitch) {
        Optional<TurretBlockEntity> turret = base.getTurret(face);
        turret.ifPresent(t -> t.setYawPitch(yaw, pitch));
        return turret.isPresent();
    }

    @Override
    public boolean setAutoFire(Face face, boolean autoFire) {
        Optional<TurretBlockEntity> turret = base.getTurret(face);
        turret.ifPresent(t -> t.setAutoFire(autoFire));
        return turret.isPresent();
    }

    @Override
    public boolean forceShot(Face face) {
        return base.getTurret(face).map(TurretBlockEntity::forceShot).orElse(false);
    }

    @Override
    public Set<Face> faces() {
        Set<Face> faces = EnumSet.noneOf(Face.class);
        for (Face face : Face.values()) {
            if (base.getTurret(face).isPresent()) faces.add(face);
        }
        return faces;
    }
}
