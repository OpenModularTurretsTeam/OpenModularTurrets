package omtteam.openmodularturrets.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.base.BlockEntityBaseWorld;
import omtteam.openmodularturrets.blockentity.base.TurretBaseNbt;
import omtteam.openmodularturrets.camo.CamoData;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.camo.CamoLightProperties;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.camo.CamoLight;
import omtteam.openmodularturrets.core.camo.CamoPermissions;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Neighbour;
import omtteam.openmodularturrets.inventory.ItemAccess;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.security.LevelSecurityEnvironment;
import omtteam.openmodularturrets.security.PlayerAdapter;
import omtteam.openmodularturrets.security.TrustHost;
import omtteam.openmodularturrets.security.SecurityNbt;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Turret base block entity. All game rules live in the Minecraft-independent {@link TurretBaseLogic}; this class
 * connects it to the world: it forwards redstone, inventory and neighbour changes, ticks it, exposes the inventory
 * and energy to automation, and saves/syncs its state.
 */
public class TurretBaseBlockEntity extends CompatBlockEntity implements CamoHost, TrustHost, Clearable {
    private final TurretBaseInventory inventory = new TurretBaseInventory(this::onInventoryChanged,
            player -> Container.stillValidBlockEntity(this, player));
    /** The ammo slots: anything the inventory accepts goes in, everything may come out. */
    /** Insert only: other blocks never take ammo out, so a stranger's hopper cannot disarm the base. */
    private final ItemAccess ammoAccess = new ItemAccess(inventory, TurretBaseInventory.AMMO_START,
            TurretBaseInventory.AMMO_COUNT, stack -> true, stack -> false);
    private final TurretBaseLogic logic;
    private boolean neighboursScanned;
    private final CamoData camoData = new CamoData();

    public TurretBaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TURRET_BASE, pos, state);
        int tier = ((TurretBaseBlock) state.getBlock()).getTier();
        this.logic = new TurretBaseLogic(tier, ConfigLogicSettings.INSTANCE, new LevelSecurityEnvironment(this::getLevel),
                new BlockEntityBaseWorld(this::getLevel, pos, inventory), new Random(), this::onLogicChanged);
    }

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, TurretBaseBlockEntity base) {
        if (!base.neighboursScanned) {
            base.refreshNeighbours();
        }
        base.logic.tick(level.getGameTime());
    }

    // ---- World events -> logic ----

    /** Called by the block when a neighbour changed: redstone and attached turrets/expanders. */
    public void onNeighbourChanged() {
        if (level == null || level.isClientSide()) return;
        logic.setRedstone(level.hasNeighborSignal(worldPosition));
        refreshNeighbours();
    }

    private void onInventoryChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            logic.onInventoryChanged();
        }
    }

    /**
     * Tells the logic what is attached to each face. Waits until all neighbour blocks are loaded, so a turret in an
     * unloaded chunk does not count as removed.
     */
    private void refreshNeighbours() {
        if (level == null) return;
        Map<Face, Neighbour> attached = new EnumMap<>(Face.class);
        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = worldPosition.relative(direction);
            if (!level.isLoaded(neighbourPos)) return;
            scanNeighbour(neighbourPos, direction).ifPresent(neighbour -> attached.put(toFace(direction), neighbour));
        }
        logic.updateNeighbours(attached);
        neighboursScanned = true;
    }

    /** A turret counts if it faces away from this base (1.12.2 {@code TurretHeadUtil.getBaseTurrets}). */
    private Optional<Neighbour> scanNeighbour(BlockPos pos, Direction direction) {
        if (level instanceof ServerLevel) {
            BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof TurretBlock turret && state.getValue(TurretBlock.FACING) == direction) {
            return Optional.of(new Neighbour.Turret(turret.getTurretType()));
        }
        }
        return Optional.empty();
    }

    private static Face toFace(Direction direction) {
        return Face.valueOf(direction.name());
    }

    private void onLogicChanged(boolean clientRelevant) {
        if (clientRelevant) {
            syncToClient();
        } else {
            setChanged();
        }
    }

    // ---- Accessors ----

    public TurretBaseLogic getLogic() {
        return logic;
    }

    public int getTier() {
        return logic.getTier();
    }

    /** Owner, trusted players and every permission check. */
    @Override
    public TrustManager getTrustManager() {
        return logic.getTrustManager();
    }

    /** Internal inventory without restrictions (see {@link TurretBaseInventory} for the slot layout). */
    public TurretBaseInventory getInventory() {
        return inventory;
    }

    /**
     * Empties the inventory. Whoever moves or replaces the block after saving its data ({@code /setblock}, Sable
     * contraptions) calls this first, so {@link #onBlockRemoved} does not drop a second copy of the items.
     */
    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    /** Drops the whole inventory (ammo, addons, upgrades) when the base is removed (OMLib {@code dropItems}). */
    @Override
    protected void onBlockRemoved(Level level) {
        Containers.dropContents(level, worldPosition, inventory);
    }

    /** Ammo slots as exposed to automation on all sides, insert only (the loader adapters wrap it). */
    @SuppressWarnings("unused")
    public ItemAccess getItemAccess(@Nullable Direction side) {
        return ammoAccess;
    }

    /** Energy as exposed to automation on all sides (insert only). */
    @SuppressWarnings("unused")
    public EnergyAccess getEnergyAccess(@Nullable Direction side) {
        return logic.getEnergy();
    }

    // ---- Camouflage ----

    @Nullable
    @Override
    public BlockState getCamo() {
        return camoData.get();
    }

    @Override
    public void setCamo(@Nullable BlockState camo) {
        if (camoData.set(camo)) {
            syncToClient();
        }
    }

    @Override
    public boolean isCamoEnabled() {
        return OMTConfig.get().bases.allowBaseCamo;
    }

    /** Only the tier 4 and 5 bases have adjustable light. */
    @Override
    public boolean supportsLight() {
        return getBlockState().getBlock() instanceof TurretBaseBlock base && base.supportsLight();
    }

    @Override
    public CamoLight getLight() {
        return CamoLightProperties.read(getBlockState());
    }

    @Override
    public void setLight(CamoLight light) {
        if (level != null && supportsLight()) {
            CamoLightProperties.apply(level, worldPosition, light);
        }
    }

    @Override
    public boolean canSeeThroughCamo(Player player) {
        return getTrustManager().canOpenGui(PlayerAdapter.info(player));
    }

    @Override
    public boolean canSetCamo(Player player) {
        return CamoPermissions.canSetDisguise(getTrustManager(), PlayerAdapter.info(player));
    }

    @Override
    public boolean canChangeLight(Player player) {
        return CamoPermissions.canChangeLight(getTrustManager(), PlayerAdapter.info(player));
    }

    /** The camouflage the model shows. */
    @Override
    protected @Nullable Object renderData() {
        return isCamoEnabled() ? camoData.get() : null;
    }

    // ---- NBT ----

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        SecurityNbt.load(getTrustManager(), tag);
        logic.loadState(TurretBaseNbt.load(tag));
        CamoData.remeshIfChanged(this, camoData.read(tag));
        // client updates carry no items; the open menu syncs its slots itself
        if (NbtCompat.hasList(tag, "Items")) {
            inventory.getItems().clear();
            VersionCompat.loadItems(tag, inventory.getItems(), registries);
        }
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        writeClientData(tag, registries);
        VersionCompat.saveItems(tag, inventory.getItems(), registries);
    }

    /**
     * Clients get owner, trust list and settings (for the GUIs); the inventory and live energy are synced through
     * the open menu.
     */
    @Override
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
        SecurityNbt.save(getTrustManager(), tag);
        TurretBaseNbt.save(logic.getState(), tag);
        camoData.write(tag);
    }
}
