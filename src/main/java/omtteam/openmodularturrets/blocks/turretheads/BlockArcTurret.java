package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockArcTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.ArcTurretTileEntity;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockArcTurret extends BlockAbstractTurretHead {
    public BlockArcTurret() {
        super();
        this.setUnlocalizedName(OMTNames.Blocks.arcTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.arcTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockArcTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity_OM(World world, IBlockState state) {
        return new ArcTurretTileEntity();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i <= 1; i++) {
            //worldIn.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, pos.getX() + (rand.nextGaussian() / 15) + 0.5F, pos.getY() + 1F,
            //pos.getZ() + (rand.nextGaussian() / 15) + 0.5F, (0), (0), (0));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP && this.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.arcTurret);
    }
}
