package omtteam.openmodularturrets.block.turret;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.block.BlockAbstractTileEntity;
import omtteam.omlib.compat.theoneprobe.TOPInfoProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.MathUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.ITurretBaseAddonBlock;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.item.ItemBlockTurret;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.turret.TurretTileEntity;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static omtteam.omlib.compat.theoneprobe.TOPCompatibility.getLocalizationString;
import static omtteam.omlib.util.GeneralUtil.getBooleanUnlocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.getColoredBooleanColor;
import static omtteam.openmodularturrets.tileentity.turret.TurretMount.getTurretBaseFacing;

/**
 * A turret block (1.12.2 {@code BlockAbstractTurretHead} and its 14 subclasses; 1.20.1+ {@code TurretBlock}): one
 * instance per turret type, registered under the 3.x id. What the turret does comes from its definition.
 */
@SuppressWarnings("deprecation")
public class BlockTurret extends BlockAbstractTileEntity implements IHasItemBlock, ITurretBaseAddonBlock, TOPInfoProvider {
    public static final PropertyBool CONCEALED = PropertyBool.create("concealed");

    /** Hardness when {@code turretBreakable} (as on 1.20.1+). */
    private static final float BREAKABLE_HARDNESS = 3.0F;

    private final TurretType coreType;

    /**
     * @param id       registry name: the 3.x id for built-in turrets, the type's id for turrets of other mods
     * @param coreType the turret's rules
     */
    public BlockTurret(ResourceLocation id, TurretType coreType) {
        super(Material.GLASS);
        this.coreType = coreType;
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (OMTConfig.TURRETS.turretBreakable) {
            this.setHardness(BREAKABLE_HARDNESS);
            this.setHarvestLevel("pickaxe", 0);
        } else {
            this.setBlockUnbreakable();
        }
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONCEALED, false));
        // built-in turrets keep their 3.x key (tile.<id>.name), other mods' turrets use tile.<namespace>.<path>.name
        this.setTranslationKey(id.getNamespace().equals(Reference.MOD_ID) ? id.getPath()
                : id.getNamespace() + "." + id.getPath());
        this.setRegistryName(id);
    }

    /** Ambient particles of the 1.12.2 melee, relativistic and railgun turrets: coloured redstone dust. */
    public static TurretDefinition.AmbientEffect dust(int red, int green, int blue, boolean onlyDeployed) {
        return (world, pos, random) -> {
            IBlockState state = world.getBlockState(pos);
            if (onlyDeployed && state.getBlock() instanceof BlockTurret && state.getValue(CONCEALED)) return;
            for (int i = 0; i <= 5; i++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + random.nextGaussian() / 10 + 0.5F,
                        pos.getY() + 0.5F, pos.getZ() + random.nextGaussian() / 10 + 0.5F, red, green, blue);
            }
        };
    }

    /** The rules of this turret. */
    public TurretType getCoreType() {
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
    @ParametersAreNonnullByDefault
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
     * A turret needs a base next to it that takes this type on that side ({@link TurretPlacement#canAttach}: tier,
     * side, free turret slot); the first neighbouring base in {@link EnumFacing} order is used.
     */
    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt_OM(World worldIn, BlockPos pos) {
        TurretPlacement.Result result = attachResult(worldIn, pos);
        return result != null && result.allowed();
    }

    /**
     * Whether this turret may be attached at {@code pos} to the base next to it ({@link TurretPlacement#canAttach});
     * also asked on the client, for the reason the placement outline shows.
     *
     * @return {@code null} if there is no base next to {@code pos}
     */
    @Nullable
    public TurretPlacement.Result attachResult(World world, BlockPos pos) {
        EnumFacing towardsBase = getTurretBaseFacing(world, pos);
        if (towardsBase == null) return null;
        BlockPos basePos = pos.offset(towardsBase);
        TileEntity tile = world.getTileEntity(basePos);
        if (!(tile instanceof TurretBase)) return null;
        TurretBase base = (TurretBase) tile;
        return TurretPlacement.canAttach(coreType, TurretBase.toFace(towardsBase.getOpposite()), base.getTier(),
                attachedTurrets(world, basePos), ConfigLogicSettings.INSTANCE);
    }

    /**
     * The turrets facing away from the base at {@code basePos}, read from the world: the base's logic only learns
     * them on the server, but the placement outline asks on the client. Unloaded neighbours count as empty.
     */
    private static Map<Face, TurretType> attachedTurrets(World world, BlockPos basePos) {
        Map<Face, TurretType> turrets = new EnumMap<>(Face.class);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighbour = basePos.offset(facing);
            if (!world.isBlockLoaded(neighbour)) continue;
            Block block = world.getBlockState(neighbour).getBlock();
            if (block instanceof BlockTurret) {
                turrets.put(TurretBase.toFace(facing), ((BlockTurret) block).getCoreType());
            }
        }
        return turrets;
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
    public boolean causesSuffocation_OM(IBlockState state) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onNeighborChange_OM(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity turret = world.getTileEntity(pos);
        // getBase() is null without a world too, so getWorld() is set when this destroys the block
        if (turret instanceof TurretTileEntity && ((TurretTileEntity) turret).getBase() == null) {
            turret.getWorld().destroyBlock(pos, true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        TurretDefinition.AmbientEffect effect = TurretRegistry.get(coreType).ambientEffect();
        if (effect != null) {
            effect.spawn(world, pos, random);
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
                + ConfigLogicSettings.INSTANCE.turret(coreType).baseDamage(), probeInfo.defaultTextStyle());
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
