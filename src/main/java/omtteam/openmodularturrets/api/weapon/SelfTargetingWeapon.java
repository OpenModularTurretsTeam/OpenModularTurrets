package omtteam.openmodularturrets.api.weapon;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.List;
import java.util.Objects;

/**
 * Ignores the base's target and picks its own (1.20.1+ {@code SelfTargetingWeapon}): once the turret is ready to fire
 * (cooldown passed, base active, enough energy), the {@link #picker()} looks for targets itself (e.g. with
 * {@code world.getEntitiesWithinAABB} within the turret's range around the base) and returns the ones to shoot at. If
 * it returns any, the turret takes energy and ammo and calls the {@link #shot()}. While the picker finds nothing it is
 * asked again every {@link #RETRY_TICKS} ticks.
 * <p>
 * Trust is up to the weapon: use {@code OMTApi.canDamage(base, entity)} to skip the owner, trusted players and their
 * pets, and {@code OMTApi.hurt(...)} to deal damage the way built-in turrets do.
 */
public final class SelfTargetingWeapon implements TurretWeapon {
    /** Ticks between two picks while the last one found nothing. */
    public static final int RETRY_TICKS = 5;

    private final SoundEvent launchSound;
    private final boolean aims;
    private final TargetPicker picker;
    private final Shot shot;

    /** @param aims whether the turret turns towards the first picked entity */
    public SelfTargetingWeapon(SoundEvent launchSound, boolean aims, TargetPicker picker, Shot shot) {
        this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
        this.aims = aims;
        this.picker = Objects.requireNonNull(picker, "picker");
        this.shot = Objects.requireNonNull(shot, "shot");
    }

    @Override
    public SoundEvent launchSound() {
        return launchSound;
    }

    @Override
    public boolean aims() {
        return aims;
    }

    public TargetPicker picker() {
        return picker;
    }

    public Shot shot() {
        return shot;
    }

    /** Finds the entities to shoot at. */
    @FunctionalInterface
    public interface TargetPicker {
        /** @return the entities to shoot at; empty to not shoot (nothing is consumed) */
        List<Entity> pick(WorldServer world, TurretTileEntity turret, TurretBase base);
    }

    /** What a shot does. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface Shot {
        /** @param targets what the picker returned, never empty */
        void fire(WorldServer world, TurretTileEntity turret, TurretBase base, List<Entity> targets);
    }
}
