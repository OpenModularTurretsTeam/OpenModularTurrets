package omtteam.openmodularturrets.block.expander;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.block.IHasItemBlock;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.config.ExpanderSetting;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Inventory expander (tiers 1–3, as on 1.20.1+ {@code InventoryExpanderBlock}): it has no inventory of its own, but
 * every ammo slot of a turret base and fuel slot of a generator next to it holds more ({@code stacks} of the tier's
 * config, see {@link SlotCapacity}). It is placed against a turret base or generator ({@link #isMachine}) and counts
 * for every machine next to it; a block behind it does not count. Two expanders of one tier fit into one block space,
 * like slabs ({@link #DOUBLE}: a second plate stacked onto the first, see {@link ItemBlockInventoryExpander}); such a
 * block counts as two ({@value SlotCapacity#MAX_STACKED} plates on a face at most). {@link #FACING} points at the
 * block the plates are placed against. Metadata: bits 0–2 the facing, bit 3 double.
 */
public class BlockInventoryExpander extends Block implements IHasItemBlock {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    /** A second plate stacked onto the first, on its side away from {@link #FACING}. */
    public static final PropertyBool DOUBLE = PropertyBool.create("double");

    /** Depth of a plate in pixels. */
    private static final int PLATE_DEPTH = 6;
    /** The 12 × 12 × 6 plate against the {@link #FACING} side. */
    private static final Map<EnumFacing, AxisAlignedBB> SHAPES = shapes(PLATE_DEPTH);
    /** Both plates of a double block, 12 deep from the {@link #FACING} side. */
    private static final Map<EnumFacing, AxisAlignedBB> DOUBLE_SHAPES = shapes(2 * PLATE_DEPTH);

    private final int tier;

    /** @param tier 1–3 */
    public BlockInventoryExpander(int tier, String name) {
        super(Material.ROCK);
        this.tier = tier;
        ExpanderSetting defaults = setting();
        this.setHardness((float) defaults.hardness);
        this.setResistance((float) defaults.explosionResistance);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 0);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH)
                .withProperty(DOUBLE, false));
        this.setTranslationKey(name);
        this.setRegistryName(Reference.MOD_ID, name);
    }

    /** A 12 × 12 box {@code depth} pixels deep against each side. */
    private static Map<EnumFacing, AxisAlignedBB> shapes(int depth) {
        double d = depth / 16.0D;
        double lo = 2 / 16.0D;
        double hi = 14 / 16.0D;
        Map<EnumFacing, AxisAlignedBB> shapes = new EnumMap<>(EnumFacing.class);
        shapes.put(EnumFacing.NORTH, new AxisAlignedBB(lo, lo, 0, hi, hi, d));
        shapes.put(EnumFacing.SOUTH, new AxisAlignedBB(lo, lo, 1 - d, hi, hi, 1));
        shapes.put(EnumFacing.WEST, new AxisAlignedBB(0, lo, lo, d, hi, hi));
        shapes.put(EnumFacing.EAST, new AxisAlignedBB(1 - d, lo, lo, 1, hi, hi));
        shapes.put(EnumFacing.DOWN, new AxisAlignedBB(lo, 0, lo, hi, d, hi));
        shapes.put(EnumFacing.UP, new AxisAlignedBB(lo, 1 - d, lo, hi, 1, hi));
        return shapes;
    }

    /** 2 for a double block, otherwise 1. */
    public static int plates(IBlockState state) {
        return state.getValue(DOUBLE) ? 2 : 1;
    }

    /** 1–3. */
    public int getTier() {
        return tier;
    }

    /** Stacks, hardness and explosion resistance of the tier from the config in effect (the server's on clients). */
    public ExpanderSetting setting() {
        return OMTConfig.EXPANDERS.tier(tier);
    }

    /**
     * The tiers of the plates of {@code state} as a machine next to it counts them: one or two (a double block), empty
     * if it is no expander.
     */
    public static List<Integer> plateTiers(IBlockState state) {
        if (!(state.getBlock() instanceof BlockInventoryExpander)) return Collections.emptyList();
        return Collections.nCopies(plates(state), ((BlockInventoryExpander) state.getBlock()).tier);
    }

    /**
     * Whether an expander may be placed against {@code state}: a turret base or a generator (by registry name, so this
     * package stays out of the package cycles of the machine blocks).
     */
    public static boolean isMachine(IBlockState state) {
        ResourceLocation id = state.getBlock().getRegistryName();
        return id != null && id.getNamespace().equals(Reference.MOD_ID)
                && (id.getPath().equals(OMTNames.Blocks.turretBase) || id.getPath().equals(OMTNames.Blocks.generator));
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockInventoryExpander(block);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, DOUBLE);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7))
                .withProperty(DOUBLE, (meta & 8) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(DOUBLE) ? 8 : 0);
    }

    /** Only against a turret base or generator ({@link #isMachine}); stacking a second plate is the item's job. */
    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockOnSide(world, pos, side)
                && isMachine(world.getBlockState(pos.offset(side.getOpposite())));
    }

    /** The plate goes against the clicked block. */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return (state.getValue(DOUBLE) ? DOUBLE_SHAPES : SHAPES).get(state.getValue(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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

    /** One expander per plate. */
    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return plates(state);
    }

    /** The configured hardness (clients mine with the server's, see {@link #setting}). */
    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return (float) setting().hardness;
    }

    /** The configured explosion resistance. */
    @Override
    @ParametersAreNonnullByDefault
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return (float) setting().explosionResistance;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
            return;
        }
        tooltip.add(TextFormatting.GRAY + I18n.format(OMTNames.Localizations.Text.INVENTORY_EXPANDER_DESC1,
                setting().stacks));
        tooltip.add(TextFormatting.GRAY + safeLocalize(OMTNames.Localizations.Text.INVENTORY_EXPANDER_DESC2));
    }
}
