package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockMachineGunTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.GunTurretTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockGunTurret extends BlockAbstractTurretHead {
    public BlockGunTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.gunTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.gunTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockMachineGunTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new GunTurretTileEntity();
    }
}
