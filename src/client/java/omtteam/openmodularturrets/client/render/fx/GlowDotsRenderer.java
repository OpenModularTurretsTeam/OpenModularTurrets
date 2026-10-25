package omtteam.openmodularturrets.client.render.fx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Small glowing lights at fixed points: a bright core sphere inside an optional faint halo, optionally pulsing.
 * Instances are immutable and configured with {@link #builder()}; one instance can be shared by any number of
 * renderers.
 */
public final class GlowDotsRenderer {
    private final List<Vector3f> points;
    private final int rgb;
    private final float coreRadius;
    private final float coreAlpha;
    private final float haloRadius;
    private final float haloAlpha;
    private final int slices;
    private final int stacks;
    private final double pulseTicks;
    private final float pulseMin;

    private GlowDotsRenderer(Builder builder) {
        this.points = List.copyOf(builder.points);
        this.rgb = builder.rgb;
        this.coreRadius = builder.coreRadius;
        this.coreAlpha = builder.coreAlpha;
        this.haloRadius = builder.haloRadius;
        this.haloAlpha = builder.haloAlpha;
        this.slices = builder.slices;
        this.stacks = builder.stacks;
        this.pulseTicks = builder.pulseTicks;
        this.pulseMin = builder.pulseMin;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** The four corners of a square of half side length {@code halfSize}, centred on the y axis at height {@code y}. */
    public static List<Vector3f> squareCorners(float halfSize, float y) {
        return List.of(new Vector3f(-halfSize, y, -halfSize), new Vector3f(halfSize, y, -halfSize),
                new Vector3f(halfSize, y, halfSize), new Vector3f(-halfSize, y, halfSize));
    }

    /**
     * @param poseStack the frame the points are given in
     * @param ticks     animation time in ticks, including the partial tick (only used when pulsing)
     */
    public void render(PoseStack poseStack, MultiBufferSource buffers, double ticks) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();
        float brightness = brightness(ticks);
        int core = GlowGeometry.argb(rgb, coreAlpha * brightness);
        int halo = GlowGeometry.argb(rgb, haloAlpha * brightness);
        for (Vector3f point : points) {
            GlowGeometry.sphere(consumer, pose, point.x(), point.y(), point.z(), coreRadius, slices, stacks, core);
            if (haloRadius > coreRadius && haloAlpha > 0) {
                GlowGeometry.sphere(consumer, pose, point.x(), point.y(), point.z(), haloRadius, slices, stacks, halo);
            }
        }
    }

    /** Brightness factor: 1 without pulsing, otherwise a sine between {@code pulseMin} and 1. */
    private float brightness(double ticks) {
        if (pulseTicks <= 0) return 1.0F;
        double wave = 0.5D + 0.5D * Math.sin(2.0D * Math.PI * (ticks % pulseTicks) / pulseTicks);
        return (float) (pulseMin + (1.0D - pulseMin) * wave);
    }

    /** Configuration of a {@link GlowDotsRenderer}; the defaults draw steady small red dots without a halo. */
    public static final class Builder {
        private final List<Vector3f> points = new ArrayList<>();
        private int rgb = 0xFF1A1A;
        private float coreRadius = 0.02F;
        private float coreAlpha = 0.9F;
        private float haloRadius;
        private float haloAlpha;
        private int slices = 8;
        private int stacks = 6;
        private double pulseTicks;
        private float pulseMin = 1.0F;

        private Builder() {
        }

        public Builder point(float x, float y, float z) {
            points.add(new Vector3f(x, y, z));
            return this;
        }

        public Builder points(List<Vector3f> points) {
            points.forEach(point -> this.points.add(new Vector3f(point)));
            return this;
        }

        /** RGB colour of the lights. */
        public Builder colour(int rgb) {
            this.rgb = rgb;
            return this;
        }

        public Builder core(float radius, float alpha) {
            this.coreRadius = radius;
            this.coreAlpha = alpha;
            return this;
        }

        /** A larger, fainter sphere around each core (radius not above the core radius = no halo). */
        public Builder halo(float radius, float alpha) {
            this.haloRadius = radius;
            this.haloAlpha = alpha;
            return this;
        }

        /** Tessellation of the spheres (longitude segments, latitude bands). */
        public Builder detail(int slices, int stacks) {
            this.slices = Math.max(3, slices);
            this.stacks = Math.max(2, stacks);
            return this;
        }

        /**
         * @param periodTicks    ticks of one pulse (0 = steady light)
         * @param minBrightness  brightness factor at the dimmest point of a pulse, in [0, 1]
         */
        public Builder pulse(double periodTicks, float minBrightness) {
            this.pulseTicks = Math.max(0.0D, periodTicks);
            this.pulseMin = Math.max(0.0F, Math.min(1.0F, minBrightness));
            return this;
        }

        public GlowDotsRenderer build() {
            return new GlowDotsRenderer(this);
        }
    }
}
