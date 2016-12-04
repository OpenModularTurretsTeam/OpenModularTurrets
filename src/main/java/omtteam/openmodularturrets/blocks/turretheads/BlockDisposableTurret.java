package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;

public class BlockDisposableTurret extends BlockAbstractTurretHead {
    public BlockDisposableTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.disposableItemTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.disposableItemTurret);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DisposableItemTurretTileEntity();
    }
}
