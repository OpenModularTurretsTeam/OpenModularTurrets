package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockDisposableTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;

public class BlockDisposableTurret extends BlockAbstractTurretHead {
    public BlockDisposableTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.disposableItemTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.disposableItemTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockDisposableTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DisposableItemTurretTileEntity();
    }
}
