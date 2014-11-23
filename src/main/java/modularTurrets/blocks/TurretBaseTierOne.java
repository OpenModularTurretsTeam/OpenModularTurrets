package modularTurrets.blocks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.misc.ConfigHandler;
import modularTurrets.misc.PacketHandler;
import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretBaseTierOneTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TurretBaseTierOne extends BlockContainer {

    public final int MaxCharge = ConfigHandler.getBaseTierOneMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierOneMaxIo();

    public TurretBaseTierOne(int par1) {
	super(par1, Material.rock);
	this.setUnlocalizedName(BlockNames.unlocalisedTurretBaseTierOne);
	this.setCreativeTab(ModularTurrets.modularTurretsTab);
	this.setHardness(-1F);
	this.setResistance(20F);
	this.setStepSound(Block.soundStoneFootstep);
	this.setLightValue(0.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister icon) {
	blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase()
		+ ":turretBaseTierOne");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
	return new TurretBaseTierOneTileEntity(this.MaxCharge, this.MaxIO);
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
	   	    outputStream.writeUTF(Minecraft.getMinecraft().getSession().getUsername());
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
    public boolean onBlockActivated(World world, int x, int y, int z,
	    EntityPlayer player, int metadata, float what, float these,
	    float are) {
	if (!world.isRemote) {
	    TurretBase base = (TurretBase) world.getBlockTileEntity(x, y, z);
	    if (player.username.equals(base.getOwner())) {
		player.openGui(ModularTurrets.instance, 1, world, x, y, z);
	    } else {
		player.addChatMessage("You do not own this turret.");
	    }
	}
	return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
	if (!world.isRemote) {
	    dropItems(world, x, y, z);
	    super.breakBlock(world, x, y, z, par5, par6);
	}
    }
    
    private void dropItems(World world, int x, int y, int z) {

	if (world.getBlockTileEntity(x, y, z) instanceof TurretBase) {
	    TurretBase base = (TurretBase) world.getBlockTileEntity(x, y, z);
	    Random rand = new Random();
	    for (int i = 0; i < base.getSizeInventory(); i++) {
		ItemStack item = base.getStackInSlot(i);

		if (item != null && item.stackSize > 0) {
		    float rx = rand.nextFloat() * 0.8F + 0.1F;
		    float ry = rand.nextFloat() * 0.8F + 0.1F;
		    float rz = rand.nextFloat() * 0.8F + 0.1F;

		    EntityItem entityItem = new EntityItem(world, x + rx, y
			    + ry, z + rz, new ItemStack(item.itemID,
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
