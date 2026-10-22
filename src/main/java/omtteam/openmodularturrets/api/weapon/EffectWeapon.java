package omtteam.openmodularturrets.api.weapon;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;

import java.util.Objects;

/**
 * Acts on the base's target without aiming (1.12.2 {@code TurretHead.updateChecks} turrets: slowing, teleporter), once
 * per shot.
 */
public final class EffectWeapon implements TurretWeapon {
    private final SoundEvent launchSound;
    private final TargetEffect effect;

    public EffectWeapon(SoundEvent launchSound, TargetEffect effect) {
        this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
        this.effect = Objects.requireNonNull(effect, "effect");
    }

    @Override
    public SoundEvent launchSound() {
        return launchSound;
    }

    public TargetEffect effect() {
        return effect;
    }

    @Override
    public boolean aims() {
        return false;
    }

    /** What a shot does to the target. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface TargetEffect {
        void apply(WorldServer world, TurretTileEntity turret, TurretBase base, EntityLivingBase target);
    }
}
