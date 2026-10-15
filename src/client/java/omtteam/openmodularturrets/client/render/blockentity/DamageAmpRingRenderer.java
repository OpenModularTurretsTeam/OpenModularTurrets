package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.core.turret.DamageAmpRing;
import omtteam.openmodularturrets.core.world.Vector3;
import org.joml.Matrix4f;

/**
 * Draws the damage amp ring ({@link DamageAmpRing}): a small red sphere circling on a wavy ring above the turret's
 * baseplate and leaving a red trail that fades out behind it.
 */
public final class DamageAmpRingRenderer {
    /** Local height of the ring centre: just above the baseplate, whose top is at -0.4375 in the turret's frame. */
    private static final float CENTRE_Y = -0.36F;
    private static final float SPHERE_RADIUS = 0.030F;
    private static final float TRAIL_HALF_WIDTH = 0.018F;
    /**
     * Part of the revolution the trail covers, and the number of segments it is drawn with. The whole ring: the trail
     * does not fade out completely anymore, so it has to reach the sphere again from the front.
     */
    private static final double TRAIL_ARC = 2.0D * Math.PI;
    private static final int TRAIL_SEGMENTS = 48;
    /** Alpha of the trail right behind the sphere; it decays quadratically to a quarter of that at the end. */
    private static final float TRAIL_ALPHA = 0.40F;
    private static final int SPHERE_SLICES = 8;
    private static final int SPHERE_STACKS = 6;
    private static final int RED = 0xCC0808;

    private DamageAmpRingRenderer() {
    }

    /**
     * @param poseStack at the block center, in the turret's local frame (x = right, y = up, z = forward)
     * @param ticks     animation time in ticks, including the partial tick
     */
    public static void render(PoseStack poseStack, MultiBufferSource buffers, double ticks) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.lightning());
        Matrix4f pose = poseStack.last().pose();
        double head = DamageAmpRing.sphereAngle(ticks);
        drawTrail(consumer, pose, head);
        drawSphere(consumer, pose, DamageAmpRing.point(head));
    }

    private static void drawTrail(VertexConsumer consumer, Matrix4f pose, double head) {
        for (int i = 0; i < TRAIL_SEGMENTS; i++) {
            double from = head - TRAIL_ARC * i / TRAIL_SEGMENTS;
            double to = head - TRAIL_ARC * (i + 1) / TRAIL_SEGMENTS;
            int alphaFrom = trailAlpha((double) i / TRAIL_SEGMENTS);
            int alphaTo = trailAlpha((double) (i + 1) / TRAIL_SEGMENTS);
            segment(consumer, pose, from, to, alphaFrom, alphaTo);
        }
    }

    private static int trailAlpha(double progress) {
        double fade = 1.0D - progress / 2;
        return (int) Math.round(255.0D * TRAIL_ALPHA * fade * fade);
    }

    /** A ribbon segment of the trail: one flat quad along the ring and one standing up, each visible from both sides. */
    private static void segment(VertexConsumer consumer, Matrix4f pose, double from, double to, int alphaFrom, int alphaTo) {
        Vector3 a = DamageAmpRing.point(from);
        Vector3 b = DamageAmpRing.point(to);
        // flat ribbon: sideways = radial direction, standing ribbon: sideways = up
        float[] radialA = {(float) Math.cos(from) * TRAIL_HALF_WIDTH, 0, (float) Math.sin(from) * TRAIL_HALF_WIDTH};
        float[] radialB = {(float) Math.cos(to) * TRAIL_HALF_WIDTH, 0, (float) Math.sin(to) * TRAIL_HALF_WIDTH};
        float[] up = {0, TRAIL_HALF_WIDTH, 0};
        quad(consumer, pose, a, b, radialA, radialB, alphaFrom, alphaTo);
        quad(consumer, pose, a, b, up, up, alphaFrom, alphaTo);
    }

    private static void quad(VertexConsumer consumer, Matrix4f pose, Vector3 a, Vector3 b, float[] sideA, float[] sideB,
                             int alphaA, int alphaB) {
        float[][] corners = {
                corner(a, sideA, 1), corner(b, sideB, 1), corner(b, sideB, -1), corner(a, sideA, -1)};
        int[] alphas = {alphaA, alphaB, alphaB, alphaA};
        for (int i = 0; i < 4; i++) {
            vertex(consumer, pose, corners[i], alphas[i]);
        }
        for (int i = 3; i >= 0; i--) {
            vertex(consumer, pose, corners[i], alphas[i]);
        }
    }

    private static float[] corner(Vector3 centre, float[] side, int sign) {
        return new float[]{(float) centre.x() + sign * side[0], CENTRE_Y + (float) centre.y() + sign * side[1],
                (float) centre.z() + sign * side[2]};
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, float[] position, int alpha) {
        ClientCompat.vertex(consumer, pose, position[0], position[1], position[2], (alpha << 24) | RED);
    }

    /** A small UV sphere, drawn a bit more opaque than the trail. */
    private static void drawSphere(VertexConsumer consumer, Matrix4f pose, Vector3 centre) {
        float cx = (float) centre.x();
        float cy = CENTRE_Y + (float) centre.y();
        float cz = (float) centre.z();
        int argb = 0xAA000000 | RED;
        for (int stack = 0; stack < SPHERE_STACKS; stack++) {
            double lat0 = Math.PI * stack / SPHERE_STACKS - Math.PI / 2;
            double lat1 = Math.PI * (stack + 1) / SPHERE_STACKS - Math.PI / 2;
            for (int slice = 0; slice < SPHERE_SLICES; slice++) {
                double lon0 = 2 * Math.PI * slice / SPHERE_SLICES;
                double lon1 = 2 * Math.PI * (slice + 1) / SPHERE_SLICES;
                spherePoint(consumer, pose, cx, cy, cz, lat0, lon0, argb);
                spherePoint(consumer, pose, cx, cy, cz, lat0, lon1, argb);
                spherePoint(consumer, pose, cx, cy, cz, lat1, lon1, argb);
                spherePoint(consumer, pose, cx, cy, cz, lat1, lon0, argb);
            }
        }
    }

    private static void spherePoint(VertexConsumer consumer, Matrix4f pose, float cx, float cy, float cz, double lat,
                                    double lon, int argb) {
        float x = cx + SPHERE_RADIUS * (float) (Math.cos(lat) * Math.cos(lon));
        float y = cy + SPHERE_RADIUS * (float) Math.sin(lat);
        float z = cz + SPHERE_RADIUS * (float) (Math.cos(lat) * Math.sin(lon));
        ClientCompat.vertex(consumer, pose, x, y, z, argb);
    }
}
