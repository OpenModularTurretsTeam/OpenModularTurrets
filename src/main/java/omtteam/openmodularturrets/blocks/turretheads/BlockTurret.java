package omtteam.openmodularturrets.blocks.turretheads;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.blocks.BlockAbstractTileEntity;
import omtteam.omlib.compatibility.theoneprobe.TOPInfoProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.api.lists.TurretList;
import omtteam.openmodularturrets.items.blocks.ItemBlockTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretTileEntity;
import omtteam.openmodularturrets.turret.TurretDefinition;
import omtteam.openmodularturrets.turret.TurretRegistry;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

import static omtteam.omlib.compatibility.theoneprobe.TOPCompatibility.getLocalizationString;
import static omtteam.omlib.util.GeneralUtil.getBooleanUnlocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.getColoredBooleanColor;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getTurretBaseFacing;

/**
 * A turret block (1.12.2 {@code BlockAbstractTurretHead} and its 14 subclasses; 1.20.1+ {@code TurretBlock}): one
 * instance per turret type, registered under the 3.x id. What the turret does comes from its definition.
 */
@SuppressWarnings("deprecation")
public class BlockTurret extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock, TOPInfoProvider {
    public static final PropertyBool CONCEALED = PropertyBool.create("concealed");

    private final String name;
    private final omtteam.openmodularturrets.core.turret.TurretType coreType;

    /**
     * @param name     registry name (the 3.x id) and key of the 3.x turret type in {@link TurretList}
     * @param coreType the turret's rules
     */
    public BlockTurret(String name, omtteam.openmodularturrets.core.turret.TurretType coreType) {
        super(Material.GLASS);
        this.name = name;
        this.coreType = coreType;
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONCEALED, false));
        this.setUnlocalizedName(name);
        this.setRegistryName(Reference.MOD_ID, name);
    }

    /** Ambient particles of the 1.12.2 melee, relativistic and railgun turrets: coloured redstone dust. */
    public static TurretDefinition.AmbientEffect dust(int red, int green, int blue, boolean onlyDeployed) {
        return (state, world, pos, random) -> {
            if (onlyDeployed && state.getValue(CONCEALED)) return;
            for (int i = 0; i <= 5; i++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + random.nextGaussian() / 10 + 0.5F,
                        pos.getY() + 0.5F, pos.getZ() + random.nextGaussian() / 10 + 0.5F, red, green, blue);
            }
        };
    }

    /** The 3.x turret type (block id and config section). */
    public TurretType getTurretType() {
        return TurretList.getTurretType(name);
    }

    /** The rules of this turret. */
    public omtteam.openmodularturrets.core.turret.TurretType getCoreType() {
        return coreType;
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockTurret(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity_OM(World world, IBlockState state) {
        return new TurretTileEntity();
    }

    @Override
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox_OM(IBlockState state, IBlockAccess source, BlockPos pos) {
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
    public BlockStateContainer createBlockState_OM() {
        return new BlockStateContainer(this, CONCEALED);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube_OM(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFullBlock_OM(IBlockState state) {
        return false;
    }

    /**
     * A turret needs a base of a high enough tier with a free turret slot, and must be allowed on that side of the
     * base ({@code TurretType.canMountOn}: the melee turret only on top or below).
     */
    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt_OM(World worldIn, BlockPos pos) {
        TurretBase base = getBase(worldIn, pos);
        EnumFacing towardsBase = getTurretBaseFacing(worldIn, pos);
        return base != null && towardsBase != null && base.getLogic().supportsTurret(coreType)
                && coreType.canMountOn(TurretBase.toFace(towardsBase.getOpposite()))
                && base.getRemainingTurretSlots(coreType) > 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy_OM(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy_OM(worldIn, pos, state, placer, stack);
        TurretBase base = getBase(worldIn, pos);
        if (base != null) {
            // a turret reaching further than the old max range raises the range (TurretBaseLogic.updateNeighbours)
            base.updateNeighbours();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean causesSuffocation_OM(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onNeighborChange_OM(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity turret = world.getTileEntity(pos);
        if (turret instanceof TurretTileEntity && ((TurretTileEntity) turret).getBase() == null
                && turret.getWorld() != null) {
            turret.getWorld().destroyBlock(pos, true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        TurretDefinition.AmbientEffect effect = TurretRegistry.get(coreType).ambientEffect();
        if (effect != null) {
            effect.display(state, world, pos, random);
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (!(te instanceof TurretTileEntity) || ((TurretTileEntity) te).getBase() == null) {
            return;
        }
        TurretTileEntity turret = (TurretTileEntity) te;
        TurretBase base = turret.getBase();
        boolean active = base.isActive();
        probeInfo.text("§6" + getLocalizationString(OMLibNames.Localizations.GUI.ACTIVE)
                + ": " + getColoredBooleanColor(active)
                + getLocalizationString(getBooleanUnlocalizationYesNo(active)), probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMLibNames.Localizations.GUI.OWNER)
                + ": §F" + base.getOwner().getName(), probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMTNames.Localizations.GUI.AMMO) + ": §F"
                + turret.getAmmoLevel(), probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMTNames.Localizations.GUI.DAMAGE) + ": §F"
                + getTurretType().getSettings().baseDamage, probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMTNames.Localizations.GUI.DAMAGE_AMP) + ": §F"
                + String.format("%.2f", turret.getTurretDamageAmpBonus() * 100 * turret.getDamageAmpLevel()) + "%",
                probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMTNames.Localizations.GUI.ACCURACY) + ": §F"
                + String.format("%.2f", Math.min(100F, 100F - turret.getActualTurretAccuracyDeviation())) + "%",
                probeInfo.defaultTextStyle());
        probeInfo.text("§6" + getLocalizationString(OMTNames.Localizations.GUI.RATE_OF_FIRE) + ": §F"
                + String.format("%.2f", 20F / turret.getFireCooldownTicks()) + "s/sec", probeInfo.defaultTextStyle());
    }
}
