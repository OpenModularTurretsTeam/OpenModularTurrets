package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Draws every turret with its 3.x model, turned with {@link TurretMath} (1.20.1+ {@code TurretRenderer}): the model
 * stands upright in the local frame of the mounting face (up = away from the base), the holder turns by the yaw and
 * the weapon by the pitch, both interpolated on the client.
 * <p>
 * The 3.x models are drawn upright with the weapon pointing along -z in model space and flipped by
 * {@code scale(1, -1, -1)} into the frame, which turns a model rotation of {@code a} around y into {@code -a} in the
 * frame; so yaw and pitch go in negated.
 */
@SideOnly(Side.CLIENT)
public class TurretRenderer extends TileEntitySpecialRenderer<TurretTileEntity> {
    private final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    @Override
    public void render(TurretTileEntity turret, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        TurretType type = turret.getCoreType();
        Face face = turret.getFace();
        if (type == null || face == null || turret.isConcealed()) return;
        TurretModels.Entry entry = TurretModels.get(type);
        if (entry == null) return;
        Minecraft.getMinecraft().renderEngine.bindTexture(turret.isActive() ? entry.activeTexture : entry.texture);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.multMatrix(frameMatrix(TurretMath.frame(face)));
        // 3.x: the model's foot is 1.5 blocks below its origin; the turret stands on the face of its base
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        entry.model.setRotationForTarget((float) -Math.toRadians(turret.getRenderPitch(partialTicks)),
                (float) -Math.toRadians(turret.getRenderYaw(partialTicks)));
        entry.model.setActivity(turret.isActive(), turret.getRenderSpin(partialTicks));
        entry.model.renderAll();
        GlStateManager.popMatrix();
    }

    /** Column-major matrix mapping the local axes (x = right, y = up, z = forward) to the frame's world axes. */
    private FloatBuffer frameMatrix(TurretMath.Frame frame) {
        matrix.clear();
        put(frame.right());
        matrix.put(0F);
        put(frame.up());
        matrix.put(0F);
        put(frame.forward());
        matrix.put(0F);
        matrix.put(0F).put(0F).put(0F).put(1F);
        matrix.flip();
        return matrix;
    }

    private void put(Vector3 axis) {
        matrix.put((float) axis.x()).put((float) axis.y()).put((float) axis.z());
    }
}
