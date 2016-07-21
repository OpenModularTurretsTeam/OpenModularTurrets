package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;

public class BlockLaserTurret extends BlockAbstractTurretHead {
    public BlockLaserTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.unlocalisedLaserTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new LaserTurretTileEntity();
    }
}
