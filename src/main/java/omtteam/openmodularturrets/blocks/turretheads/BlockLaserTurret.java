package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.tileentity.turrets.LaserTurretTileEntity;

public class BlockLaserTurret extends BlockAbstractTurretHead {
    public BlockLaserTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.laserTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new LaserTurretTileEntity();
    }
}
