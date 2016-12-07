package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockLaserTurret;
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
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockLaserTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new LaserTurretTileEntity();
    }
}
