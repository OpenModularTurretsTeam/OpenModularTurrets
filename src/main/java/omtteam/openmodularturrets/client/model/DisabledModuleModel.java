package omtteam.openmodularturrets.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.item.AddonMetaItem;
import omtteam.openmodularturrets.item.ModuleToggles;
import omtteam.openmodularturrets.item.UpgradeMetaItem;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The item model of an upgrade or addon, wrapped so a stack that is disabled in the config ({@link ModuleToggles})
 * shows vanilla's barrier sprite (a red "no" symbol) over its icon in GUIs, like the item decoration on 1.20.1+.
 * 1.12 has no item decoration hook, so the symbol is a quad in front of the item, added only for the GUI transform;
 * the check runs on every render through the override list, so a config change shows at once.
 */
@SideOnly(Side.CLIENT)
public final class DisabledModuleModel implements IBakedModel {
    /** In vanilla's block atlas, which holds the item textures in 1.12. */
    public static final ResourceLocation SYMBOL = new ResourceLocation("minecraft", "items/barrier");
    /** A little in front of the item's front face (generated item models end at 8.5 / 16). */
    private static final float SYMBOL_Z = 9.0F;

    private final IBakedModel base;
    private final ItemOverrideList overrides;
    @Nullable
    private Marked marked;

    private DisabledModuleModel(IBakedModel base, List<BakedQuad> symbol) {
        this.base = base;
        this.overrides = new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world,
                                               @Nullable EntityLivingBase entity) {
                IBakedModel resolved = base.getOverrides().handleItemState(base, stack, world, entity);
                if (!ModuleToggles.isDisabled(stack)) return resolved;
                if (resolved != base) return new Marked(resolved, symbol);
                if (marked == null) marked = new Marked(base, symbol);
                return marked;
            }
        };
    }

    /** The model locations of every upgrade and addon item, as the client proxy registers them. */
    private static List<ModelResourceLocation> locations() {
        List<ModelResourceLocation> locations = new ArrayList<>();
        for (String name : UpgradeMetaItem.subNames) {
            locations.add(new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + name.toLowerCase()));
        }
        for (String name : AddonMetaItem.subNames) {
            locations.add(new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + name.toLowerCase()));
        }
        locations.add(new ModelResourceLocation(Reference.MOD_ID + ":upgrade_energy_multiplier"));
        return locations;
    }

    /** Wraps the upgrade and addon item models; called from {@code ModelBakeEvent}. */
    public static void wrapModels(IRegistry<ModelResourceLocation, IBakedModel> registry, TextureMap textures) {
        List<BakedQuad> symbol = Collections.singletonList(symbolQuad(textures.getAtlasSprite(SYMBOL.toString())));
        for (ModelResourceLocation location : locations()) {
            IBakedModel model = registry.getObject(location);
            if (model != null && !(model instanceof DisabledModuleModel)) {
                registry.putObject(location, new DisabledModuleModel(model, symbol));
            }
        }
    }

    /** The full 16 × 16 sprite, facing the viewer of a GUI item, unshaded. */
    private static BakedQuad symbolQuad(TextureAtlasSprite sprite) {
        BlockPartFace face = new BlockPartFace(null, -1, "", new BlockFaceUV(new float[]{0, 0, 16, 16}, 0));
        return new FaceBakery().makeBakedQuad(new Vector3f(0, 0, SYMBOL_Z), new Vector3f(16, 16, SYMBOL_Z), face, sprite,
                EnumFacing.SOUTH, ModelRotation.X0_Y0, null, false, false);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return base.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrides;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        return base.handlePerspective(type);
    }

    /**
     * The model of a disabled stack: the item's own model, with the symbol in GUIs only (the transform of a held or
     * dropped item gives back the item's model).
     */
    private static final class Marked implements IBakedModel {
        private final IBakedModel base;
        private final List<BakedQuad> symbol;

        private Marked(IBakedModel base, List<BakedQuad> symbol) {
            this.base = base;
            this.symbol = symbol;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            List<BakedQuad> quads = base.getQuads(state, side, rand);
            if (side != null) return quads;
            List<BakedQuad> all = new ArrayList<>(quads);
            all.addAll(symbol);
            return all;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return base.isAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return base.isGui3d();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return base.isBuiltInRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return base.getParticleTexture();
        }

        @SuppressWarnings("deprecation")
        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return base.getItemCameraTransforms();
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.NONE;
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
            Pair<? extends IBakedModel, Matrix4f> perspective = base.handlePerspective(type);
            if (type != ItemCameraTransforms.TransformType.GUI) return perspective;
            IBakedModel model = perspective.getLeft();
            return Pair.of(model == base ? this : new Marked(model, symbol), perspective.getRight());
        }
    }
}
