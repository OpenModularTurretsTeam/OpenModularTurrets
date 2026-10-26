package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.fabric.api.client.model.loading.v1.wrapper.WrapperBlockStateModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Model of a camouflage host ({@code CamoHost}) on Fabric (the NeoForge counterpart is {@code NeoForgeCamoModel}):
 * without a disguise it is the block's own model, with one it emits the quads of the disguise's model into the normal
 * chunk mesh, in the render layer of the disguise. That gives the disguise proper ambient occlusion and face culling,
 * and transparent blocks (glass, leaves, ice) are drawn in their own render layer.
 * <p>
 * Tinted quads (grass, leaves, water...) get the colour of the disguise at the host's position, taken from the level
 * view of the chunk being meshed, so the client's biome blend setting applies like on the real block.
 * <p>
 * The variant for 1.21.11 (Fabric's block state model API); {@code OMTFabricClient} wraps the models.
 */
public final class FabricCamoModel extends WrapperBlockStateModel {
    public FabricCamoModel(BlockStateModel wrapped) {
        super(wrapped);
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter view, BlockPos pos, BlockState state,
                          RandomSource random, Predicate<@Nullable Direction> cullTest) {
        BlockState camo = camoAt(view, pos);
        if (camo == null) {
            super.emitQuads(emitter, view, pos, state, random, cullTest);
            return;
        }
        BlockStateModel camoModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camo);
        emitter.pushTransform(new DisguiseTransform(view, pos, camo, Minecraft.getInstance().getBlockColors()));
        try {
            camoModel.emitQuads(emitter, view, pos, camo, random, cullTest);
        } finally {
            emitter.popTransform();
        }
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter view, BlockPos pos, BlockState state,
                                              RandomSource random) {
        // a disguise changes the geometry, and its tint depends on the position: no key to cache it by
        return camoAt(view, pos) == null ? super.createGeometryKey(view, pos, state, random) : null;
    }

    @Nullable
    private static BlockState camoAt(BlockAndTintGetter view, BlockPos pos) {
        return view.getBlockEntityRenderData(pos) instanceof BlockState camo ? camo : null;
    }

    /**
     * Puts the quads of the disguise into the render layer of the disguise (the host block's layer is the solid one) and
     * applies its tint.
     */
    private static final class DisguiseTransform implements QuadTransform {
        private final BlockAndTintGetter view;
        private final BlockPos pos;
        private final BlockState camo;
        private final BlockColors colors;
        private final ChunkSectionLayer layer;

        private DisguiseTransform(BlockAndTintGetter view, BlockPos pos, BlockState camo, BlockColors colors) {
            this.view = view;
            this.pos = pos;
            this.camo = camo;
            this.colors = colors;
            this.layer = ItemBlockRenderTypes.getChunkRenderType(camo);
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            // a quad without its own layer would be drawn in the light's layer
            if (quad.renderLayer() == null) {
                quad.renderLayer(layer);
            }
            int tintIndex = quad.tintIndex();
            if (tintIndex != -1) {
                int tint = colors.getColor(camo, view, pos, tintIndex);
                if (tint != -1) {
                    for (int vertex = 0; vertex < 4; vertex++) {
                        quad.color(vertex, multiply(quad.color(vertex), 0xFF000000 | tint));
                    }
                }
                // the tint is applied; the host block has no colour provider that could apply it a second time
                quad.tintIndex(-1);
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
