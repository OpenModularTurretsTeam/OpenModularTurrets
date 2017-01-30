package omtteam.openmodularturrets.client.render.models;

import com.google.common.base.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static omtteam.openmodularturrets.blocks.BlockTurretBase.RENDERBLOCKSTATE;
import static omtteam.openmodularturrets.blocks.BlockTurretBase.TIER;

/**
 * Created by Keridos on 29/01/17.
 * This Class
 */
public class CamoBakedModel implements IBakedModel {
    public static ResourceLocation FAKE_LOCATION = new ResourceLocation("openmodularturrets","models/block/custom/turret_base");
    List<IBakedModel> defaultModels;

    public CamoBakedModel(List<IBakedModel> list) {
        defaultModels = list;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        IBlockState camoState = extendedState.getValue(RENDERBLOCKSTATE).getRenderState();

        if (camoState.getBlock() instanceof BlockTurretBase && state != null) {
            return defaultModels.get(state.getValue(TIER)).getQuads(state, side, rand);

        } else {
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(camoState).getQuads(camoState, side, rand);
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

    public static class Model implements IModel {
        @Override
        public Collection<ResourceLocation> getDependencies() {
            List<ResourceLocation> list = new ArrayList<>();
            for (int i = 1;i<6; i++) {
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
            for (int i = 1;i<6; i++) {
                try {
                    list.add(ModelLoaderRegistry.getModel(new ModelResourceLocation("openmodularturrets:turret_base_normal", "tier=" + i)).bake(state, format, bakedTextureGetter));
                }  catch (Exception e){
                    e.printStackTrace();
                }
            }
            return new CamoBakedModel(list);
        }

        @Override
        public IModelState getDefaultState() {
            return null;
        }
    }

    public static class ModelLoader implements ICustomModelLoader
    {

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            if(!modelLocation.getResourceDomain().equals(Reference.MOD_ID))
                return false;
            return modelLocation.equals(FAKE_LOCATION);
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            return new Model();
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // Nothing to do
        }
    }

    public static class Statemapper extends StateMapperBase
    {
        public static final ModelResourceLocation LOCATION = new ModelResourceLocation("openmodularturrets:turret_base", "normal");

        @Override
        @Nonnull
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state)
        {
            return LOCATION;
        }
    }
}
