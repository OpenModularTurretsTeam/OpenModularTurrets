package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Draws the model of a turret as its item (1.12.2: turret renderer with {@code turretHead == null}); the display
 * transforms are in the item model JSON ({@code base} of a {@code minecraft:special} model). The variant for 1.21.11,
 * where items are drawn by special model renderers instead of {@code builtin/entity} renderers. An item model names it
 * as {@code openmodularturrets:turret} with the turret's id: {@code "turret": "openmodularturrets:laser_turret"}.
 */
public final class TurretSpecialRenderer implements SpecialModelRenderer<Void> {
    private final TurretModel model;

    private TurretSpecialRenderer(TurretModel model) {
        this.model = model;
    }

    @Override
    public void submit(@Nullable Void argument, ItemDisplayContext context, PoseStack poseStack,
                       SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean glint, int outline) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        TurretRenderer.submitModel(model, TurretModel.Pose.ITEM, model.renderTypeFor(TurretModel.Pose.ITEM), poseStack,
                collector, packedLight, packedOverlay, null);
        poseStack.popPose();
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

    /** The item model's type: which turret to draw. */
    public record Unbaked(TurretType turret) implements SpecialModelRenderer.Unbaked {
        private static final Codec<TurretType> TURRET = Codec.STRING.comapFlatMap(
                id -> TurretTypes.byId(id).map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown turret " + id)),
                TurretType::getId);
        public static final MapCodec<Unbaked> CODEC = TURRET.fieldOf("turret").xmap(Unbaked::new, Unbaked::turret);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(BakingContext context) {
            TurretModel model = TurretModels.bake(context.entityModelSet(), turret);
            return model == null ? null : new TurretSpecialRenderer(model);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }
    }
}
