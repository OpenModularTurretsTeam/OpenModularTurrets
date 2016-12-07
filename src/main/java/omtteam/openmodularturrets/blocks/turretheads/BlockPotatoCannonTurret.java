package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockPotatoCannonTurret;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.PotatoCannonTurretTileEntity;

public class BlockPotatoCannonTurret extends BlockAbstractTurretHead {
    public BlockPotatoCannonTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.potatoCannonTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.potatoCannonTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockPotatoCannonTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new PotatoCannonTurretTileEntity();
    }
}
