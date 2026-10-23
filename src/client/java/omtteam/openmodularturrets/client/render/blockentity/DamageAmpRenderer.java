package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import omtteam.openmodularturrets.client.render.fx.GlowPanelsRenderer;

/**
 * Damage amp effect on turrets: a small red LED in each upper corner of the turret's baseplate, lighting the top
 * face and both side faces at the corner.
 */
public final class DamageAmpRenderer {
    /** Top of the baseplate in the turret's local frame (the plate is 12×12×1 pixels at the bottom of the block). */
    private static final float PLATE_TOP = -0.4375F;
    /** Half side of the baseplate (6 pixels). */
    private static final float PLATE_HALF_SIZE = 0.375F;
    /** LED edge length, under the plate's thickness of one pixel. */
    private static final float LED_SIZE = 0.04F;

    private static final GlowPanelsRenderer LEDS = GlowPanelsRenderer.builder()
            .panels(GlowPanelsRenderer.boxTopCorners(PLATE_HALF_SIZE, PLATE_TOP, LED_SIZE))
            .colour(0xFF1A1A)
            .alpha(0.85F)
            .build();

    private DamageAmpRenderer() {
    }

    /** @param poseStack at the block center, in the turret's local frame (x = right, y = up, z = forward) */
    public static void render(PoseStack poseStack, MultiBufferSource buffers) {
        LEDS.render(poseStack, buffers);
    }
}
