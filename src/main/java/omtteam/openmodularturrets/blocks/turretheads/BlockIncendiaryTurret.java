package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockIncendiaryTurret;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.IncendiaryTurretTileEntity;

public class BlockIncendiaryTurret extends BlockAbstractTurretHead {
    public BlockIncendiaryTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.incendiaryTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.incendiaryTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockIncendiaryTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new IncendiaryTurretTileEntity();
    }
}
