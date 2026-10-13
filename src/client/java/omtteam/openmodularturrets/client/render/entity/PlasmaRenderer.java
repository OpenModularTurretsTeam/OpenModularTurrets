package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.entity.projectile.PlasmaProjectile;

/**
 * Plasma ball (oldmod {@code PlasmaRenderer} + {@code ModelPlasmaProjectile}): two crossed 4x4 planes, scaled by
 * 0.675 and turned along the flight direction. oldmod rotated around a point 0.9 blocks below the ball; here it turns
 * around its own centre.
 */
public class PlasmaRenderer extends EntityRenderer<PlasmaProjectile, PlasmaRenderer.State> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(OMT.id("plasma"), "main");
    private static final Identifier TEXTURE = OMT.id("textures/entity/projectile/plasma.png");
    private static final float SCALE = 0.675F;

    private final ModelPart shot;

    public static class State extends EntityRenderState {
        float yRot;
        float xRot;
    }

    public PlasmaRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shot = context.bakeLayer(LAYER).getChild("shot");
    }

    /** oldmod {@code ModelPlasmaProjectile}, with the pivot moved to the ball's centre. */
    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("shot", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0F, -2.0F, -2.0F, 0, 4, 4)
                        .texOffs(0, 0).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 0),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PlasmaProjectile entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        int light = state.lightCoords;
        poseStack.pushPose();
        poseStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.rotate(Axis.ZP.rotationDegrees(state.xRot));
        poseStack.scale(SCALE, SCALE, SCALE);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TEXTURE),
                (pose, consumer) -> shot.render(ClientCompat.poseStackAt(pose), consumer, light, OverlayTexture.NO_OVERLAY));
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}
