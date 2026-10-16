package omtteam.openmodularturrets.client.render.fx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import omtteam.openmodularturrets.core.geometry.WavyRing;
import omtteam.openmodularturrets.core.world.Vector3;
import org.joml.Matrix4f;

/**
 * A glowing sphere circling on a {@link WavyRing}, leaving a trail that fades out behind it. Instances are immutable
 * and configured with {@link #builder(WavyRing)}; one instance can be shared by any number of renderers.
 */
public final class OrbitTrailRenderer {
    private final WavyRing ring;
    private final float centreX;
    private final float centreY;
    private final float centreZ;
    private final int rgb;
    private final float sphereRadius;
    private final float sphereAlpha;
    private final int sphereSlices;
    private final int sphereStacks;
    private final float trailHalfWidth;
    private final double trailArc;
    private final int trailSegments;
    private final float trailAlpha;
    private final double trailFalloff;

    private OrbitTrailRenderer(Builder builder) {
        this.ring = builder.ring;
        this.centreX = builder.centreX;
        this.centreY = builder.centreY;
        this.centreZ = builder.centreZ;
        this.rgb = builder.rgb;
        this.sphereRadius = builder.sphereRadius;
        this.sphereAlpha = builder.sphereAlpha;
        this.sphereSlices = builder.sphereSlices;
        this.sphereStacks = builder.sphereStacks;
        this.trailHalfWidth = builder.trailHalfWidth;
        this.trailArc = builder.trailArc;
        this.trailSegments = builder.trailSegments;
        this.trailAlpha = builder.trailAlpha;
        this.trailFalloff = builder.trailFalloff;
    }

    public static Builder builder(WavyRing ring) {
        return new Builder(ring);
    }

    /**
     * @param poseStack the frame the ring lies in (y = ring axis); the ring centre is offset by the configured centre
     * @param ticks     animation time in ticks, including the partial tick
     */
    public void render(PoseStack poseStack, MultiBufferSource buffers, double ticks) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();
        double head = ring.orbitAngle(ticks);
        if (trailSegments > 0 && trailAlpha > 0) {
            drawTrail(consumer, pose, head);
        }
        Vector3 centre = ring.point(head);
        GlowGeometry.sphere(consumer, pose, centreX + (float) centre.x(), centreY + (float) centre.y(),
                centreZ + (float) centre.z(), sphereRadius, sphereSlices, sphereStacks, GlowGeometry.argb(rgb, sphereAlpha));
    }

    private void drawTrail(VertexConsumer consumer, Matrix4f pose, double head) {
        for (int i = 0; i < trailSegments; i++) {
            double from = head - trailArc * i / trailSegments;
            double to = head - trailArc * (i + 1) / trailSegments;
            int colourFrom = trailColour((double) i / trailSegments);
            int colourTo = trailColour((double) (i + 1) / trailSegments);
            segment(consumer, pose, from, to, colourFrom, colourTo);
        }
    }

    /**
     * Trail colour at {@code progress} (0 = at the sphere, 1 = trail end): the alpha decays quadratically, to
     * {@code (1 - falloff)²} of the start alpha at the end.
     */
    private int trailColour(double progress) {
        double fade = 1.0D - progress * trailFalloff;
        return GlowGeometry.argb(rgb, (float) (trailAlpha * fade * fade));
    }

    /** A ribbon segment of the trail: one flat quad along the ring and one standing up, each visible from both sides. */
    private void segment(VertexConsumer consumer, Matrix4f pose, double from, double to, int colourFrom, int colourTo) {
        Vector3 a = ring.point(from);
        Vector3 b = ring.point(to);
        // flat ribbon: sideways = radial direction, standing ribbon: sideways = up
        float[] radialA = {(float) Math.cos(from) * trailHalfWidth, 0, (float) Math.sin(from) * trailHalfWidth};
        float[] radialB = {(float) Math.cos(to) * trailHalfWidth, 0, (float) Math.sin(to) * trailHalfWidth};
        float[] up = {0, trailHalfWidth, 0};
        int[] colours = {colourFrom, colourTo, colourTo, colourFrom};
        GlowGeometry.doubleSidedQuad(consumer, pose, ribbon(a, b, radialA, radialB), colours);
        GlowGeometry.doubleSidedQuad(consumer, pose, ribbon(a, b, up, up), colours);
    }

    private float[][] ribbon(Vector3 a, Vector3 b, float[] sideA, float[] sideB) {
        return new float[][]{corner(a, sideA, 1), corner(b, sideB, 1), corner(b, sideB, -1), corner(a, sideA, -1)};
    }

    private float[] corner(Vector3 point, float[] side, int sign) {
        return new float[]{centreX + (float) point.x() + sign * side[0], centreY + (float) point.y() + sign * side[1],
                centreZ + (float) point.z() + sign * side[2]};
    }

    /** Configuration of an {@link OrbitTrailRenderer}; the defaults draw a small red sphere with a full-circle trail. */
    public static final class Builder {
        private final WavyRing ring;
        private float centreX;
        private float centreY;
        private float centreZ;
        private int rgb = 0xCC0808;
        private float sphereRadius = 0.03F;
        private float sphereAlpha = 0.67F;
        private int sphereSlices = 8;
        private int sphereStacks = 6;
        private float trailHalfWidth = 0.018F;
        private double trailArc = 2.0D * Math.PI;
        private int trailSegments = 48;
        private float trailAlpha = 0.4F;
        private double trailFalloff = 0.5D;

        private Builder(WavyRing ring) {
            this.ring = ring;
        }

        /** Offset of the ring centre from the pose origin. */
        public Builder centre(float x, float y, float z) {
            this.centreX = x;
            this.centreY = y;
            this.centreZ = z;
            return this;
        }

        /** RGB colour of the sphere and the trail. */
        public Builder colour(int rgb) {
            this.rgb = rgb;
            return this;
        }

        public Builder sphere(float radius, float alpha) {
            this.sphereRadius = radius;
            this.sphereAlpha = alpha;
            return this;
        }

        /** Tessellation of the sphere (longitude segments, latitude bands). */
        public Builder sphereDetail(int slices, int stacks) {
            this.sphereSlices = Math.max(3, slices);
            this.sphereStacks = Math.max(2, stacks);
            return this;
        }

        /**
         * @param halfWidth half the width of the trail ribbons
         * @param arc       part of the ring the trail covers, in radians (2π = the whole ring)
         * @param segments  number of segments the trail is drawn with (0 = no trail)
         */
        public Builder trail(float halfWidth, double arc, int segments) {
            this.trailHalfWidth = halfWidth;
            this.trailArc = arc;
            this.trailSegments = Math.max(0, segments);
            return this;
        }

        /**
         * @param alpha   alpha right behind the sphere
         * @param falloff how far the trail fades by its end: 0 = not at all, 1 = completely
         */
        public Builder trailFade(float alpha, double falloff) {
            this.trailAlpha = alpha;
            this.trailFalloff = Math.max(0.0D, Math.min(1.0D, falloff));
            return this;
        }

        public OrbitTrailRenderer build() {
            return new OrbitTrailRenderer(this);
        }
    }
}
