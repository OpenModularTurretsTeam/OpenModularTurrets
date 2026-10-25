package omtteam.openmodularturrets.api.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;

import java.util.List;
import java.util.Objects;

/**
 * Ignores the base's target and picks its own: once the turret is ready to fire (cooldown passed, base active, enough
 * energy), the {@link #picker} looks for targets itself (e.g. with {@code level.getEntitiesOfClass} within
 * {@code base.getLogic().getRange(turret.getTurretType())} of the base) and returns the ones to shoot at. If it
 * returns any, the turret takes energy and ammo and calls the {@link #shot}. While the picker finds nothing it is
 * asked again every {@link #RETRY_TICKS} ticks.
 * <p>
 * Trust is up to the weapon: use {@code OMTApi.canDamage(base, entity)} to skip the owner, trusted players and their
 * pets, and {@code OMTApi.hurt(...)} to deal damage the way built-in turrets do.
 * <p>
 * On a Sable contraption the turret's block position is in the contraption's plot, while entities are in the world:
 * measure from {@code turret.getMount().centre()} (see
 * {@link omtteam.openmodularturrets.blockentity.turret.TurretMount}).
 *
 * @param aims whether the turret turns towards the first picked entity
 */
public record SelfTargetingWeapon(SoundEvent launchSound, boolean aims, TargetPicker picker, Shot shot)
        implements TurretWeapon {
    /** Ticks between two picks while the last one found nothing. */
    public static final int RETRY_TICKS = 5;

    public SelfTargetingWeapon {
        Objects.requireNonNull(launchSound, "launchSound");
        Objects.requireNonNull(picker, "picker");
        Objects.requireNonNull(shot, "shot");
    }

    /** Finds the entities to shoot at. */
    @FunctionalInterface
    public interface TargetPicker {
        /** @return the entities to shoot at; empty to not shoot (nothing is consumed) */
        List<Entity> pick(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base);
    }

    /** What a shot does. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface Shot {
        /** @param targets what the picker returned, never empty */
        void fire(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, List<Entity> targets);
    }
}
