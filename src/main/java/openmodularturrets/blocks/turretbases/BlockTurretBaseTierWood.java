package openmodularturrets.blocks.turretbases;

import openmodularturrets.ModInfo;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.misc.ConfigHandler;
import openmodularturrets.tileentity.turretBase.TurretWoodBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTurretBaseTierWood extends BlockAbstractTurretBase {

    public final int MaxCharge = ConfigHandler.getBaseTierWoodMaxCharge();
    public final int MaxIO = ConfigHandler.getBaseTierWoodMaxIo();

    public BlockTurretBaseTierWood() {
        super();

        this.setBlockName(BlockNames.unlocalisedTurretBaseTierWood);
        this.setBlockTextureName(ModInfo.ID + ":turretBaseTierWood");
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        super.registerBlockIcons(p_149651_1_);

        blockIcon = p_149651_1_.registerIcon(ModInfo.ID.toLowerCase() + ":turretBaseTierWood");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
	    return new TurretWoodBase(this.MaxCharge, this.MaxIO);
    }
}
