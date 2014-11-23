package modularTurrets.blocks;

import java.util.List;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.tileEntities.turretBase.TurretWoodBase;
import modularTurrets.tileEntities.turrets.DisposableItemTurretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DisposableTurretBlock extends BlockContainer {

	public DisposableTurretBlock(int par1) {
		super(par1, Material.rock);
		this.setUnlocalizedName(BlockNames.unlocalisedDisposableItemTurret);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setHardness(-1F);
		this.setBlockBounds(0, 0, 0, 0.0F, 0.0F, 0.0F);
		this.setResistance(20F);
		this.setStepSound(Block.soundStoneFootstep);
		this.setLightValue(0.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister icon) {
		blockIcon = icon.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierTwo");
	}	

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new DisposableItemTurretTileEntity();
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
