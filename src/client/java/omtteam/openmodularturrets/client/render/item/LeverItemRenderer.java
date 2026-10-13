package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * The crank item shows the crank model. Used by the {@code openmodularturrets:lever} special item model; the display
 * transforms are in the base item model JSON.
 */
public class LeverItemRenderer implements NoDataSpecialModelRenderer {
    private final LeverRenderer renderer;

    public LeverItemRenderer(LeverRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay,
                       boolean hasFoil, int outlineColor) {
        renderer.submit(Direction.SOUTH, 0F, poseStack, collector, packedLight);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        TurretItemRenderer.ItemExtents.fullBlock(output);
    }

    /** JSON: {@code {"type": "openmodularturrets:lever"}}. */
    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public LeverItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new LeverItemRenderer(new LeverRenderer(context.entityModelSet().bakeLayer(LeverRenderer.LAYER)));
        }
    }
}
