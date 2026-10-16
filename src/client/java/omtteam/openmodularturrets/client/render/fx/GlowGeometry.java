package omtteam.openmodularturrets.client.render.fx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import org.joml.Matrix4f;

/**
 * Untextured shapes for glow effects, written as position + colour quads (for buffers like
 * {@code RenderType.lightning()}).
 */
public final class GlowGeometry {
    private GlowGeometry() {
    }

    /** Packs an RGB colour and an alpha in [0, 1] into ARGB. */
    public static int argb(int rgb, float alpha) {
        int a = Math.round(255.0F * Math.max(0.0F, Math.min(1.0F, alpha)));
        return (a << 24) | (rgb & 0xFFFFFF);
    }

    /** A UV sphere of {@code stacks} latitude bands and {@code slices} longitude segments, facing outwards. */
    public static void sphere(VertexConsumer consumer, Matrix4f pose, float cx, float cy, float cz, float radius,
                              int slices, int stacks, int argb) {
        for (int stack = 0; stack < stacks; stack++) {
            double lat0 = Math.PI * stack / stacks - Math.PI / 2;
            double lat1 = Math.PI * (stack + 1) / stacks - Math.PI / 2;
            for (int slice = 0; slice < slices; slice++) {
                double lon0 = 2 * Math.PI * slice / slices;
                double lon1 = 2 * Math.PI * (slice + 1) / slices;
                spherePoint(consumer, pose, cx, cy, cz, radius, lat0, lon0, argb);
                spherePoint(consumer, pose, cx, cy, cz, radius, lat0, lon1, argb);
                spherePoint(consumer, pose, cx, cy, cz, radius, lat1, lon1, argb);
                spherePoint(consumer, pose, cx, cy, cz, radius, lat1, lon0, argb);
            }
        }
    }

    private static void spherePoint(VertexConsumer consumer, Matrix4f pose, float cx, float cy, float cz, float radius,
                                    double lat, double lon, int argb) {
        float x = cx + radius * (float) (Math.cos(lat) * Math.cos(lon));
        float y = cy + radius * (float) Math.sin(lat);
        float z = cz + radius * (float) (Math.cos(lat) * Math.sin(lon));
        ClientCompat.vertex(consumer, pose, x, y, z, argb);
    }

    /** A quad visible from both sides; {@code corners} are four {x, y, z} points in order, with one colour each. */
    public static void doubleSidedQuad(VertexConsumer consumer, Matrix4f pose, float[][] corners, int[] argb) {
        for (int i = 0; i < 4; i++) {
            ClientCompat.vertex(consumer, pose, corners[i][0], corners[i][1], corners[i][2], argb[i]);
        }
        for (int i = 3; i >= 0; i--) {
            ClientCompat.vertex(consumer, pose, corners[i][0], corners[i][1], corners[i][2], argb[i]);
        }
    }
}
