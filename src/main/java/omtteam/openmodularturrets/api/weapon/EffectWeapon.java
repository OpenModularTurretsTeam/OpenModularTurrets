package omtteam.openmodularturrets.api.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;

import java.util.Objects;

/**
 * Acts on the base's target without aiming (1.12.2 {@code TurretHead.updateChecks} turrets: slowing, teleporter), once
 * per shot. Usually combined with {@code TurretType.Builder#ignoresScattershot()}.
 */
public record EffectWeapon(SoundEvent launchSound, TargetEffect effect) implements TurretWeapon {
    public EffectWeapon {
        Objects.requireNonNull(launchSound, "launchSound");
        Objects.requireNonNull(effect, "effect");
    }

    @Override
    public boolean aims() {
        return false;
    }

    /** What a shot does to the target. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface TargetEffect {
        void apply(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Entity target);
    }
}
