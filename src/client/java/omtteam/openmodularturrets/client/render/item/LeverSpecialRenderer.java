package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * The crank item shows the crank model; display transforms are in the item model JSON ({@code base} of a
 * {@code minecraft:special} model). The variant for 1.21.11; an item model names it as
 * {@code openmodularturrets:lever}.
 */
public final class LeverSpecialRenderer implements SpecialModelRenderer<Void> {
    private final LeverRenderer.LeverModel model;

    private LeverSpecialRenderer(LeverRenderer.LeverModel model) {
        this.model = model;
    }

    @Override
    public void submit(@Nullable Void argument, ItemDisplayContext context, PoseStack poseStack,
                       SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean glint, int outline) {
        LeverRenderer.submit(model, Direction.SOUTH, 0F, poseStack, collector, packedLight, packedOverlay, null);
    }

    /** The block's unit cube, as for a block item. */
    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0));
        output.accept(new Vector3f(1, 1, 1));
    }

    @Override
    public @Nullable Void extractArgument(ItemStack stack) {
        return null;
    }

    /** The item model's type; it takes no arguments. */
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new LeverSpecialRenderer(new LeverRenderer.LeverModel(context.entityModelSet().bakeLayer(LeverRenderer.LAYER)));
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
