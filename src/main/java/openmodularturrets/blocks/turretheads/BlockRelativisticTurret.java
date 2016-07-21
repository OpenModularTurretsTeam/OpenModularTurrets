package openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

import java.util.Random;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {
    public BlockRelativisticTurret() {
        super();
        this.setUnlocalizedName(Names.Blocks.unlocalisedRelativisticTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new RelativisticTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        for (int i = 0; i <= 5; i++) {
            Random random = new Random();
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + (random.nextGaussian() / 10) + 0.5F, pos.getY() + 0.5F,
                                      pos.getZ() + (random.nextGaussian() / 10) + 0.5F, (200), (200), (200));
        }
    }
}
