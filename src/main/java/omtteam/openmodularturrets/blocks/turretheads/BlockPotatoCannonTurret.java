package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockPotatoCannonTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.PotatoCannonTurretTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockPotatoCannonTurret extends BlockAbstractTurretHead {
    public BlockPotatoCannonTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.potatoCannonTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.potatoCannonTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockPotatoCannonTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new PotatoCannonTurretTileEntity();
    }
}
