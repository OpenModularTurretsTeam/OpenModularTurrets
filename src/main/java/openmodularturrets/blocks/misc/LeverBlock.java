package openmodularturrets.blocks.misc;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModularTurrets;
import openmodularturrets.blocks.util.BlockAbstract;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.LeverTileEntity;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class LeverBlock extends BlockAbstract implements ITileEntityProvider {
    public LeverBlock() {
        super(Material.rock);
        this.setBlockName(Names.Blocks.unlocalisedLever);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setHardness(2F);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setResistance(15F);
        this.setStepSound(Block.soundTypeStone);
    }

    @Override
    public void registerBlockIcons(IIconRegister icon) {
        blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierTwo");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new LeverTileEntity();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return (world.getTileEntity(x + 1, y, z) instanceof TurretBaseTierOneTileEntity ||
                world.getTileEntity(x - 1, y, z) instanceof TurretBaseTierOneTileEntity ||
                world.getTileEntity(x, y, z + 1) instanceof TurretBaseTierOneTileEntity ||
                world.getTileEntity(x, y, z - 1) instanceof TurretBaseTierOneTileEntity);
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        float l = 0;
        if (par1World.getTileEntity(par2 + 1, par3, par4) instanceof TurretBaseTierOneTileEntity) {
            l = 270F;
        }
        if (par1World.getTileEntity(par2 - 1, par3, par4) instanceof TurretBaseTierOneTileEntity) {
            l = 90F;
        }
        if (par1World.getTileEntity(par2, par3, par4 + 1) instanceof TurretBaseTierOneTileEntity) {
            l = 0F;
        }
        if (par1World.getTileEntity(par2, par3, par4 - 1) instanceof TurretBaseTierOneTileEntity) {
            l = 180;
        }
        int shu = MathHelper.floor_double((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, shu, 2);
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        TurretBaseTierOneTileEntity base;
        LeverTileEntity lever = (LeverTileEntity) par1World.getTileEntity(par2, par3, par4);

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 0 && par1World.getTileEntity(par2, par3,
                                                                                                par4 + 1) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) par1World.getTileEntity(par2, par3, par4 + 1);
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    par1World.playSoundEffect(par2, par3, par4, "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
                }
            }
        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 90 && par1World.getTileEntity(par2 - 1, par3,
                                                                                                 par4) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) par1World.getTileEntity(par2 - 1, par3, par4);
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    par1World.playSoundEffect(par2, par3, par4, "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
                }
            }
        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 180 && par1World.getTileEntity(par2, par3,
                                                                                                  par4 - 1) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) par1World.getTileEntity(par2, par3, par4 - 1);
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    par1World.playSoundEffect(par2, par3, par4, "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
                }
            }
        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 270 && par1World.getTileEntity(par2 + 1, par3,
                                                                                                  par4) instanceof TurretBaseTierOneTileEntity) {
            base = (TurretBaseTierOneTileEntity) par1World.getTileEntity(par2 + 1, par3, par4);
            if (base != null) {
                lever.isTurning = true;
                if (lever.rotation == 0F) {
                    par1World.playSoundEffect(par2, par3, par4, "openmodularturrets:windup", 1.0F, 1.0F);
                    base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
                }
            }
        }
        return true;
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
