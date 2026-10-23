package omtteam.openmodularturrets.client.platform.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Model of a camouflage host ({@code CamoHost}) on Forge, the same as {@code NeoForgeCamoModel} (whose Forge 1.20.1 API
 * it shares): without a disguise it is the block's own model, with one it returns the quads and render types of the
 * disguise's model, so the disguise is meshed like the real block (transparent ones in their own layer). The block
 * entity hands the disguise over as {@link CompatBlockEntity#RENDER_DATA} in its model data.
 * <p>
 * Unlike on NeoForge, ambient occlusion stays the block's own: Forge 1.20.1 does not pass the model data there.
 * Tinted quads keep their tint index; {@link #color} gives the hosts the colour of their disguise.
 */
public final class ForgeCamoModel extends BakedModelWrapper<BakedModel> {
    public ForgeCamoModel(BakedModel original) {
        super(original);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
                                             @NotNull ModelData data, @Nullable RenderType renderType) {
        BlockState camo = camo(data);
        if (camo == null) return super.getQuads(state, side, rand, data, renderType);
        return model(camo).getQuads(camo, side, rand, ModelData.EMPTY, renderType);
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                                      @NotNull ModelData data) {
        BlockState camo = camo(data);
        return camo == null ? super.getRenderTypes(state, rand, data) : model(camo).getRenderTypes(camo, rand, ModelData.EMPTY);
    }

    /**
     * Colour of a camouflage host: that of its disguise at this position, or none without a disguise. Registered for
     * all camouflage hosts, which have no colour of their own.
     */
    public static int color(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        ModelDataManager manager = level == null ? null : level.getModelDataManager();
        ModelData data = manager == null || pos == null ? null : manager.getAt(pos);
        if (data == null || !(data.get(CompatBlockEntity.RENDER_DATA) instanceof BlockState camo)) {
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
