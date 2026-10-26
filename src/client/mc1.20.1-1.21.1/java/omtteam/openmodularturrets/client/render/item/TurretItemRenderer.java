package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.client.render.blockentity.TurretRenderer;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Turret items show the turret model (1.12.2: turret renderer with {@code turretHead == null}); the display
 * transforms are in the item model JSON ({@code parent: builtin/entity}).
 */
public class TurretItemRenderer implements ItemStackRenderer {
    private final TurretType type;
    private Map<TurretType, TurretModel> models;

    private TurretItemRenderer(TurretType type) {
        this.type = type;
    }

    /** Hands over an item renderer for every turret with a model. */
    public static void register(Map<TurretType, ? extends ItemLike> items, BiConsumer<Item, ItemStackRenderer> registrar) {
        items.forEach((type, item) -> {
            if (TurretModels.hasModel(type)) {
                registrar.accept(item.asItem(), new TurretItemRenderer(type));
            }
        });
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (models == null) {
            // baked lazily: the model set is only available once resources are loaded
            models = TurretModels.bake(Minecraft.getInstance().getEntityModels());
        }
        TurretModel model = models.get(type);
        if (model == null) return;
        model.setAim(0, 0);
        model.setActivity(false, 0);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        TurretRenderer.renderModel(model, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
