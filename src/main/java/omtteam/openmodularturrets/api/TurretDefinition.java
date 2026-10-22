package omtteam.openmodularturrets.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * Everything Open Modular Turrets needs to know about a turret (1.20.1+ {@code api.TurretDefinition}).
 */
public final class TurretDefinition {
    private final TurretType type;
    private final TurretWeapon weapon;
    private final TurretStats defaultStats;
    @Nullable
    private final AmbientEffect ambientEffect;

    /**
     * @param type         id, base tier, ammo and targeting rules
     * @param weapon       what a shot does
     * @param defaultStats default values of the turret's config entry; for turrets of other mods they are written to
     *                     {@code config/openmodularturrets_addon_turrets.cfg}
     */
    public TurretDefinition(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
        this(type, weapon, defaultStats, null);
    }

    private TurretDefinition(TurretType type, TurretWeapon weapon, TurretStats defaultStats,
                             @Nullable AmbientEffect ambientEffect) {
        this.type = Objects.requireNonNull(type, "type");
        this.weapon = Objects.requireNonNull(weapon, "weapon");
        this.defaultStats = Objects.requireNonNull(defaultStats, "defaultStats");
        this.ambientEffect = ambientEffect;
    }

    /** For the optional parts of a definition (the {@link AmbientEffect}). */
    public static Builder builder(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
        return new Builder(type, weapon, defaultStats);
    }

    public TurretType type() {
        return type;
    }

    public TurretWeapon weapon() {
        return weapon;
    }

    public TurretStats defaultStats() {
        return defaultStats;
    }

    /** Particles around the placed turret on the client, or null. */
    @Nullable
    public AmbientEffect ambientEffect() {
        return ambientEffect;
    }

    /**
     * Client-side particles of a placed turret (3.x {@code randomDisplayTick} of the turret blocks), called from the
     * turret block's {@code randomDisplayTick}; spawn them with {@code world.spawnParticle}.
     */
    @FunctionalInterface
    public interface AmbientEffect {
        void spawn(World world, BlockPos pos, Random random);
    }

    public static final class Builder {
        private final TurretType type;
        private final TurretWeapon weapon;
        private final TurretStats defaultStats;
        @Nullable
        private AmbientEffect ambientEffect;

        private Builder(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
            this.type = type;
            this.weapon = weapon;
            this.defaultStats = defaultStats;
        }

        public Builder ambientEffect(AmbientEffect effect) {
            this.ambientEffect = Objects.requireNonNull(effect, "effect");
            return this;
        }

        public TurretDefinition build() {
            return new TurretDefinition(type, weapon, defaultStats, ambientEffect);
        }
    }
}
