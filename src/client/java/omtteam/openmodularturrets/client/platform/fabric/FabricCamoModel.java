package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.client.camo.CamoModels;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Model of a camouflage host ({@code CamoHost}) on Fabric (the NeoForge counterpart is {@code NeoForgeCamoModel}):
 * without a disguise it is the block's own model, with one it emits
 * the quads of the disguise's model into the normal chunk mesh. That gives the disguise proper ambient occlusion and
 * face culling, and transparent blocks (glass, leaves, ice) are drawn in their own render layer.
 * <p>
 * Tinted quads (grass, leaves, water...) get the colour of the disguise at the host's position, taken from the
 * level view of the chunk being meshed, so the client's biome blend setting applies like on the real block.
 */
public final class FabricCamoModel extends ForwardingBakedModel {
    public FabricCamoModel(BakedModel wrapped) {
        this.wrapped = wrapped;
    }

    /** Wraps the block state and item models of the camouflage hosts ({@link CamoModels#isCamoModel}) once baked. */
    public static void register() {
        ModelLoadingPlugin.register(plugin -> {
            //? if >=1.21 {
            plugin.modifyModelAfterBake().register((model, context) -> {
                var topLevel = context.topLevelId();
                return wrap(model, topLevel != null ? topLevel.id() : context.resourceId());
            });
            //?} else {
            /*plugin.modifyModelAfterBake().register((model, context) -> wrap(model, context.id()));
            *///?}
        });
    }

    private static BakedModel wrap(BakedModel model, ResourceLocation id) {
        return CamoModels.isCamoModel(id) ? new FabricCamoModel(model) : model;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier,
                               RenderContext context) {
        BlockState camo = camoAt(view, pos);
        if (camo == null) {
            super.emitBlockQuads(view, state, pos, randomSupplier, context);
            return;
        }
        BakedModel camoModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camo);
        DisguiseTransform transform = new DisguiseTransform(view, pos, camo, Minecraft.getInstance().getBlockColors());
        context.pushTransform(transform);
        try {
            camoModel.emitBlockQuads(view, camo, pos, randomSupplier, context);
        } finally {
            context.popTransform();
        }
    }

    @Nullable
    private static BlockState camoAt(BlockAndTintGetter view, BlockPos pos) {
        return view.getBlockEntityRenderData(pos) instanceof BlockState camo ? camo : null;
    }

    /**
     * Puts the quads of the disguise into the render layer of the disguise (the host block's layer is the solid
     * one) and applies its tint.
     */
    private static final class DisguiseTransform implements RenderContext.QuadTransform {
        private final BlockAndTintGetter view;
        private final BlockPos pos;
        private final BlockState camo;
        private final BlockColors colors;
        private final MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
        private final BlendMode blendMode;

        private DisguiseTransform(BlockAndTintGetter view, BlockPos pos, BlockState camo, BlockColors colors) {
            this.view = view;
            this.pos = pos;
            this.camo = camo;
            this.colors = colors;
            this.blendMode = BlendMode.fromRenderLayer(ItemBlockRenderTypes.getChunkRenderType(camo));
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            quad.material(finder.copyFrom(quad.material()).blendMode(blendMode).find());
            int tintIndex = quad.colorIndex();
            if (tintIndex != -1) {
                int tint = colors.getColor(camo, view, pos, tintIndex);
                if (tint != -1) {
                    for (int vertex = 0; vertex < 4; vertex++) {
                        quad.color(vertex, multiply(quad.color(vertex), 0xFF000000 | tint));
                    }
                }
                // the tint is applied; the host block has no colour provider that could apply it a second time
                quad.colorIndex(-1);
            }
            return true;
        }

        private static int multiply(int first, int second) {
            int a = channel(first, 24) * channel(second, 24) / 255;
            int r = channel(first, 16) * channel(second, 16) / 255;
            int g = channel(first, 8) * channel(second, 8) / 255;
            int b = channel(first, 0) * channel(second, 0) / 255;
            return (a << 24) | (r << 16) | (g << 8) | b;
        }

        private static int channel(int argb, int shift) {
            return (argb >>> shift) & 0xFF;
        }
    }
}
