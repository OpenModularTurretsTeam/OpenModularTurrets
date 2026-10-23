package omtteam.openmodularturrets.client.platform.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import omtteam.openmodularturrets.client.render.item.ItemStackRenderer;

import java.lang.reflect.Field;

/**
 * Forge view of an {@link ItemStackRenderer}: the {@link BlockEntityWithoutLevelRenderer} that Forge calls for items
 * whose model has {@code parent: builtin/entity} (as {@code NeoForgeItemRenderer}).
 * <p>
 * Forge 1.20.1 has no event for client item extensions: an item gets them from its own {@code initializeClient},
 * which Forge stores in the item's {@code renderProperties} field while constructing it. The shared item classes stay
 * loader-free, so {@link #register} sets that field afterwards (a field Forge adds, so its name is the same in the dev
 * and the release environment); Forge reads it on every lookup.
 */
public final class ForgeItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final Field RENDER_PROPERTIES = ObfuscationReflectionHelper.findField(Item.class, "renderProperties");

    private final ItemStackRenderer renderer;

    private ForgeItemRenderer(ItemStackRenderer renderer) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.renderer = renderer;
    }

    /** Draws {@code item} with {@code renderer} (the renderer is created on first use). */
    public static void register(Item item, ItemStackRenderer renderer) {
        try {
            RENDER_PROPERTIES.set(item, extensions(renderer));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not set the item renderer of " + item, e);
        }
    }

    private static IClientItemExtensions extensions(ItemStackRenderer renderer) {
        return new IClientItemExtensions() {
            private ForgeItemRenderer itemRenderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (itemRenderer == null) {
                    itemRenderer = new ForgeItemRenderer(renderer);
                }
                return itemRenderer;
            }
        };
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                             int packedLight, int packedOverlay) {
        renderer.render(stack, context, poseStack, buffers, packedLight, packedOverlay);
    }
}
