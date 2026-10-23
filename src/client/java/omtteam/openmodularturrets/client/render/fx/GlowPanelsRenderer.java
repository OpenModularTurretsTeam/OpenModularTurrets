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
 * Small, uniformly lit panels on surfaces, like LEDs. Instances are immutable and configured with {@link #builder()};
 * one instance can be shared by any number of renderers.
 */
public final class GlowPanelsRenderer {
    /**
     * A parallelogram panel on a surface: {@code anchor}, {@code anchor + edgeA}, {@code anchor + edgeA + edgeB},
     * {@code anchor + edgeB}. {@code outward} is the surface's unit
     * normal; the panel is lifted off the surface along it so the two do not z-fight. Both edges must point away from
     * the anchor along the surface: the lifted panel is extended back past the anchor by the same distance, so panels
     * on adjacent faces sharing an anchor (like {@link #boxTopCorners}) still meet at the corner without a gap.
     */
    public record Panel(Vector3f anchor, Vector3f edgeA, Vector3f edgeB, Vector3f outward) {
    }

    private final List<Panel> panels;
    private final int color;
    private final float alpha;
    private final float surfaceOffset;

    private GlowPanelsRenderer(Builder builder) {
        this.panels = List.copyOf(builder.panels);
        this.color = builder.rgb;
        this.alpha = builder.alpha;
        this.surfaceOffset = builder.surfaceOffset;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Three panels on each of the four upper corners of a box centred on the y axis: one on the top face and one on
     * each of the two side faces, all anchored at the corner itself (so they read as one lit corner). The panels lie
     * exactly on the faces; the renderer lifts them off.
     *
     * @param halfSize half the side length of the box's top face
     * @param topY     height of the box's top face
     * @param size     edge length of each panel
     */
    public static List<Panel> boxTopCorners(float halfSize, float topY, float size) {
        List<Panel> panels = new ArrayList<>();
        Vector3f up = new Vector3f(0, 1, 0);
        for (int sx = -1; sx <= 1; sx += 2) {
            for (int sz = -1; sz <= 1; sz += 2) {
                Vector3f anchor = new Vector3f(sx * halfSize, topY, sz * halfSize);
                Vector3f inX = new Vector3f(-sx * size, 0, 0);
                Vector3f inZ = new Vector3f(0, 0, -sz * size);
                Vector3f down = new Vector3f(0, -size, 0);
                panels.add(new Panel(anchor, inX, inZ, up));
                panels.add(new Panel(anchor, down, inZ, new Vector3f(sx, 0, 0)));
                panels.add(new Panel(anchor, inX, down, new Vector3f(0, 0, sz)));
            }
        }
        return panels;
    }

    /** @param poseStack the frame the panels are given in */
    public void render(PoseStack poseStack, MultiBufferSource buffers) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();
        int lit = GlowGeometry.argb(color, alpha);
        int[] colours = {lit, lit, lit, lit};
        for (Panel panel : panels) {
            GlowGeometry.doubleSidedQuad(consumer, pose, corners(panel, surfaceOffset), colours);
        }
    }

    /**
     * Corners of the panel lifted by {@code offset}. The anchor moves back by
     * {@code offset} along both edges, onto the corner where the lifted panels of adjacent faces meet.
     */
    private static float[][] corners(Panel panel, float offset) {
        Vector3f backA = new Vector3f(panel.edgeA()).normalize(offset);
        Vector3f backB = new Vector3f(panel.edgeB()).normalize(offset);
        Vector3f o = new Vector3f(panel.outward()).mul(offset).add(panel.anchor()).sub(backA).sub(backB);
        Vector3f a = new Vector3f(panel.edgeA()).add(backA);
        Vector3f b = new Vector3f(panel.edgeB()).add(backB);
        return new float[][]{
                {o.x(), o.y(), o.z()},
                {o.x() + a.x(), o.y() + a.y(), o.z() + a.z()},
                {o.x() + a.x() + b.x(), o.y() + a.y() + b.y(), o.z() + a.z() + b.z()},
                {o.x() + b.x(), o.y() + b.y(), o.z() + b.z()}};
    }

    /** Configuration of a {@link GlowPanelsRenderer}; the defaults draw red panels. */
    public static final class Builder {
        private final List<Panel> panels = new ArrayList<>();
        private int rgb = 0xCC0000;
        private float alpha = 0.85F;
        private float surfaceOffset = 0.0001F;

        private Builder() {
        }

        public Builder panels(List<Panel> panels) {
            this.panels.addAll(panels);
            return this;
        }

        /** RGB colour of the panels. */
        public Builder colour(int rgb) {
            this.rgb = rgb;
            return this;
        }

        /** Alpha of the panels. */
        public Builder alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }


        public GlowPanelsRenderer build() {
            return new GlowPanelsRenderer(this);
        }
    }
}
