package modularTurrets.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.misc.ConfigHandler;
import modularTurrets.misc.PacketHandler;
import modularTurrets.tileentity.turretBase.TurretBase;
import modularTurrets.tileentity.turretBase.TurretBaseTierFourTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

public class TurretBaseTierFour extends BlockContainer {

    public final int MaxCharge = ConfigHandler.getBaseTierFourMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierFourMaxIo();

    public TurretBaseTierFour() {
	super(Material.rock);
	this.setBlockName(BlockNames.unlocalisedTurretBaseTierFour);
	this.setCreativeTab(ModularTurrets.modularTurretsTab);
	this.setHardness(-1F);
	this.setResistance(20F);
	this.setStepSound(Block.soundTypeStone);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister icon) {
	    blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase()
		+ ":turretBaseTierFour");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
	    return new TurretBaseTierFourTileEntity(this.MaxCharge, this.MaxIO);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are) {
        if (!world.isRemote) {
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);

            if (player.getDisplayName().equals(base.getOwner())) {
                player.openGui(ModularTurrets.instance, 4, world, x, y, z);
            } else {
                player.addChatMessage(new ChatComponentText("You do not own this turret."));
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
	    EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        if (par1World.isRemote) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
                outputStream.writeInt(PacketHandler.SET_BASE_OWNER);
                outputStream.writeInt(par2);
                outputStream.writeInt(par3);
                outputStream.writeInt(par4);
                outputStream.writeInt(0);
                outputStream.writeUTF(Minecraft.getMinecraft().getSession()
                    .getUsername());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = ModInfo.CHANNEL;
            packet.data = bos.toByteArray();
            packet.length = bos.size();

            PacketDispatcher.sendPacketToServer(packet);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (!world.isRemote) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
        }
    }

    private void dropItems(World world, int x, int y, int z) {

        if (world.getTileEntity(x, y, z) instanceof TurretBase) {
            TurretBase base = (TurretBase) world.getTileEntity(x, y, z);
            Random rand = new Random();
            for (int i = 0; i < base.getSizeInventory(); i++) {
            ItemStack item = base.getStackInSlot(i);

                if (item != null && item.stackSize > 0) {
                    float rx = rand.nextFloat() * 0.8F + 0.1F;
                    float ry = rand.nextFloat() * 0.8F + 0.1F;
                    float rz = rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityItem = new EntityItem(world, x + rx, y
                        + ry, z + rz, new ItemStack(item.getItem(),
                        item.stackSize, item.getItemDamage()));

                    if (item.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound(
                        (NBTTagCompound) item.getTagCompound().copy());
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
}
