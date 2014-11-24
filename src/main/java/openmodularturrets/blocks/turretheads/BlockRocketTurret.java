package openmodularturrets.blocks.turretheads;

import openmodularturrets.ModInfo;
import openmodularturrets.blocks.BlockNames;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRocketTurret extends BlockAbstractTurretHead {

    public BlockRocketTurret() {
        super();

        this.setBlockName(BlockNames.unlocalisedRocketTurret);
        this.setBlockTextureName(ModInfo.ID + ":rocketTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
	    return new RocketTurretTileEntity();
    }
}
