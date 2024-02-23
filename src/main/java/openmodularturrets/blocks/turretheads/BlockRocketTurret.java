package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;

public class BlockRocketTurret extends BlockAbstractTurretHead {

    public BlockRocketTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedRocketTurret);
        this.setBlockTextureName(ModInfo.ID + ":rocketTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new RocketTurretTileEntity();
    }
}
