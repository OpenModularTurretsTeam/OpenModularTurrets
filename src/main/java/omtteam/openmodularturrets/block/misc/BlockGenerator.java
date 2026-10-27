package omtteam.openmodularturrets.block.misc;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.omlib.util.inventory.InvUtil;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

/**
 * Generator (new in 4.0.0, as on 1.20.1+): a furnace-like fuel burner that powers adjacent turret bases. Anyone may
 * use it (no owner). {@link #FACING} is the front, {@link #LIT} is set while burning. Metadata: bits 0–1 the
 * horizontal facing, bit 2 lit; the 3.x power generator attachment is remapped to this block.
 */
public class BlockGenerator extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool LIT = PropertyBool.create("lit");
    /** GUI id for {@code OMTGuiHandler} (3.x used it for the power generator). */
    public static final int GUI_ID = 3;

    public BlockGenerator() {
        super(Material.IRON);
        this.setHardness(3.5F);
        this.setResistance(3.5F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 0);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
                .withProperty(LIT, false));
        this.setTranslationKey(OMTNames.Blocks.generator);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.generator);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
                .withProperty(LIT, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(LIT) ? 4 : 0);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /** Light 13 while burning, like a furnace. */
    @Override
    @ParametersAreNonnullByDefault
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(LIT) ? 13 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new GeneratorTileEntity();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand != EnumHand.MAIN_HAND || player.isSneaking()) {
            return false;
        }
        if (!world.isRemote && world.getTileEntity(pos) instanceof GeneratorTileEntity) {
            player.openGui(Reference.MOD_ID, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    /** Attached inventory expanders change what the fuel slots hold. */
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GeneratorTileEntity) {
            ((GeneratorTileEntity) tileEntity).onNeighbourChanged();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof GeneratorTileEntity) {
            GeneratorTileEntity generator = (GeneratorTileEntity) tileEntity;
            InvUtil.dropItems(world, pos, generator.getFuel());
            InvUtil.dropItems(world, pos, generator.getUpgrades());
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + GeneralUtil.safeLocalize("tooltip.openmodularturrets.generator"));
    }

    /** Furnace-style crackle and smoke at the front while burning. */
    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.3D + random.nextDouble() * 0.4D;
        double z = pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.1D) {
            world.playSound(x, pos.getY(), z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F,
                    false);
        }
        EnumFacing facing = state.getValue(FACING);
        double side = random.nextDouble() * 0.6D - 0.3D;
        double dx = facing.getAxis() == EnumFacing.Axis.X ? facing.getXOffset() * 0.52D : side;
        double dz = facing.getAxis() == EnumFacing.Axis.Z ? facing.getZOffset() * 0.52D : side;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + dx, y, z + dz, 0.0D, 0.0D, 0.0D);
        world.spawnParticle(EnumParticleTypes.FLAME, x + dx, y, z + dz, 0.0D, 0.0D, 0.0D);
    }
}
