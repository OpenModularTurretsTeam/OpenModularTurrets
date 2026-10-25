package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.item.ItemBlockTurret;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Locale;

import static omtteam.openmodularturrets.tileentity.turret.TurretMount.getTurretBaseFacing;

/**
 * While the player holds a turret and OMLib draws a red placement outline next to a base, says why the turret cannot
 * go there ({@link TurretPlacement.Result}) in a small label facing the camera, drawn without depth test so blocks do
 * not hide it (1.20.1+ {@code TurretPlacementOutline.renderLabel}). Called by the client event handler after the
 * world was rendered.
 */
@SideOnly(Side.CLIENT)
public final class TurretPlacementLabel {
    private static final String KEY = "message.openmodularturrets.placement.";
    /** The label: distance from the middle of the block towards the camera, size and colours. */
    private static final double LABEL_OFFSET = 0.5D;
    private static final float LABEL_SCALE = 0.01F;
    private static final int LABEL_COLOR = 0xFFFF5555;
    private static final float LABEL_BACKGROUND_ALPHA = 0.25F;

    private TurretPlacementLabel() {
    }

    public static void render(float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity viewer = minecraft.getRenderViewEntity();
        RayTraceResult hit = minecraft.objectMouseOver;
        // OMLib draws the outline from DrawBlockHighlightEvent, which needs a player view and the GUI shown
        if (!(viewer instanceof EntityPlayer) || minecraft.world == null || minecraft.gameSettings.hideGUI
                || hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK || hit.sideHit == null) {
            return;
        }
        ItemStack stack = ((EntityPlayer) viewer).getHeldItemMainhand();
        if (!(stack.getItem() instanceof ItemBlockTurret)) return;
        BlockTurret turret = (BlockTurret) ((ItemBlockTurret) stack.getItem()).getBlock();
        BlockPos pos = hit.getBlockPos().offset(hit.sideHit);
        String reason = reason(minecraft.world, pos, turret);
        if (reason != null) {
            draw(minecraft, viewer, partialTicks, pos, reason);
        }
    }

    /**
     * Why the turret cannot go to {@code pos}, with the limit that stops it; {@code null} if it can or if no base is
     * next to {@code pos} (then OMLib draws no outline either).
     */
    @Nullable
    private static String reason(World world, BlockPos pos, BlockTurret turret) {
        EnumFacing towardsBase = getTurretBaseFacing(world, pos);
        if (towardsBase == null) return null;
        // ItemBlockTurret.canPlace: the outline is red for any block in the way
        if (!world.isAirBlock(pos)) return I18n.format(KEY + "blocked");
        TurretPlacement.Result result = turret.attachResult(world, pos);
        if (result == null || result.allowed()) return null;
        TurretBase base = (TurretBase) world.getTileEntity(pos.offset(towardsBase));
        TurretType type = turret.getCoreType();
        String key = KEY + result.name().toLowerCase(Locale.ROOT);
        switch (result) {
            case TIER_TOO_LOW:
                return I18n.format(key, type.getRequiredBaseTier());
            case BASE_FULL:
                return I18n.format(key, ConfigLogicSettings.INSTANCE.baseTier(base.getTier()).maxTurrets());
            case TYPE_LIMIT:
                return I18n.format(key, ConfigLogicSettings.INSTANCE.turret(type).maxSimultaneous());
            default:
                return I18n.format(key);
        }
    }

    /**
     * Draws the text like a name tag, half a block from the middle of the turret's block towards the camera, so the
     * outline does not cross it.
     */
    private static void draw(Minecraft minecraft, Entity viewer, float partialTicks, BlockPos pos, String text) {
        // the world is rendered relative to the viewer's interpolated position
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        Vec3d camera = ActiveRenderInfo.getCameraPosition().add(viewerX, viewerY, viewerZ);
        Vec3d centre = new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
        Vec3d at = centre.add(camera.subtract(centre).normalize().scale(LABEL_OFFSET));

        FontRenderer font = minecraft.fontRenderer;
        int halfWidth = font.getStringWidth(text) / 2;
        boolean frontView = minecraft.gameSettings.thirdPersonView == 2;
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(at.x - viewerX, at.y - viewerY, at.z - viewerZ);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((frontView ? -1 : 1) * minecraft.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO);

            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(-halfWidth - 1, -1, 0).color(0F, 0F, 0F, LABEL_BACKGROUND_ALPHA).endVertex();
            buffer.pos(-halfWidth - 1, 8, 0).color(0F, 0F, 0F, LABEL_BACKGROUND_ALPHA).endVertex();
            buffer.pos(halfWidth + 1, 8, 0).color(0F, 0F, 0F, LABEL_BACKGROUND_ALPHA).endVertex();
            buffer.pos(halfWidth + 1, -1, 0).color(0F, 0F, 0F, LABEL_BACKGROUND_ALPHA).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();

            font.drawString(text, -halfWidth, 0, LABEL_COLOR);
        } finally {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
