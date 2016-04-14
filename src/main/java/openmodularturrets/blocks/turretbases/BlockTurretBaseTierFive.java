package openmodularturrets.blocks.turretbases;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFiveTileEntity;

public class BlockTurretBaseTierFive extends BlockAbstractTurretBase {
    private final int MaxCharge = ConfigHandler.getBaseTierFiveMaxCharge();
    private final int MaxIO = ConfigHandler.getBaseTierFiveMaxIo();

    public BlockTurretBaseTierFive() {
        super();
        this.setResistance(ConfigHandler.getBaseTierFiveBlastResistance());
        this.setBlockName(Names.Blocks.unlocalisedTurretBaseTierFive);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierFive");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierFive");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TurretBaseTierFiveTileEntity(this.MaxCharge, this.MaxIO);
    }
}
