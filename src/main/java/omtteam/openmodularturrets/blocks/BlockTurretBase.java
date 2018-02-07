package omtteam.openmodularturrets.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.api.IHasItemBlock;
import omtteam.omlib.blocks.BlockAbstractCamoTileEntity;
import omtteam.omlib.compatibility.theoneprobe.TOPInfoProvider;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.tileentity.EnumMachineMode;
import omtteam.omlib.util.PlayerUtil;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.UsableMetaItem;
import omtteam.openmodularturrets.items.blocks.ItemBlockTurretBase;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.*;
import static omtteam.omlib.util.PlayerUtil.addChatMessage;
import static omtteam.omlib.util.WorldUtil.getTouchingTileEntities;


@SuppressWarnings("deprecation")
public class BlockTurretBase extends BlockAbstractCamoTileEntity implements IHasItemBlock, TOPInfoProvider {
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 1, 5);

    public BlockTurretBase() {
        super(Material.ROCK);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        if (!OMTConfigHandler.turretBreakable) {
            this.setBlockUnbreakable();
        }
        setDefaultState(this.blockState.getBaseState().withProperty(TIER, 1));
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName(OMTNames.Blocks.turretBase);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.turretBase);
    }

    @Override
    public ItemBlock getItemBlock(Block block) {
        return new ItemBlockTurretBase(block);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state) {
        int MaxCharge;
        int MaxIO;
        switch (state.getValue(TIER) - 1) {
            case 0:
                MaxCharge = OMTConfigHandler.getBaseTierOneMaxCharge();
                MaxIO = OMTConfigHandler.getBaseTierOneMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 1, state);
            case 1:
                MaxCharge = OMTConfigHandler.getBaseTierTwoMaxCharge();
                MaxIO = OMTConfigHandler.getBaseTierTwoMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 2, state);
            case 2:
                MaxCharge = OMTConfigHandler.getBaseTierThreeMaxCharge();
                MaxIO = OMTConfigHandler.getBaseTierThreeMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 3, state);
            case 3:
                MaxCharge = OMTConfigHandler.getBaseTierFourMaxCharge();
                MaxIO = OMTConfigHandler.getBaseTierFourMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 4, state);
            case 4:
                MaxCharge = OMTConfigHandler.getBaseTierFiveMaxCharge();
                MaxIO = OMTConfigHandler.getBaseTierFiveMaxIo();
                return new TurretBase(MaxCharge, MaxIO, 5, state);
            default:
                return new TurretBase(1, 1, 1, state);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return true;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TIER, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{TIER}, new IUnlistedProperty[]{RENDERBLOCKSTATE});
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            ItemStack heldItem = player.getHeldItemMainhand();
            TurretBase base = (TurretBase) world.getTileEntity(pos);
            if (player.isSneaking() && OMTConfigHandler.isAllowBaseCamo() && heldItem == ItemStack.EMPTY) {
                if (base != null) {
                    if (player.getUniqueID().toString().equals(base.getOwner())) {
                        base.setCamoState(state);
                        world.notifyBlockUpdate(pos, state, state, 3);
                    } else {
                        addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
                    }
                }
            }

            Block heldItemBlock = null;

            if (heldItem != ItemStack.EMPTY) {
                heldItemBlock = Block.getBlockFromItem(heldItem.getItem());
            }

            if (!player.isSneaking() && OMTConfigHandler.isAllowBaseCamo() && heldItem != ItemStack.EMPTY && heldItem.getItem() instanceof ItemBlock &&
                    heldItemBlock.isNormalCube(heldItemBlock.getStateFromMeta(heldItem.getMetadata())) && Block.getBlockFromItem(
                    heldItem.getItem()).isOpaqueCube(heldItemBlock.getStateFromMeta(heldItem.getMetadata())) && !(Block.getBlockFromItem(
                    heldItem.getItem()) instanceof BlockTurretBase)) {
                if (base != null) {
                    if (player.getUniqueID().toString().equals(base.getOwner())) {
                        base.setCamoState(heldItemBlock.getStateFromMeta(heldItem.getItemDamage()));
                        world.notifyBlockUpdate(pos, state, state, 3);
                    } else {
                        addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
                    }
                }

            } else if (player.isSneaking() && base != null && player.getHeldItemMainhand() != ItemStack.EMPTY &&
                    player.getHeldItemMainhand().getItem() instanceof UsableMetaItem && player.getHeldItemMainhand().getItemDamage() == 2) {
                ((UsableMetaItem) player.getHeldItemMainhand().getItem()).setDataStored(player.getHeldItemMainhand(), base.writeMemoryCardNBT());
            } else if (!player.isSneaking() && base != null && player.getHeldItemMainhand() != ItemStack.EMPTY &&
                    player.getHeldItemMainhand().getItem() instanceof UsableMetaItem && player.getHeldItemMainhand().getItemDamage() == 2 &&
                    ((UsableMetaItem) player.getHeldItemMainhand().getItem()).hasDataStored(player.getHeldItemMainhand())) {
                base.readMemoryCardNBT(((UsableMetaItem) player.getHeldItemMainhand().getItem()).getDataStored(player.getHeldItemMainhand()));
            } else if (!player.isSneaking() && base != null) {
                TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);
                if (trustedPlayer != null && trustedPlayer.canOpenGUI) {
                    world.notifyBlockUpdate(pos, state, state, 6);
                    player.openGui(OpenModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
                    return true;
                } else if (PlayerUtil.isPlayerOwner(player, base)) {
                    world.notifyBlockUpdate(pos, state, state, 6);
                    player.openGui(OpenModularTurrets.instance, base.getTier(), world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    addChatMessage(player, new TextComponentString(safeLocalize("status.ownership")));
                }
            }
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos neighbor) {
        if (!worldIn.isRemote) {
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (base != null && worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
            stack) {

        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TurretBase) {
            EntityPlayerMP player = (EntityPlayerMP) placer;
            TurretBase base = (TurretBase) worldIn.getTileEntity(pos);
            if (base == null) {
                return;
            }
            base.setCamoState(state);
            base.setOwner(player.getUniqueID().toString());
            if (worldIn.isBlockIndirectlyGettingPowered(pos) > 0) {
                base.setRedstone(true);
            } else if (worldIn.isBlockIndirectlyGettingPowered(pos) == 0) {
                base.setRedstone(false);
            }
            base.markDirty();
            switch (state.getValue(TIER)) {
                case 1:
                    this.setResistance(OMTConfigHandler.getBaseTierOneBlastResistance());
                    this.setHardness(OMTConfigHandler.getBaseTierOneHardness());
                    break;
                case 2:
                    this.setResistance(OMTConfigHandler.getBaseTierTwoBlastResistance());
                    this.setHardness(OMTConfigHandler.getBaseTierTwoHardness());
                    break;
                case 3:
                    this.setResistance(OMTConfigHandler.getBaseTierThreeBlastResistance());
                    this.setHardness(OMTConfigHandler.getBaseTierThreeHardness());
                    break;
                case 4:
                    this.setResistance(OMTConfigHandler.getBaseTierFourBlastResistance());
                    this.setHardness(OMTConfigHandler.getBaseTierFourHardness());
                    break;
                case 5:
                    this.setResistance(OMTConfigHandler.getBaseTierFiveBlastResistance());
                    this.setHardness(OMTConfigHandler.getBaseTierFiveHardness());
            }
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            for (TileEntity tileEntity : getTouchingTileEntities(worldIn, pos.offset(facing))) {
                if (tileEntity instanceof TurretBase) return false;
            }
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            dropItems(worldIn, pos);
            worldIn.removeTileEntity(pos);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        switch (blockState.getValue(TIER)) {
            case 1:
                return OMTConfigHandler.getBaseTierOneHardness();
            case 2:
                return OMTConfigHandler.getBaseTierTwoHardness();
            case 3:
                return OMTConfigHandler.getBaseTierThreeHardness();
            case 4:
                return OMTConfigHandler.getBaseTierFourHardness();
            case 5:
                return OMTConfigHandler.getBaseTierFiveHardness();
        }
        return 10.0F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        switch (world.getBlockState(pos).getValue(TIER)) {
            case 1:
                return OMTConfigHandler.getBaseTierOneBlastResistance();
            case 2:
                return OMTConfigHandler.getBaseTierTwoBlastResistance();
            case 3:
                return OMTConfigHandler.getBaseTierThreeBlastResistance();
            case 4:
                return OMTConfigHandler.getBaseTierFourBlastResistance();
            case 5:
                return OMTConfigHandler.getBaseTierFiveBlastResistance();
        }
        return 10.0F;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(0, new ItemStack(ModBlocks.turretBase, 1, this.getMetaFromState(state)));
        return drops;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TIER) - 1;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer
            player) {
        return new ItemStack(ModBlocks.turretBase, 1, state.getValue(TIER) - 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModBlocks.turretBase, 1, i));
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity te = world.getTileEntity(data.getPos());
        if (te != null && te instanceof TurretBase) {
            TurretBase base = (TurretBase) te;
            EnumMachineMode machineMode = base.getMode();
            boolean active = base.isActive();
            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.MODE) + ": \u00A7A" + getMachineModeLocalization(machineMode));
            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.ACTIVE) + ": " + getColoredBooleanLocalizationYesNo(active));
            String ownerName = base.getOwnerName();
            probeInfo.text("\u00A76" + safeLocalize(OMLibNames.Localizations.GUI.OWNER) + ": \u00A7F" + ownerName);
        }
    }
}
