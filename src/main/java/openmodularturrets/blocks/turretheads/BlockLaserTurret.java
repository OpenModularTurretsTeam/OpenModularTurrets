package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;

public class BlockLaserTurret extends BlockAbstractTurretHead {

    public BlockLaserTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedLaserTurret);
        this.setBlockTextureName(ModInfo.ID + ":laserTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int par2) {
        return new LaserTurretTileEntity();
    }
}
