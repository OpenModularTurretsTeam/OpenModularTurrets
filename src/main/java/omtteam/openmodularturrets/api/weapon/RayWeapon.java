package omtteam.openmodularturrets.api.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.core.turret.RayMath;
import omtteam.openmodularturrets.core.turret.RayProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Hits instantly along a ray (1.12.2 {@code RayTracingTurret}: laser, railgun, arc): one ray per scattershot level
 * towards the target's eyes, scattered by the turret's accuracy. The first damageable entity before the first solid
 * block takes {@link RayMath#damage}, split into normal and armor-piercing damage by the {@link RayProfile}.
 *
 * @param argb         colour of the drawn ray
 * @param duration     ticks the ray stays visible
 * @param lightning    drawn as a jagged lightning bolt instead of a straight beam
 * @param drawFromTip  a hit is drawn from the tip of the turret instead of the ray's start
 * @param hitSound     played at a hit entity, or null
 * @param normalDamage damage type of the normal part, null if the profile deals none
 * @param bypassDamage damage type of the armor-piercing part, null if the profile deals none
 * @param blockHit     called when the ray hits a block instead of an entity, or null
 * @param entityHit    called for every entity the ray damages, or null; see {@link Builder#onEntityHit}
 */
public record RayWeapon(SoundEvent launchSound, RayProfile profile, int argb, int duration, boolean lightning,
                        boolean drawFromTip, @Nullable SoundEvent hitSound,
                        @Nullable ResourceKey<DamageType> normalDamage, @Nullable ResourceKey<DamageType> bypassDamage,
                        @Nullable BlockHitHandler blockHit, @Nullable EntityHitHandler entityHit)
        implements TurretWeapon {
    public RayWeapon {
        Objects.requireNonNull(launchSound, "launchSound");
        Objects.requireNonNull(profile, "profile");
    }

    public static Builder builder(SoundEvent launchSound, RayProfile profile) {
        return new Builder(launchSound, profile);
    }

    /** Packs colour components (0–1) into {@code 0xAARRGGBB} (1.12.2 {@code ColorOM(r, g, b, a)}). */
    public static int argb(float r, float g, float b, float a) {
        return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    @Override
    public boolean aims() {
        return true;
    }

    /** Called when a ray hits a block (e.g. the railgun breaking it). */
    @FunctionalInterface
    public interface BlockHitHandler {
        void onBlockHit(ServerLevel level, BlockPos pos);
    }

    /** Called for an entity a ray damaged (e.g. to add an effect). */
    @FunctionalInterface
    public interface EntityHitHandler {
        void onEntityHit(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Entity hit);
    }

    public static final class Builder {
        private final SoundEvent launchSound;
        private final RayProfile profile;
        private int argb = 0xFFFFFFFF;
        private int duration = 3;
        private boolean lightning;
        private boolean drawFromTip;
        private SoundEvent hitSound;
        private ResourceKey<DamageType> normalDamage;
        private ResourceKey<DamageType> bypassDamage;
        private BlockHitHandler blockHit;
        private EntityHitHandler entityHit;

        private Builder(SoundEvent launchSound, RayProfile profile) {
            this.launchSound = launchSound;
            this.profile = profile;
        }

        /** Colour as {@code 0xAARRGGBB}, default opaque white. */
        public Builder color(int argb) {
            this.argb = argb;
            return this;
        }

        /** Ticks the ray stays visible, default 3. */
        public Builder duration(int ticks) {
            this.duration = ticks;
            return this;
        }

        public Builder lightning() {
            this.lightning = true;
            return this;
        }

        public Builder drawFromTip() {
            this.drawFromTip = true;
            return this;
        }

        public Builder hitSound(SoundEvent sound) {
            this.hitSound = sound;
            return this;
        }

        /** Damage types of the normal and the armor-piercing part; null for a part the profile does not deal. */
        public Builder damageTypes(@Nullable ResourceKey<DamageType> normal, @Nullable ResourceKey<DamageType> bypass) {
            this.normalDamage = normal;
            this.bypassDamage = bypass;
            return this;
        }

        public Builder onBlockHit(BlockHitHandler handler) {
            this.blockHit = handler;
            return this;
        }

        /**
         * Called for every entity a ray damages, after the damage: the entity it hit and, for arc chain profiles,
         * every entity the arc jumps to. Each ray of a scattershot counts on its own.
         */
        public Builder onEntityHit(EntityHitHandler handler) {
            this.entityHit = handler;
            return this;
        }

        /** @throws IllegalStateException if the profile deals a part without a damage type */
        public RayWeapon build() {
            if (profile.normalShare() > 0 && normalDamage == null || profile.bypassShare() > 0 && bypassDamage == null) {
                throw new IllegalStateException("Ray weapon needs a damage type for every part its profile deals");
            }
            return new RayWeapon(launchSound, profile, argb, duration, lightning, drawFromTip, hitSound, normalDamage,
                    bypassDamage, blockHit, entityHit);
        }
    }
}
