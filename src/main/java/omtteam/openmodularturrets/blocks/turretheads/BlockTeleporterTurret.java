package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

import java.util.Random;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {
    public boolean shouldAnimate = false;

    public BlockTeleporterTurret() {
        super();

        this.setUnlocalizedName(Names.Blocks.teleporterTurret);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TeleporterTurretTileEntity();
    }


    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
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
