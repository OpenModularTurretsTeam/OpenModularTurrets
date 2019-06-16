package omtteam.openmodularturrets.blocks.turretheads;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.reference.OMLibNames;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockTeleporterTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.TeleporterTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class BlockTeleporterTurret extends BlockAbstractTurretHead {
    public boolean shouldAnimate = false;

    public BlockTeleporterTurret() {
        super();

        this.setUnlocalizedName(OMTNames.Blocks.teleporterTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.teleporterTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockTeleporterTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
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

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TurretHead) {
            TurretHead turret = (TurretHead) te;
            boolean active = turret.getBase().isActive();

            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
            String ownerName = turret.getBase().getOwner().getName();
            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
            probeInfo.text("\u00A76" + safeLocalize(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": " + String.format("%.2f", 20F / (turret.getTurretBaseFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(turret.getBase(), turret)))) + "s/sec");
        }
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.teleporterTurret);
    }
}
