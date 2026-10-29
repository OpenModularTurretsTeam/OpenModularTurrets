package omtteam.openmodularturrets.api;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Everything Open Modular Turrets needs to know about a turret.
 *
 * @param type          id, base tier, ammo and targeting rules
 * @param weapon        what a shot does
 * @param defaultStats  default values of the turret's config entry; for turrets of other mods it is written to
 *                      {@code turrets.addon_turrets."<id>"} in {@code config/openmodularturrets.json}
 * @param ambientEffect particles of a placed turret, or null; set it with {@link #builder}
 */
public record TurretDefinition(TurretType type, TurretWeapon weapon, TurretStats defaultStats,
                               @Nullable AmbientEffect ambientEffect) {
    public TurretDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(weapon, "weapon");
        Objects.requireNonNull(defaultStats, "defaultStats");
    }

    /** Without an ambient effect (existing addons). */
    public TurretDefinition(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
        this(type, weapon, defaultStats, null);
    }

    /** For the optional parts of a definition (the {@link AmbientEffect}). */
    public static Builder builder(TurretType type, TurretWeapon weapon, TurretStats defaultStats) {
        return new Builder(type, weapon, defaultStats);
    }

    /**
     * Client-side particles of a placed turret (1.12.2 {@code randomDisplayTick} of the turret blocks), called from
     * the turret block's {@code animateTick}, also while the turret is concealed; spawn them with
     * {@code level.addParticle}.
     */
    @FunctionalInterface
    public interface AmbientEffect {
        void spawn(Level level, BlockPos pos, RandomSource random);
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
