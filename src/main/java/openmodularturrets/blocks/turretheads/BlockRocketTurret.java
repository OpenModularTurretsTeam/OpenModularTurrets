package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RocketTurretTileEntity;

public class BlockRocketTurret extends BlockAbstractTurretHead {
    public BlockRocketTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.rocketTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new RocketTurretTileEntity();
    }
}
