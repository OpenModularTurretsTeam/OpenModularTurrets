package omtteam.openmodularturrets.client.render.models;

import com.google.common.base.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.render.CamoBakedModel;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static omtteam.openmodularturrets.blocks.BlockTurretBase.TIER;

/**
 * Created by Keridos on 29/01/17.
 * This Class
 */

@SideOnly(Side.CLIENT)
public class TurretBaseBakedModel extends CamoBakedModel {
    private static final ResourceLocation FAKE_LOCATION = new ResourceLocation("openmodularturrets", "models/block/custom/turret_base");

    private final TextureAtlasSprite particle;


    private TurretBaseBakedModel(List<IBakedModel> list, TextureAtlasSprite part) {
        super(list);
        particle = part;
    }

    @Override
    protected IBakedModel getModel(List<IBakedModel> list, IBlockState state) {
        return list.get(state.getValue(TIER) - 1);
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    @Override
    @Nonnull
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    public static class Model implements IModel {
        Model() {

        }


        @Override
        public Collection<ResourceLocation> getDependencies() {
            List<ResourceLocation> list = new ArrayList<>();
            for (int i = 1; i < 6; i++) {
                list.add(new ModelResourceLocation("openmodularturrets:turret_base_normal", "tier=" + i));
            }
            return list;
        }

        @Override
        public Collection<ResourceLocation> getTextures() {
            return Collections.emptyList();
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
            List<IBakedModel> list = new ArrayList<>();
            for (int i = 1; i < 6; i++) {
                try {
                    list.add(ModelLoaderRegistry.getModel(new ModelResourceLocation("openmodularturrets:turret_base_normal", "tier=" + i)).bake(state, format, bakedTextureGetter));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TextureAtlasSprite part = bakedTextureGetter.apply(new ResourceLocation("openmodularturrets", "blocks/turret_base_tier_two"));
            return new TurretBaseBakedModel(list, part);
        }

        @Override
        public IModelState getDefaultState() {
            return null;
        }
    }

    public static class ModelLoader implements ICustomModelLoader {

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return (modelLocation.getResourceDomain().equals(Reference.MOD_ID) && modelLocation.equals(FAKE_LOCATION));
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            return new Model();
        }

        @Override
        @ParametersAreNonnullByDefault
        public void onResourceManagerReload(IResourceManager resourceManager) {
            // Nothing to do
        }
    }

    public static class Statemapper extends StateMapperBase {
        static final ModelResourceLocation LOCATION = new ModelResourceLocation("openmodularturrets:turret_base", "normal");

        @Override
        @Nonnull
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return LOCATION;
        }
    }
}
