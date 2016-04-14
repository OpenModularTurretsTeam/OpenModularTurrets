package openmodularturrets.blocks.turretbases;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class BlockTurretBaseTierOne extends BlockAbstractTurretBase {
    private final int MaxCharge = ConfigHandler.getBaseTierOneMaxCharge();
    private final int MaxIO = ConfigHandler.getBaseTierOneMaxIo();

    public BlockTurretBaseTierOne() {
        super();
        this.setResistance(ConfigHandler.getBaseTierOneBlastResistance());
        this.setBlockName(Names.Blocks.unlocalisedTurretBaseTierOne);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierOne");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierOne");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new TurretBaseTierOneTileEntity(this.MaxCharge, this.MaxIO);
    }
}
