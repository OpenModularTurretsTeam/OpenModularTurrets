package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;

public class BlockIncendiaryTurret extends BlockAbstractTurretHead {
    public BlockIncendiaryTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.unlocalisedIncendiaryTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new IncendiaryTurretTileEntity();
    }
}
