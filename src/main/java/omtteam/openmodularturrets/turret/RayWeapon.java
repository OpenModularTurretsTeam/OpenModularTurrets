package omtteam.openmodularturrets.turret;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import omtteam.omlib.api.render.ColorOM;
import omtteam.openmodularturrets.core.turret.RayMath;
import omtteam.openmodularturrets.core.turret.RayProfile;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Hits instantly along a ray (1.12.2 {@code RayTracingTurret}: laser, railgun, arc): one ray per scattershot level
 * towards the target's eyes, scattered by the turret's accuracy. The first damageable entity before the first solid
 * block takes {@link RayMath#damage}, split into normal and armor-piercing damage by the {@link RayProfile}.
 */
public final class RayWeapon implements TurretWeapon {
    private final SoundEvent launchSound;
    private final RayProfile profile;
    private final ColorOM color;
    private final int duration;
    private final boolean lightning;
    private final boolean drawFromTip;
    @Nullable
    private final SoundEvent hitSound;
    @Nullable
    private final BlockHitHandler blockHit;

    private RayWeapon(Builder builder) {
        this.launchSound = builder.launchSound;
        this.profile = builder.profile;
        this.color = builder.color;
        this.duration = builder.duration;
        this.lightning = builder.lightning;
        this.drawFromTip = builder.drawFromTip;
        this.hitSound = builder.hitSound;
        this.blockHit = builder.blockHit;
    }

    public static Builder builder(SoundEvent launchSound, RayProfile profile) {
        return new Builder(launchSound, profile);
    }

    @Override
    public SoundEvent launchSound() {
        return launchSound;
    }

    public RayProfile profile() {
        return profile;
    }

    /** Colour of the drawn ray. */
    public ColorOM color() {
        return color;
    }

    /** Ticks the ray stays visible. */
    public int duration() {
        return duration;
    }

    /** Drawn as a jagged lightning bolt instead of a straight beam. */
    public boolean lightning() {
        return lightning;
    }

    /** A hit is drawn from the tip of the turret instead of the ray's start. */
    public boolean drawFromTip() {
        return drawFromTip;
    }

    /** Played at a hit entity, or null. */
    @Nullable
    public SoundEvent hitSound() {
        return hitSound;
    }

    /** Called when the ray hits a block instead of an entity, or null. */
    @Nullable
    public BlockHitHandler blockHit() {
        return blockHit;
    }

    @Override
    public boolean aims() {
        return true;
    }

    /** Called when a ray hits a block (e.g. the railgun breaking it). */
    @FunctionalInterface
    public interface BlockHitHandler {
        void onBlockHit(WorldServer world, BlockPos pos);
    }

    public static final class Builder {
        private final SoundEvent launchSound;
        private final RayProfile profile;
        private ColorOM color = new ColorOM(1F, 1F, 1F, 1F);
        private int duration = 3;
        private boolean lightning;
        private boolean drawFromTip;
        private SoundEvent hitSound;
        private BlockHitHandler blockHit;

        private Builder(SoundEvent launchSound, RayProfile profile) {
            this.launchSound = Objects.requireNonNull(launchSound, "launchSound");
            this.profile = Objects.requireNonNull(profile, "profile");
        }

        /** Default opaque white. */
        public Builder color(ColorOM color) {
            this.color = color;
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

        public Builder onBlockHit(BlockHitHandler handler) {
            this.blockHit = handler;
            return this;
        }

        public RayWeapon build() {
            return new RayWeapon(this);
        }
    }
}
