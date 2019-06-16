package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockDisposableTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

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
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new DisposableItemTurretTileEntity();
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.disposableItemTurret);
    }
}
