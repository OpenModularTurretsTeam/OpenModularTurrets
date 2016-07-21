package openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

import java.util.Random;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {
    public boolean shouldAnimate = false;

    public BlockTeleporterTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.unlocalisedTeleporterTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TeleporterTurretTileEntity();
    }




    @Override
    //public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        if (shouldAnimate) {
            for (int i = 0; i <= 25; i++) {
                float var21 = (rand.nextFloat() - 0.5F) * 0.2F;
                float var22 = (rand.nextFloat() - 0.5F) * 0.2F;
                float var23 = (rand.nextFloat() - 0.5F) * 0.2F;
                worldIn.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + 0.5f + rand.nextGaussian(),
                        pos.getY() + 0.5f + rand.nextGaussian(),
                        pos.getZ() + 0.5f + rand.nextGaussian(), (double) var21,
                                          (double) var22, (double) var23);
            }
            shouldAnimate = false;
        }
    }
}
