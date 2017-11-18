package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.render.models.ModelRelativisticTurret;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import org.lwjgl.opengl.GL11;

class RelativisticTurretRenderer extends TileEntitySpecialRenderer {
    private final ModelRelativisticTurret model;

    public RelativisticTurretRenderer() {
        model = new ModelRelativisticTurret();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {        TurretHead turretHead = (TurretHead) te;
        ResourceLocation textures = (new ResourceLocation(Reference.MOD_ID + ":textures/blocks/relativistic_turret.png"));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        int rotation;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        if (te == null) {
            GL11.glScalef(0.7F, -0.7F, -0.7F);
            GL11.glTranslatef((float) x + 0.0F, (float) y + 0.4F, (float) z + 0.5F);
            GL11.glRotatef(45.0F, 2.5F, -4.5F, -1.0F);
            model.renderAll();
            GL11.glPopMatrix();
            return;
        }

        if (turretHead.shouldConceal) {
            GL11.glPopMatrix();
            return;
        }

        if (te.getWorld() != null) {
            rotation = te.getBlockMetadata();
            GL11.glRotatef(rotation * 90, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, -1F, -1F);
            model.Base.rotateAngleX = turretHead.baseFitRotationX;
            model.Base.rotateAngleY = turretHead.baseFitRotationZ;
            model.Spike1.rotateAngleX = turretHead.baseFitRotationX;
            model.Spike1.rotateAngleY = turretHead.baseFitRotationZ;
            model.Spike2.rotateAngleX = turretHead.baseFitRotationX;
            model.Spike2.rotateAngleY = turretHead.baseFitRotationZ;
            model.Spike3.rotateAngleX = turretHead.baseFitRotationX;
            model.Spike3.rotateAngleY = turretHead.baseFitRotationZ;
            model.Spike4.rotateAngleX = turretHead.baseFitRotationX;
            model.Spike4.rotateAngleY = turretHead.baseFitRotationZ;
            model.Base2.rotateAngleX = turretHead.baseFitRotationX;
            model.Base2.rotateAngleY = turretHead.baseFitRotationZ;
            model.Crystal.rotateAngleX = turretHead.baseFitRotationX;
            model.Crystal.rotateAngleY = turretHead.baseFitRotationZ;
            model.Crystal.rotateAngleX = turretHead.rotationAnimation;
            model.Crystal.rotateAngleY = turretHead.rotationAnimation;
            model.Crystal.rotateAngleZ = turretHead.rotationAnimation;
            model.renderAll();
        }

        GL11.glPopMatrix();
    }
}
