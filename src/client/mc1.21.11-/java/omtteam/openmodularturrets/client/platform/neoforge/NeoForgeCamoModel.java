package omtteam.openmodularturrets.client.platform.neoforge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Model of a camouflage host ({@code CamoHost}) on NeoForge (the Fabric counterpart is {@code FabricCamoModel}):
 * without a disguise it is the block's own model, with one it consists of the parts of the disguise's model, with
 * their render types and ambient occlusion, so the disguise is meshed like the real block (transparent ones in their
 * own layer). The block entity hands the disguise over as {@link CompatBlockEntity#RENDER_DATA} in its model data.
 * <p>
 * Tinted quads keep their tint index; {@link #color} gives the hosts the colour of their disguise, so the vanilla
 * mesher tints them with the client's biome blend like on the real block.
 * <p>
 * The variant for 1.21.11 (block state models made of parts); {@code OMTNeoForgeClient} wraps the models.
 */
public final class NeoForgeCamoModel extends DelegateBlockStateModel {
    public NeoForgeCamoModel(BlockStateModel delegate) {
        super(delegate);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
                             List<BlockModelPart> parts) {
        BlockState camo = level.getModelData(pos).get(CompatBlockEntity.RENDER_DATA) instanceof BlockState found
                ? found : null;
        if (camo == null) {
            super.collectParts(level, pos, state, random, parts);
            return;
        }
        BlockStateModel camoModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camo);
        for (BlockModelPart part : camoModel.collectParts(level, pos, camo, random)) {
            parts.add(new DisguisePart(part, camo));
        }
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

    /** A part of the disguise's model, drawn in the disguise's render type (the host block's is the solid one). */
    private record DisguisePart(BlockModelPart part, BlockState camo) implements BlockModelPart {
        @Override
        public List<BakedQuad> getQuads(@Nullable Direction side) {
            return part.getQuads(side);
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean useAmbientOcclusion() {
            return part.useAmbientOcclusion();
        }

        @Override
        public TriState ambientOcclusion() {
            return part.ambientOcclusion();
        }

        @Override
        public TextureAtlasSprite particleIcon() {
            return part.particleIcon();
        }

        @Override
        public ChunkSectionLayer getRenderType(BlockState state) {
            return part.getRenderType(camo);
        }
    }
}
