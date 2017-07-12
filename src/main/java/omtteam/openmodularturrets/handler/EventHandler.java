package omtteam.openmodularturrets.handler;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.items.blocks.ItemBlockBaseAddon;
import omtteam.openmodularturrets.util.OMTFakePlayer;
import omtteam.openmodularturrets.util.OMTUtil;

import static omtteam.omlib.util.RenderUtil.drawHighlightBox;
import static omtteam.openmodularturrets.blocks.BlockExpander.FACING;
import static omtteam.openmodularturrets.util.TurretHeadUtil.getTurretBase;
import static omtteam.openmodularturrets.util.TurretHeadUtil.getTurretBaseFacing;

/**
 * Created by Keridos on 02/05/17.
 * This Class
 */
public class EventHandler {
    private static EventHandler instance;

    private EventHandler() {
    }

    public static EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    @SubscribeEvent
    public void lootEvent(LivingDropsEvent event) {
        if (event.getEntityLiving().getTags().contains("openmodularturrets:turret_hit") && !ConfigHandler.doTurretsKillsDropMobLoot) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void entityHurtEvent(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (fakeDrops > -1) {
            FakePlayer player = OMTFakePlayer.getFakePlayer((WorldServer) event.getEntityLiving().getEntityWorld());
            player.getEntityAttribute(SharedMonsterAttributes.LUCK).setBaseValue(fakeDrops);
            entity.setLastAttacker(player);
        }
    }

    @SubscribeEvent
    public void entityAttackedEvent(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        int fakeDrops = OMTUtil.getFakeDropsLevel(entity);
        if (fakeDrops > -1) {
            FakePlayer player = OMTFakePlayer.getFakePlayer((WorldServer) event.getEntityLiving().getEntityWorld());
            player.getEntityAttribute(SharedMonsterAttributes.LUCK).setBaseValue(fakeDrops);
            entity.setLastAttacker(player);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void drawBlockOutline(DrawBlockHighlightEvent event) {
        if (event.getTarget() != null && event.getTarget().sideHit != null && event.getPlayer().getHeldItemMainhand() != null && event.getPlayer().getHeldItemMainhand().getItem() instanceof ItemBlockBaseAddon) {
            BlockPos blockpos = event.getTarget().getBlockPos().offset(event.getTarget().sideHit);
            if (getTurretBase(event.getPlayer().getEntityWorld(), blockpos) != null) {
                ItemBlockBaseAddon addon = (ItemBlockBaseAddon) event.getPlayer().getHeldItemMainhand().getItem();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                AxisAlignedBB alignedBB;
                EnumFacing facing = getTurretBaseFacing(event.getPlayer().getEntityWorld(), blockpos);
                if (addon.getBlock().getDefaultState().getProperties().containsKey(FACING)) {
                    alignedBB = addon.getRenderOutline(addon.getBlock().getDefaultState().withProperty(FACING, facing), event.getPlayer().getEntityWorld(), blockpos);
                } else {
                    alignedBB = addon.getRenderOutline(addon.getBlock().getDefaultState(), event.getPlayer().getEntityWorld(), blockpos);
                }
                EntityPlayer player = event.getPlayer();
                double partialTicks = event.getPartialTicks();
                double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
                double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
                double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
                float[] color = new float[3];
                if (event.getPlayer().getEntityWorld().isAirBlock(blockpos)) {
                    color[0] = 0F;
                    color[1] = 1F;
                    color[2] = 1F;
                } else {
                    color[0] = 1F;
                    color[1] = 0F;
                    color[2] = 0F;
                }

                alignedBB = alignedBB.offset(-d0, -d1, -d2);

                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();
                vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
                drawHighlightBox(vertexbuffer, alignedBB.minX, alignedBB.minY, alignedBB.minZ, alignedBB.maxX, alignedBB.maxY, alignedBB.maxZ, color[0], color[1], color[2], 0.5F);
                tessellator.draw();
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
        }
    }
}
