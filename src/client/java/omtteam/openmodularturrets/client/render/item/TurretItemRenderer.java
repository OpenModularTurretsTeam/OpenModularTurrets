package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Turret items show the turret model (oldmod: turret renderer with {@code turretHead == null}). Used by the
 * {@code openmodularturrets:turret} special item model ({@code assets/openmodularturrets/items/*_turret.json});
 * the display transforms are in the base item model JSON.
 */
public class TurretItemRenderer implements NoDataSpecialModelRenderer {
    @Nullable
    private final TurretModel model;

    public TurretItemRenderer(@Nullable TurretModel model) {
        this.model = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay,
                       boolean hasFoil, int outlineColor) {
        if (model == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        TurretRenderer.submitModel(model, 0, 0, poseStack, collector, packedLight);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        ItemExtents.fullBlock(output);
    }

    /** JSON: {@code {"type": "openmodularturrets:turret", "turret": "machine_gun_turret"}}. */
    public record Unbaked(TurretType turret) implements NoDataSpecialModelRenderer.Unbaked {
        private static final Codec<TurretType> TURRET_CODEC = Codec.STRING.comapFlatMap(
                id -> TurretType.byId(id).map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown turret: " + id)),
                TurretType::getId);
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TURRET_CODEC.fieldOf("turret").forGetter(Unbaked::turret)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TurretItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TurretItemRenderer(TurretModels.bake(context.entityModelSet()).get(turret));
        }
    }

    /** Corners of the block the models fill, for the item's bounding box. */
    static final class ItemExtents {
        private ItemExtents() {
        }

        static void fullBlock(Consumer<Vector3fc> output) {
            for (int i = 0; i < 8; i++) {
                output.accept(new Vector3f(i & 1, (i >> 1) & 1, (i >> 2) & 1));
            }
        }
    }
}
