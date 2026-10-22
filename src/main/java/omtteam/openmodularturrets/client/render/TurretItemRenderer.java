package omtteam.openmodularturrets.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.core.turret.TurretType;

/**
 * Draws a turret item with its 3.x model, as the 1.12.2 turret renderers did for items.
 */
@SideOnly(Side.CLIENT)
public class TurretItemRenderer extends TileEntityItemStackRenderer {
    private final TurretType type;

    public TurretItemRenderer(TurretType type) {
        this.type = type;
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TurretModels.Entry entry = TurretModels.get(type);
        if (entry == null) return;
        Minecraft.getMinecraft().renderEngine.bindTexture(entry.texture);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 1.5F, 0.5F);
        GlStateManager.scale(0.7F, -0.7F, -0.7F);
        GlStateManager.translate(0.0F, 0.4F, 0.5F);
        GlStateManager.rotate(45.0F, 2.5F, -4.5F, -1.0F);
        entry.model.setRotationForTarget(0, 0);
        entry.model.setActivity(false, 0);
        entry.model.renderAll();
        GlStateManager.popMatrix();
    }
}
