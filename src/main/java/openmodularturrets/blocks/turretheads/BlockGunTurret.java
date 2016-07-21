package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.GunTurretTileEntity;

public class BlockGunTurret extends BlockAbstractTurretHead {
    public BlockGunTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.unlocalisedGunTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int par2) {
        return new GunTurretTileEntity();
    }
}
