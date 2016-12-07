package omtteam.openmodularturrets.blocks.turretheads;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.items.blocks.ItemBlockRelativisticTurret;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

import java.util.Random;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {
    public BlockRelativisticTurret() {
        super();
        this.setUnlocalizedName(Names.Blocks.relativisticTurret);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.relativisticTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockRelativisticTurret(block);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new RelativisticTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i <= 5; i++) {
            Random random = new Random();
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + (random.nextGaussian() / 10) + 0.5F, pos.getY() + 0.5F,
                    pos.getZ() + (random.nextGaussian() / 10) + 0.5F, (200), (200), (200));
        }
    }
}
