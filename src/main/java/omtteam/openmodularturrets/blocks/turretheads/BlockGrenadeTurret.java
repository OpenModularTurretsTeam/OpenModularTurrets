package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;

public class BlockGrenadeTurret extends BlockAbstractTurretHead {
    public BlockGrenadeTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.grenadeTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.grenadeTurret);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new GrenadeLauncherTurretTileEntity();
    }
}
