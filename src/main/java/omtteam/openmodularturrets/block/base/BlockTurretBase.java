package omtteam.openmodularturrets.block.base;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.block.BlockAbstractCamoTileEntity;
import omtteam.omlib.block.UnownedMarker;
import omtteam.omlib.compat.theoneprobe.TOPCompatibility;
import omtteam.omlib.compat.theoneprobe.TOPInfoModifier;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.player.Player;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.BaseTier;
import omtteam.openmodularturrets.item.ItemBlockTurretBase;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.base.TurretBaseOverlay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mcjty.theoneprobe.apiimpl.providers.DefaultProbeInfoProvider.showStandardBlockInfo;
import static omtteam.omlib.util.player.PlayerUtil.addChatMessage;
import static omtteam.omlib.util.world.WorldUtil.getTouchingTileEntities;

public class BlockTurretBase extends BlockAbstractCamoTileEntity implements IHasItemBlock, TOPInfoModifier {
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 1, 5);
    public static final PropertyInteger LIGHT_VALUE = PropertyInteger.create("light_value", 0, 15);
    public static final PropertyInteger LIGHT_OPACITY = PropertyInteger.create("light_opacity", 0, 15);

    public BlockTurretBase() {
        super(Material.ROCK);
        if (!OMTConfig.BASES.baseBreakable) {
            this.setBlockUnbreakable();
        }
        setDefaultState(this.blockState.getBaseState().withProperty(TIER, 1).withProperty(LIGHT_VALUE, 0).withProperty(LIGHT_OPACITY, 0));
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey(OMTNames.Blocks.turretBase);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.turretBase);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockTurretBase(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity_OM(World world, IBlockState state) {
        int tier = state.getValue(TIER);
        return new TurretBase(tier >= BaseTier.MIN && tier <= BaseTier.MAX ? tier : BaseTier.MIN, state);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isOpaqueCube_OM(IBlockState blockState) {
        return false;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, meta + 1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState_OM() {
        return new ExtendedBlockState(this, new IProperty[]{TIER, LIGHT_VALUE, LIGHT_OPACITY}, new IUnlistedProperty[]{RENDERBLOCKSTATE});
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isTranslucent(IBlockState state) {
        if (state instanceof IExtendedBlockState && state.getBlock() instanceof BlockTurretBase && ((IExtendedBlockState) state).getValue(RENDERBLOCKSTATE) != null) {
            return ((IExtendedBlockState) state).getValue(RENDERBLOCKSTATE).getRenderState().isTranslucent();
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getBlock() instanceof BlockTurretBase) {
            return state.getActualState(world, pos).getValue(LIGHT_VALUE);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getBlock() instanceof BlockTurretBase) {
            return state.getActualState(world, pos).getValue(LIGHT_OPACITY);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileEntity = WorldUtil.getTileEntity(worldIn, pos);
        if (state.getBlock() instanceof BlockTurretBase && tileEntity instanceof TurretBase) {
            TurretBase base = (TurretBase) tileEntity;
            int lightValue = base.getCamoSettings().getLightValue();
            int lightOpacity = base.getCamoSettings().getLightOpacity();
            return state.withProperty(LIGHT_VALUE, lightValue).withProperty(LIGHT_OPACITY, lightOpacity);
        }
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated_OM(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            TurretBase base = (TurretBase) world.getTileEntity(pos);
            // Open GUI
            if (!player.isSneaking() && base != null) {
                if (PlayerUtil.canPlayerAccessBlock(player, base)) {
                    player.openGui(Reference.MOD_ID, 1, world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    addChatMessage(player, new TextComponentTranslation(OMLibNames.Localizations.Text.STATUS_PERMISSION));
                }
            }
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos neighbor) {
        if (!worldIn.isRemote) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null && worldIn.getRedstonePowerFromNeighbors(pos) > 0) {
                base.setRedstone(true);
            } else if (base != null && worldIn.getRedstonePowerFromNeighbors(pos) == 0) {
                base.setRedstone(false);
            }
            if (base != null) {
                base.updateNeighbours();
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy_OM(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
            stack) {

        if (worldIn.getTileEntity(pos) instanceof TurretBase) {
            EntityPlayer player = (EntityPlayer) placer;
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base == null) {
                return;
            }
            base.setCamoState(state);
            base.setOwner(new Player(player));
            if (worldIn.getRedstonePowerFromNeighbors(pos) > 0) {
                base.setRedstone(true);
            } else if (worldIn.getRedstonePowerFromNeighbors(pos) == 0) {
                base.setRedstone(false);
            }
            base.markDirty();
            switch (state.getValue(TIER)) {
                case 1:
                    this.setResistance(OMTConfig.BASES.baseTierOne.baseBlastResistance);
                    this.setHardness(OMTConfig.BASES.baseTierOne.baseHardness);
                    break;
                case 2:
                    this.setResistance(OMTConfig.BASES.baseTierTwo.baseBlastResistance);
                    this.setHardness(OMTConfig.BASES.baseTierTwo.baseHardness);
                    break;
                case 3:
                    this.setResistance(OMTConfig.BASES.baseTierThree.baseBlastResistance);
                    this.setHardness(OMTConfig.BASES.baseTierThree.baseHardness);
                    break;
                case 4:
                    this.setResistance(OMTConfig.BASES.baseTierFour.baseBlastResistance);
                    this.setHardness(OMTConfig.BASES.baseTierFour.baseHardness);
                    break;
                case 5:
                    this.setResistance(OMTConfig.BASES.baseTierFive.baseBlastResistance);
                    this.setHardness(OMTConfig.BASES.baseTierFive.baseHardness);
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt_OM(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            for (TileEntity tileEntity : getTouchingTileEntities(worldIn, pos.offset(facing))) {
                if (tileEntity instanceof TurretBase) return false;
                if (worldIn.getTileEntity(pos.offset(facing)) instanceof TurretBase) return false;
            }
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TurretBase) {
                dropItems(worldIn, pos, ((TurretBase) tileEntity).getInventory());
            }
            worldIn.removeTileEntity(pos);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        if (OMTConfig.BASES.baseBreakable) {
            switch (blockState.getValue(TIER)) {
                case 1:
                    return OMTConfig.BASES.baseTierOne.baseHardness;
                case 2:
                    return OMTConfig.BASES.baseTierTwo.baseHardness;
                case 3:
                    return OMTConfig.BASES.baseTierThree.baseHardness;
                case 4:
                    return OMTConfig.BASES.baseTierFour.baseHardness;
                case 5:
                    return OMTConfig.BASES.baseTierFive.baseHardness;
            }
            return 10.0F;
        } else {
            // as bedrock: a negative resistance would make explosions stronger, so they destroyed the base
            return 3600000.0F;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        if (OMTConfig.BASES.baseBreakable) {
            switch (world.getBlockState(pos).getValue(TIER)) {
                case 1:
                    return OMTConfig.BASES.baseTierOne.baseBlastResistance;
                case 2:
                    return OMTConfig.BASES.baseTierTwo.baseBlastResistance;
                case 3:
                    return OMTConfig.BASES.baseTierThree.baseBlastResistance;
                case 4:
                    return OMTConfig.BASES.baseTierFour.baseBlastResistance;
                case 5:
                    return OMTConfig.BASES.baseTierFive.baseBlastResistance;
            }
            return 10.0F;
        } else {
            return -1.0F;
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(0, new ItemStack(this, 1, this.getMetaFromState(state)));
        return drops;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int damageDropped(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(this, 1, i));
        }
    }

    /** Bases without an owner sparkle green, so players see they can claim them. */
    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        UnownedMarker.randomDisplayTick(world, pos, random);
    }

    /**
     * Replaces The One Probe's block info (1.20.1+ Jade): players who may not open the base see its disguise and
     * nothing else, the others the base and the lines of {@link TurretBaseOverlay}.
     */
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        IProbeConfig config = Config.getRealConfig();
        if (te instanceof TurretBase && TurretBaseOverlay.isDisguisedFor((TurretBase) te, blockState, player)) {
            IBlockState camo = ((TurretBase) te).getCamoState();
            showStandardBlockInfo(config, mode, probeInfo, camo, camo.getBlock(), world, data.getPos(), player, data);
            return;
        }
        showStandardBlockInfo(config, mode, probeInfo, blockState, this, world, data.getPos(), player, data);
        if (te instanceof TurretBase) {
            for (String line : TurretBaseOverlay.lines(TurretBaseOverlay.data((TurretBase) te),
                    TOPCompatibility::getLocalizationString)) {
                probeInfo.text(line, probeInfo.defaultTextStyle());
            }
        }
    }
}
