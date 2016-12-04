package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.LaserTurretTileEntity;

public class BlockLaserTurret extends BlockAbstractTurretHead {
    public BlockLaserTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.laserTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.laserTurret);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new LaserTurretTileEntity();
    }
}
