package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.blockentity.misc.LeverBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * The crank (1.12.2 {@code LeverRenderer} + {@code ModelLever}): an arm pointing into the base and a handle, both
 * turning around the arm while cranking.
 * <p>
 * The variant for 1.21.11: the crank angle goes into the model when it is drawn ({@link LeverModel#setupAnim}), so
 * every crank is drawn at its own angle.
 */
public class LeverRenderer implements BlockEntityRenderer<LeverBlockEntity, LeverRenderer.State> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(OMT.id("lever_block"), "main");
    public static final ResourceLocation TEXTURE = OMT.id("textures/block/lever_block.png");
    /** 1.12.2 {@code rotateAngleZ = rotation / 55}: the crank angle in degrees divided by 55 (about one turn). */
    private static final float ANGLE_DIVISOR = 55F;

    /** The crank model; its pose is the crank angle in degrees. */
    public static final class LeverModel extends Model<Float> {
        private final ModelPart arm;
        private final ModelPart handle;

        public LeverModel(ModelPart root) {
            super(root, RenderTypes::entityCutout);
            this.arm = root.getChild("arm");
            this.handle = root.getChild("handle");
        }

        @Override
        public void setupAnim(Float crankDegrees) {
            float angle = crankDegrees / ANGLE_DIVISOR;
            arm.zRot = angle;
            handle.zRot = angle;
        }
    }

    /** What the renderer needs of a crank. */
    public static final class State extends BlockEntityRenderState {
        Direction facing = Direction.SOUTH;
        float crankDegrees;
    }

    private final LeverModel model;

    public LeverRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new LeverModel(context.bakeLayer(LAYER));
    }

    /** 1.12.2 {@code ModelLever} (Shape1 = arm, Shape2 = handle), 32x32 texture. */
    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 0).mirror()
                .addBox(-1.0F, -1.0F, -9.0F, 2, 2, 8), PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(22, 0).mirror()
                .addBox(-1.0F, 0.0F, -3.0F, 2, 6, 2), PartPose.offset(0.0F, 16.0F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(LeverBlockEntity lever, State state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(lever, state, partialTick, cameraPos, crumbling);
        state.facing = lever.getBlockState().getValue(LeverBlock.FACING);
        state.crankDegrees = lever.getRotation(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submit(model, state.facing, state.crankDegrees, poseStack, collector, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.breakProgress);
    }

    /** Submits the crank facing {@code facing} (towards the base) at the given crank angle. */
    public static void submit(LeverModel model, Direction facing, float crankDegrees, PoseStack poseStack,
                              SubmitNodeCollector collector, int packedLight, int packedOverlay,
                              ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        // Rotation index 0 = base to the south, then west, north, east (Direction's 2D order)
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.get2DDataValue() * -90F));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        collector.submitModel(model, crankDegrees, poseStack, RenderTypes.entityCutout(TEXTURE), packedLight,
                packedOverlay, 0, crumbling);
        poseStack.popPose();
    }
}
