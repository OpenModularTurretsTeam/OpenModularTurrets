package omtteam.openmodularturrets.api.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Ignores the base's target (melee turret): the turret is active while any living entity its base may damage is in
 * {@link #area} and strikes all of them once the cooldown passed.
 */
public record AreaWeapon(SoundEvent launchSound, Function<TurretBlockEntity, AABB> area, AreaStrike strike)
        implements TurretWeapon {
    public AreaWeapon {
        Objects.requireNonNull(launchSound, "launchSound");
        Objects.requireNonNull(area, "area");
        Objects.requireNonNull(strike, "strike");
    }

    @Override
    public boolean aims() {
        return false;
    }

    /** What a shot does. Energy and ammo are already consumed. */
    @FunctionalInterface
    public interface AreaStrike {
        /** @param victims the damageable living entities in the area, never empty */
        void strike(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, List<LivingEntity> victims);
    }
}
