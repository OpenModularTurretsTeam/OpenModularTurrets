package openmodularturrets.blocks.util;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmodularturrets.tileentity.TileEntityContainer;

import java.util.Random;

/**
 * Created by Keridos on 05/12/2015.
 * This Class
 */
public abstract class BlockAbstractContainer extends BlockContainer {
    protected BlockAbstractContainer(Material material) {
        super(material);
    }

    public abstract TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_);

    @Override
    public boolean canCreatureSpawn(IBlockAccess worldIn, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    protected void dropItems(World worldIn, BlockPos pos) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityContainer) {
            TileEntityContainer entity = (TileEntityContainer) worldIn.getTileEntity(pos);
            Random rand = new Random();
            for (int i = 0; i < entity.getSizeInventory(); i++) {
                ItemStack item = entity.getStackInSlot(i);

                if (item != null && item.stackSize > 0) {
                    float rx = rand.nextFloat() * 0.8F + 0.1F;
                    float ry = rand.nextFloat() * 0.8F + 0.1F;
                    float rz = rand.nextFloat() * 0.8F + 0.1F;

                    EntityItem entityItem = new EntityItem(worldIn, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                                                           new ItemStack(item.getItem(), item.stackSize,
                                                                         item.getItemDamage()));

                    if (item.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                    }

                    float factor = 0.05F;
                    entityItem.motionX = rand.nextGaussian() * factor;
                    entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                    entityItem.motionZ = rand.nextGaussian() * factor;
                    worldIn.spawnEntityInWorld(entityItem);
                    item.stackSize = 0;
                }
            }
        }
    }
}
