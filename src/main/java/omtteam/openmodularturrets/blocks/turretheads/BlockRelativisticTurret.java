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
import omtteam.openmodularturrets.items.blocks.ItemBlockRelativisticTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.turrets.RelativisticTurretTileEntity;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class BlockRelativisticTurret extends BlockAbstractTurretHead {
    public BlockRelativisticTurret() {
        super();
        this.setUnlocalizedName(OMTNames.Blocks.relativisticTurret);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.relativisticTurret);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockRelativisticTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
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

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TurretHead) {
            TurretHead turret = (TurretHead) te;
            boolean active = turret.getBase().isActive();

            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
            String ownerName = turret.getBase().getOwner().getName();
            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
        }
    }

    @Override
    public TurretType getTurretType() {
        return TurretList.getTurretType(OMTNames.Blocks.relativisticTurret);
    }
}
