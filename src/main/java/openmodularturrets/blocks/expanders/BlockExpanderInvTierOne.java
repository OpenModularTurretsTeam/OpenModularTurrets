package openmodularturrets.blocks.expanders;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import openmodularturrets.ModularTurrets;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.expander.AbstractInvExpander;
import openmodularturrets.tileentity.expander.ExpanderInvTierFiveTileEntity;
import openmodularturrets.tileentity.expander.ExpanderInvTierOneTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierOneTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFiveTileEntity;

import java.util.Random;

public class BlockExpanderInvTierOne extends BlockContainer {
    public BlockExpanderInvTierOne() {
        super(Material.rock);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockName(Names.Blocks.unlocalisedExpanderInvTierOne);
        this.setBlockBounds(0.1F, 0.1F, 0.1F, 0.9F, 0.9F, 0.9F);
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);
        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":expanderInvTierOne");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        AbstractInvExpander expander = (AbstractInvExpander) world.getTileEntity(x, y, z);
        TurretBase base = expander.getBase();
        if (base != null && base.getTrustedPlayer(player.getUniqueID()) != null) {
            if (base.getTrustedPlayer(player.getUniqueID()).canOpenGUI) {
                player.openGui(ModularTurrets.instance, 7, world, x, y, z);
                return true;
            }
        }
        if (base != null && player.getUniqueID().toString().equals(base.getOwner())) {
            player.openGui(ModularTurrets.instance, 7, world, x, y, z);
        } else {
            player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("status.ownership")));
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (!world.isRemote) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
        }
    }

    private void dropItems(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof ExpanderInvTierOneTileEntity) {
            ExpanderInvTierOneTileEntity entity = (ExpanderInvTierOneTileEntity) world.getTileEntity(x, y, z);
            Random rand = new Random();
            for (int i = 0; i < entity.getSizeInventory(); i++) {
                ItemStack item = entity.getStackInSlot(i);

                if (item != null && item.stackSize > 0) {
                    float rx = rand.nextFloat() * 0.8F + 0.1F;
                    float ry = rand.nextFloat() * 0.8F + 0.1F;
                    float rz = rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz,
                            new ItemStack(item.getItem(), item.stackSize,
                                    item.getItemDamage()));

                    if (item.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                    }

                    float factor = 0.05F;
                    entityItem.motionX = rand.nextGaussian() * factor;
                    entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                    entityItem.motionZ = rand.nextGaussian() * factor;
                    world.spawnEntityInWorld(entityItem);
                    item.stackSize = 0;
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new ExpanderInvTierOneTileEntity();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
