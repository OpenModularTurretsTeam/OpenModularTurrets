package openmodularturrets.blocks.turretbases;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turretbase.TurretBaseTierTwoTileEntity;

public class BlockTurretBaseTierTwo extends BlockAbstractTurretBase {

    public final int MaxCharge = ConfigHandler.getBaseTierTwoMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierTwoMaxIo();

    public BlockTurretBaseTierTwo() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedTurretBaseTierTwo);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierTwo");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierTwo");
    }

    @Override
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_, int p_149673_3_, int p_149673_4_, int p_149673_5_) {
        if (this.camoStack == null) {
            return blockIcon;
        } else {
            return this.camoStack.getIconIndex();
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TurretBaseTierTwoTileEntity(this.MaxCharge, this.MaxIO);
    }
}
