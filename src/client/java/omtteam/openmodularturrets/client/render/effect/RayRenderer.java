package omtteam.openmodularturrets.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.network.RayPacket;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Draws turret rays (laser, railgun) and arcs (OMLib {@code Ray} render objects) as thin glowing beams for a few
 * ticks. Client-only visual state, cleared on disconnect. The loader client entrypoints call {@link #tick},
 * {@link #clear} and {@link #render}.
 */
public final class RayRenderer {
    private static final float HALF_WIDTH = 0.025F;
    /** Lightning: segment length and maximum sideways offset. */
    private static final double BOLT_SEGMENT = 0.75D;
    private static final double BOLT_JITTER = 0.25D;

    private static final List<ActiveRay> RAYS = new ArrayList<>();

    private record ActiveRay(List<Vec3> points, int argb, int[] ticksLeft) {
    }

    private RayRenderer() {
    }

    /** Registers the ray packet handler. */
    public static void init() {
        ClientNetworkCompat.registerHandler(OMTPackets.RAY, RayRenderer::add);
    }

    /** Removes all rays (on disconnect). */
    public static void clear() {
        RAYS.clear();
    }

    private static void add(RayPacket packet) {
        List<Vec3> points = packet.lightning() ? bolt(packet.from(), packet.to()) : List.of(packet.from(), packet.to());
        RAYS.add(new ActiveRay(points, packet.argb(), new int[]{packet.duration()}));
    }

    /** A jagged line between two points (fixed per ray, so it does not flicker). */
    private static List<Vec3> bolt(Vec3 from, Vec3 to) {
        Random random = new Random();
        int segments = Math.max(2, (int) Math.ceil(from.distanceTo(to) / BOLT_SEGMENT));
        List<Vec3> points = new ArrayList<>();
        points.add(from);
        for (int i = 1; i < segments; i++) {
            Vec3 point = from.lerp(to, (double) i / segments);
            points.add(point.add((random.nextDouble() - 0.5) * 2 * BOLT_JITTER, (random.nextDouble() - 0.5) * 2 * BOLT_JITTER,
                    (random.nextDouble() - 0.5) * 2 * BOLT_JITTER));
        }
        points.add(to);
        return points;
    }

    /** Ages the rays; called at the end of every client tick. */
    public static void tick() {
        RAYS.removeIf(ray -> --ray.ticksLeft()[0] <= 0);
    }

    /** Draws the rays; called after the translucent blocks, with the world's pose stack and the camera position. */
    public static void render(PoseStack poseStack, Vec3 camera) {
        if (RAYS.isEmpty() || poseStack == null) return;
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = buffers.getBuffer(ClientCompat.lightning());
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        Matrix4f pose = poseStack.last().pose();
        for (ActiveRay ray : RAYS) {
            for (int i = 0; i + 1 < ray.points().size(); i++) {
                beam(consumer, pose, ray.points().get(i), ray.points().get(i + 1), ray.argb());
            }
        }
        poseStack.popPose();
        buffers.endBatch(ClientCompat.lightning());
    }

    /** Two crossed quads along the segment, each drawn from both sides. */
    private static void beam(VertexConsumer consumer, Matrix4f pose, Vec3 a, Vec3 b, int argb) {
        Vec3 direction = b.subtract(a).normalize();
        Vec3 helper = Math.abs(direction.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 side1 = direction.cross(helper).normalize().scale(HALF_WIDTH);
        Vec3 side2 = direction.cross(side1).normalize().scale(HALF_WIDTH);
        quad(consumer, pose, a, b, side1, argb);
        quad(consumer, pose, a, b, side2, argb);
    }

    private static void quad(VertexConsumer consumer, Matrix4f pose, Vec3 a, Vec3 b, Vec3 side, int argb) {
        Vec3[] corners = {a.add(side), b.add(side), b.subtract(side), a.subtract(side)};
        for (Vec3 corner : corners) {
            ClientCompat.vertex(consumer, pose, (float) corner.x, (float) corner.y, (float) corner.z, argb);
        }
        for (int i = corners.length - 1; i >= 0; i--) {
            ClientCompat.vertex(consumer, pose, (float) corners[i].x, (float) corners[i].y, (float) corners[i].z, argb);
        }
    }
}
