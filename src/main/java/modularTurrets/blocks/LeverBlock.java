package modularTurrets.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.tileentity.LeverTileEntity;
import modularTurrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class LeverBlock extends BlockContainer {

    public LeverBlock() {
        super(Material.rock);
        this.setBlockName(BlockNames.unlocalisedLever);
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
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
	    EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        float l = 0;

        if (par1World.getTileEntity(par2 + 1, par3, par4) instanceof TurretWoodBase) {
            l = 270F;
        }

        if (par1World.getTileEntity(par2 - 1, par3, par4) instanceof TurretWoodBase) {
            l = 90F;
        }

        if (par1World.getTileEntity(par2, par3, par4 + 1) instanceof TurretWoodBase) {
            l = 0F;
        }

        if (par1World.getTileEntity(par2, par3, par4 - 1) instanceof TurretWoodBase) {
            l = 180;
        }

        int shu = MathHelper.floor_double((double) (l * 4.0F / 360.0F) + 0.5D) & 3;
	    par1World.setBlockMetadataWithNotify(par2, par3, par4, shu, 2);
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3,
	    int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
	    float par8, float par9) {

        TurretWoodBase base;
        LeverTileEntity lever = (LeverTileEntity) par1World.getTileEntity(
                par2, par3, par4);

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 0
            && par1World.getTileEntity(par2, par3, par4 + 1) instanceof TurretWoodBase) {
            base = (TurretWoodBase) par1World.getTileEntity(par2, par3,
                    par4 + 1);
            if (base != null) {
            lever.isTurning = true;
            if (lever.rotation == 0F) {
                par1World.playSoundEffect(par2, par3, par4,
                    "modularturrets:windup", 1.0F, 1.0F);
                base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
            }
            }
        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 90
            && par1World.getTileEntity(par2 - 1, par3, par4) instanceof TurretWoodBase) {
            base = (TurretWoodBase) par1World.getTileEntity(par2 - 1,
                    par3, par4);
            if (base != null) {
            lever.isTurning = true;
            if (lever.rotation == 0F) {
                par1World.playSoundEffect(par2, par3, par4,
                    "modularturrets:windup", 1.0F, 1.0F);
                base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
            }
            }

        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 180
            && par1World.getTileEntity(par2, par3, par4 - 1) instanceof TurretWoodBase) {
            base = (TurretWoodBase) par1World.getTileEntity(par2, par3,
                    par4 - 1);
            if (base != null) {
            lever.isTurning = true;
            if (lever.rotation == 0F) {
                par1World.playSoundEffect(par2, par3, par4,
                    "modularturrets:windup", 1.0F, 1.0F);
                base.receiveEnergy(ForgeDirection.UNKNOWN, 50, false);
            }
            }
        }

        if ((par1World.getBlockMetadata(par2, par3, par4) * 90) == 270
            && par1World.getTileEntity(par2 + 1, par3, par4) instanceof TurretWoodBase) {
            base = (TurretWoodBase) par1World.getTileEntity(par2 + 1,
                    par3, par4);
            if (base != null) {
            lever.isTurning = true;
                if (lever.rotation == 0F) {
                    par1World.playSoundEffect(par2, par3, par4,
                        "modularturrets:windup", 1.0F, 1.0F);
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

    public boolean renderAsNormalBlock() {
	return false;
    }
}
