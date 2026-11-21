package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;

/**
 * Plasma ball (1.12.2 {@code PlasmaRenderer} + {@code ModelPlasmaProjectile}): two crossed 4x4 planes, scaled by
 * 0.675 and turned along the flight direction. 1.12.2 rotated around a point 0.9 blocks below the ball; here it turns
 * around its own centre.
 */
public class PlasmaRenderer extends EntityRenderer<PlasmaProjectile> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(OMT.id("plasma"), "main");
    private static final ResourceLocation TEXTURE = OMT.id("textures/entity/projectile/plasma.png");
    private static final float SCALE = 0.675F;

    private final ModelPart shot;

    public PlasmaRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shot = context.bakeLayer(LAYER).getChild("shot");
    }

    /** 1.12.2 {@code ModelPlasmaProjectile}, with the pivot moved to the ball's centre. */
    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("shot", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0F, -2.0F, -2.0F, 0, 4, 4)
                        .texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 0),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void render(PlasmaProjectile entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int light) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.scale(SCALE, SCALE, SCALE);
        shot.render(poseStack, buffers.getBuffer(RenderType.entityTranslucent(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public ResourceLocation getTextureLocation(PlasmaProjectile entity) {
        return TEXTURE;
    }
}
