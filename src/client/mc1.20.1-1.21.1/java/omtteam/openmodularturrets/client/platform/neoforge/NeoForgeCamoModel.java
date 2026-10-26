package omtteam.openmodularturrets.client.platform.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import omtteam.openmodularturrets.client.camo.CamoModels;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of a camouflage host ({@code CamoHost}) on NeoForge (the Fabric counterpart is {@code FabricCamoModel}):
 * without a disguise it is the block's own model, with one it returns the quads, render types and ambient occlusion
 * of the disguise's model, so the disguise is meshed like the real block (transparent ones in their own layer).
 * The block entity hands the disguise over as {@link CompatBlockEntity#RENDER_DATA} in its model data.
 * <p>
 * Tinted quads keep their tint index; {@link #color} gives the hosts the colour of their disguise, so the vanilla
 * mesher tints them with the client's biome blend like on the real block.
 * <p>
 * The variant up to 1.21.1 (baked models); {@code OMTNeoForgeClient} wraps the models.
 */
public final class NeoForgeCamoModel extends BakedModelWrapper<BakedModel> {
    public NeoForgeCamoModel(BakedModel original) {
        super(original);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data,
                                    @Nullable RenderType renderType) {
        BlockState camo = camo(data);
        if (camo == null) return super.getQuads(state, side, rand, data, renderType);
        List<BakedQuad> quads = new ArrayList<>();
        BakedModel camoModel = model(camo);
        // vanilla models ignore the render type: only ask the disguise for its own layers
        if (renderType == null || camoModel.getRenderTypes(camo, rand, ModelData.EMPTY).contains(renderType)) {
            quads.addAll(camoModel.getQuads(camo, side, rand, ModelData.EMPTY, renderType));
        }
        // what stays of the block's own model on top (e.g. a lamp), in its own layer
        int overlay = CamoModels.overlayTintIndex(state);
        if (overlay >= 0 && (renderType == null || super.getRenderTypes(state, rand, data).contains(renderType))) {
            for (BakedQuad quad : super.getQuads(state, side, rand, data, renderType)) {
                if (quad.getTintIndex() == overlay) quads.add(quad);
            }
        }
        return quads;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        BlockState camo = camo(data);
        if (camo == null) return super.getRenderTypes(state, rand, data);
        ChunkRenderTypeSet disguise = model(camo).getRenderTypes(camo, rand, ModelData.EMPTY);
        return CamoModels.overlayTintIndex(state) < 0 ? disguise
                : ChunkRenderTypeSet.union(disguise, super.getRenderTypes(state, rand, data));
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        BlockState camo = camo(data);
        return camo == null ? super.useAmbientOcclusion(state, data, renderType)
                : model(camo).useAmbientOcclusion(camo, ModelData.EMPTY, renderType);
    }

    /**
     * Colour of a camouflage host: that of its disguise at this position, or none without a disguise. Registered for
     * all camouflage hosts, which have no colour of their own.
     */
    public static int color(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level == null || pos == null || !(level.getModelData(pos).get(CompatBlockEntity.RENDER_DATA) instanceof BlockState camo)) {
            return -1;
        }
        return Minecraft.getInstance().getBlockColors().getColor(camo, level, pos, tintIndex);
    }

    @Nullable
    private static BlockState camo(ModelData data) {
        return data.get(CompatBlockEntity.RENDER_DATA) instanceof BlockState camo ? camo : null;
    }

    private static BakedModel model(BlockState camo) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(camo);
    }
}
