package omtteam.openmodularturrets.client.render.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.network.RayPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Draws turret rays (laser, railgun) and arcs (OMLib {@code Ray} render objects) as thin glowing beams for a few
 * ticks. Client-only visual state, cleared on disconnect.
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

    public static void init() {
        ClientNetworkCompat.registerHandler(OMTPackets.RAY, RayRenderer::add);
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> RAYS.clear());
        LevelRenderEvents.COLLECT_SUBMITS.register(RayRenderer::render);
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

    private static void tick() {
        Iterator<ActiveRay> iterator = RAYS.iterator();
        while (iterator.hasNext()) {
            ActiveRay ray = iterator.next();
            if (--ray.ticksLeft()[0] <= 0) iterator.remove();
        }
    }

    private static void render(LevelRenderContext context) {
        if (RAYS.isEmpty()) return;
        Vec3 camera = Minecraft.getInstance().gameRenderer.mainCamera().position();
        // drawn later in the frame: take a snapshot, the list changes every tick
        List<ActiveRay> rays = List.copyOf(RAYS);
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, consumer) -> {
            for (ActiveRay ray : rays) {
                for (int i = 0; i + 1 < ray.points().size(); i++) {
                    beam(consumer, pose, ray.points().get(i), ray.points().get(i + 1), ray.argb());
                }
            }
        });
        poseStack.popPose();
    }

    /** Two crossed quads along the segment, each drawn from both sides. */
    private static void beam(VertexConsumer consumer, PoseStack.Pose pose, Vec3 a, Vec3 b, int argb) {
        Vec3 direction = b.subtract(a).normalize();
        Vec3 helper = Math.abs(direction.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 side1 = direction.cross(helper).normalize().scale(HALF_WIDTH);
        Vec3 side2 = direction.cross(side1).normalize().scale(HALF_WIDTH);
        quad(consumer, pose, a, b, side1, argb);
        quad(consumer, pose, a, b, side2, argb);
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, Vec3 a, Vec3 b, Vec3 side, int argb) {
        Vec3[] corners = {a.add(side), b.add(side), b.subtract(side), a.subtract(side)};
        for (Vec3 corner : corners) {
            ClientCompat.vertex(consumer, pose, (float) corner.x, (float) corner.y, (float) corner.z, argb);
        }
        for (int i = corners.length - 1; i >= 0; i--) {
            ClientCompat.vertex(consumer, pose, (float) corners[i].x, (float) corners[i].y, (float) corners[i].z, argb);
        }
    }
}
