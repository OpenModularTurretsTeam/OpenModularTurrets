package openmodularturrets.blocks.turretbases;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFourTileEntity;

public class BlockTurretBaseTierFour extends BlockAbstractTurretBase {
    public final int MaxCharge = ConfigHandler.getBaseTierFourMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierFourMaxIo();

    public BlockTurretBaseTierFour() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedTurretBaseTierFour);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierFour");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierFour");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TurretBaseTierFourTileEntity(this.MaxCharge, this.MaxIO);
    }
}
