package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;

import java.util.Random;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {
    public BlockRelativisticTurret() {
        super();
        this.setBlockName(Names.Blocks.unlocalisedRelativisticTurret);
        this.setBlockTextureName(ModInfo.ID + ":relativisticTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new RelativisticTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
        for (int i = 0; i <= 5; i++) {
            Random random = new Random();
            p_149734_1_.spawnParticle("reddust", p_149734_2_ + (random.nextGaussian() / 10) + 0.5F, p_149734_3_ + 0.5F,
                                      p_149734_4_ + (random.nextGaussian() / 10) + 0.5F, (200), (200), (200));
        }
    }
}
