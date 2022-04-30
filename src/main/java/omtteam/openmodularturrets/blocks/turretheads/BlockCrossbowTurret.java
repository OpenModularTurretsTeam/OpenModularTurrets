package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockCrossbowTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.CrossbowTurretTileEntity;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockCrossbowTurret extends BlockAbstractTurretHead {
    public BlockCrossbowTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.crossbowTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.crossbowTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockCrossbowTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity_OM(World world, IBlockState state) {
        return new CrossbowTurretTileEntity();
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.crossbowTurret);
    }
}
