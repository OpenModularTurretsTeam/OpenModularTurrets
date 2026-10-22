package omtteam.openmodularturrets.client.render.renderers.blockitem;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.tileentity.LeverTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretTileEntity;

public class TileEntityRenderers {
    @SideOnly(Side.CLIENT)
    public static void init() {
        ClientRegistry.bindTileEntitySpecialRenderer(TurretTileEntity.class, new TurretRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(LeverTileEntity.class, new LeverRenderer());
    }
}
