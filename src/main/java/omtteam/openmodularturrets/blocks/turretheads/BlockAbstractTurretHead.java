package omtteam.openmodularturrets.blocks.turretheads;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.compatibility.theoneprobe.TOPInfoProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretType;
import omtteam.openmodularturrets.util.OMTUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.compatibility.theoneprobe.TOPCompatibility.getLocalizationString;
import static omtteam.omlib.util.GeneralUtil.getBooleanUnlocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.getColoredBooleanColor;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.*;

@SuppressWarnings("deprecation")
public abstract class BlockAbstractTurretHead extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock, TOPInfoProvider {
    public static final PropertyBool CONCEALED = PropertyBool.create("concealed");

    BlockAbstractTurretHead() {
        super(Material.GLASS);

        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONCEALED, false));
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(CONCEALED)) {
            return new AxisAlignedBB(0F, 0F, 0F, 0F, 0F, 0F);
        }
        return new AxisAlignedBB(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
    }

    @Override
    public AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing, World world, BlockPos pos) {
        facing = getTurretBaseFacing(world, pos);
        if (facing != null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(-0.3F, -0.3F, -0.5F, 0.3F, 0.3F, 0.5F);
            axisAlignedBB = MathUtil.rotateAABB(axisAlignedBB, facing.getOpposite());
            return axisAlignedBB.offset(pos).offset(0.5D, 0.5D, 0.5D);
        }
        return new AxisAlignedBB(0.2F, 0.0F, 0.2F, 0.8F, 1F, 0.8F).offset(pos);
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CONCEALED, meta == 1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CONCEALED) ? 1 : 0;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONCEALED);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        TurretBase base = getBase(worldIn, pos);
        return base != null && OMTUtil.getRemainingTurretSlots(base, this.getTurretType()) > 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TurretBase base = getBase(worldIn, pos);
        if (this.getTurretType().getSettings().baseRange > base.getMaxRange()) {
            base.updateMaxRange();
            base.setRange(base.getMaxRange());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity head = world.getTileEntity(pos);
        if (head instanceof TurretHead && ((TurretHead) head).getBase() == null) {
            head.getWorld().destroyBlock(pos, true);
        }
    }

    public abstract TurretType getTurretType();

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TurretHead) {
            TurretHead turret = (TurretHead) te;
            boolean active = turret.getBase().isActive();

            probeInfo.text("\u00A76" + getLocalizationString(OMLibNames.Localizations.GUI.ACTIVE)
                                   + ": " + getColoredBooleanColor(active)
                                   + getLocalizationString(getBooleanUnlocalizationYesNo(active)), probeInfo.defaultTextStyle());

            String ownerName = turret.getBase().getOwner().getName();
            probeInfo.text("\u00A76" + getLocalizationString(OMLibNames.Localizations.GUI.OWNER)
                                   + ": \u00A7F" + ownerName, probeInfo.defaultTextStyle());

            probeInfo.text("\u00A76" + getLocalizationString(OMTNames.Localizations.GUI.AMMO) + ": \u00A7F"
                                   + getAmmoLevel(turret, turret.getBase()), probeInfo.defaultTextStyle());
            probeInfo.text("\u00A76" + getLocalizationString(OMTNames.Localizations.GUI.DAMAGE_AMP) + ": \u00A7F"
                                   + String.format("%.2f", turret.getTurretDamageAmpBonus() * 100 * getAmpLevel(turret.getBase())) + "%", probeInfo.defaultTextStyle());
            probeInfo.text("\u00A76" + getLocalizationString(OMTNames.Localizations.GUI.ACCURACY) + ": \u00A7F"
                                   + String.format("%.2f", Math.min(100F, 100F - turret.getActualTurretAccuracyDeviation())) + "%", probeInfo.defaultTextStyle());
            probeInfo.text("\u00A76" + getLocalizationString(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": \u00A7F"
                                   + String.format("%.2f", 20F / (turret.getTurretBaseFireRate()
                    / (1 + TurretHeadUtil.getFireRateUpgrades(turret.getBase(), turret)))) + "s/sec", probeInfo.defaultTextStyle());
        }
    }
}
