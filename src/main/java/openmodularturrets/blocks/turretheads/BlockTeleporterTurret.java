package openmodularturrets.blocks.turretheads;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;

import java.util.Random;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {
    public boolean shouldAnimate = false;

    public BlockTeleporterTurret() {
        super();

        this.setBlockName(Names.Blocks.unlocalisedTeleporterTurret);
        this.setBlockTextureName(ModInfo.ID + ":teleporterTurret");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TeleporterTurretTileEntity();
    }

    @Override
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
        if (shouldAnimate) {
            for (int i = 0; i <= 25; i++) {
                float var21 = (p_149734_5_.nextFloat() - 0.5F) * 0.2F;
                float var22 = (p_149734_5_.nextFloat() - 0.5F) * 0.2F;
                float var23 = (p_149734_5_.nextFloat() - 0.5F) * 0.2F;
                p_149734_1_.spawnParticle("portal", p_149734_2_ + 0.5f + p_149734_5_.nextGaussian(),
                                          p_149734_3_ + 0.5f + p_149734_5_.nextGaussian(),
                                          p_149734_4_ + 0.5f + p_149734_5_.nextGaussian(), (double) var21,
                                          (double) var22, (double) var23);
            }
            shouldAnimate = false;
        }
    }
}
