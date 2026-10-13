package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.misc.LeverBlock;
import omtteam.openmodularturrets.blockentity.misc.LeverBlockEntity;

/**
 * The crank (1.12.2 {@code LeverRenderer} + {@code ModelLever}): an arm pointing into the base and a handle, both
 * turning around the arm while cranking.
 */
public class LeverRenderer implements BlockEntityRenderer<LeverBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(OMT.id("lever_block"), "main");
    public static final ResourceLocation TEXTURE = OMT.id("textures/block/lever_block.png");
    /** 1.12.2 {@code rotateAngleZ = rotation / 55}: the crank angle in degrees divided by 55 (about one turn). */
    private static final float ANGLE_DIVISOR = 55F;

    private final ModelPart arm;
    private final ModelPart handle;

    public LeverRenderer(BlockEntityRendererProvider.Context context) {
        this(context.bakeLayer(LAYER));
    }

    public LeverRenderer(ModelPart root) {
        this.arm = root.getChild("arm");
        this.handle = root.getChild("handle");
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
    public void render(LeverBlockEntity lever, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        Direction facing = lever.getBlockState().getValue(LeverBlock.FACING);
        render(facing, lever.getRotation(partialTick), poseStack, buffers, packedLight, packedOverlay);
    }

    /** Draws the crank facing {@code facing} (towards the base) at the given crank angle. */
    public void render(Direction facing, float crankDegrees, PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                       int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        // Rotation index 0 = base to the south, then west, north, east (Direction's 2D order)
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.get2DDataValue() * -90F));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        float angle = crankDegrees / ANGLE_DIVISOR;
        arm.zRot = angle;
        handle.zRot = angle;
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        arm.render(poseStack, consumer, packedLight, packedOverlay);
        handle.render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
