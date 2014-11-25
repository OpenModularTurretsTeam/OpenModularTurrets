package openmodularturrets.blocks.turretbases;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.ModInfo;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.misc.ConfigHandler;
import openmodularturrets.tileentity.turretBase.TurretBaseTierTwoTileEntity;

public class BlockTurretBaseTierTwo extends BlockAbstractTurretBase {

    public final int MaxCharge = ConfigHandler.getBaseTierTwoMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierTwoMaxIo();

    public BlockTurretBaseTierTwo() {
        super();

        this.setBlockName(BlockNames.unlocalisedTurretBaseTierTwo);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierTwo");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierTwo");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TurretBaseTierTwoTileEntity(this.MaxCharge, this.MaxIO);
    }
}
