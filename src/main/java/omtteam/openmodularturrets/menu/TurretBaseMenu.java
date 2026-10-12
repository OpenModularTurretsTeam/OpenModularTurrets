package omtteam.openmodularturrets.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.core.base.EnergyStore;
import omtteam.openmodularturrets.inventory.TurretBaseInventory;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.UpgradeItem;
import omtteam.openmodularturrets.registry.ModMenus;
import omtteam.openmodularturrets.security.PlayerAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * Turret base inventory menu. Ported from oldmod {@code TurretBaseContainer}: 3x3 ammo slots, addon and upgrade slots
 * depending on the tier, and the player inventory. Energy is synced as container data; everything else the screen
 * shows comes from the block entity sync.
 */
public class TurretBaseMenu extends AbstractContainerMenu {
    /** oldmod {@code canInteractWith}: squared distance below 64. */
    private static final double MAX_DISTANCE_SQR = 64.0;
    private static final int DATA_ENERGY = 0;
    private static final int DATA_CAPACITY = 1;

    private final BlockPos pos;
    private final int tier;
    @Nullable
    private final TurretBaseBlockEntity base;
    private final ContainerData data;
    private final int baseSlotStart;
    private final int baseSlotEnd;

    /** Client side. Uses its own container; the server keeps it in sync. */
    public static TurretBaseMenu client(int containerId, Inventory playerInventory, BlockPos pos) {
        int tier = playerInventory.player.level().getBlockEntity(pos) instanceof TurretBaseBlockEntity base
                ? base.getTier() : 1;
        return new TurretBaseMenu(containerId, playerInventory, pos, tier, null,
                new SimpleContainer(TurretBaseInventory.SIZE), new SimpleContainerData(LongContainerData.size(2)));
    }

    /** Server side. */
    public static TurretBaseMenu server(int containerId, Inventory playerInventory, TurretBaseBlockEntity base) {
        return new TurretBaseMenu(containerId, playerInventory, base.getBlockPos(), base.getTier(), base,
                base.getInventory(), energyData(base.getLogic().getEnergy()));
    }

    private TurretBaseMenu(int containerId, Inventory playerInventory, BlockPos pos, int tier,
                           @Nullable TurretBaseBlockEntity base, Container container, ContainerData data) {
        super(ModMenus.TURRET_BASE, containerId);
        this.pos = pos;
        this.tier = tier;
        this.base = base;
        this.data = data;

        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        baseSlotStart = slots.size();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new BaseSlot(container, TurretBaseInventory.AMMO_START + x + y * 3, 8 + x * 18, 17 + y * 18));
            }
        }
        addIfUsable(container, TurretBaseInventory.ADDON_START, 72, 18);
        addIfUsable(container, TurretBaseInventory.ADDON_START + 1, 92, 18);
        addIfUsable(container, TurretBaseInventory.UPGRADE_START, 72, 52);
        addIfUsable(container, TurretBaseInventory.UPGRADE_START + 1, 92, 52);
        baseSlotEnd = slots.size();
        addDataSlots(data);
    }

    private void addIfUsable(Container container, int index, int x, int y) {
        if (TurretBaseInventory.isSlotUsable(index, tier)) {
            addSlot(new BaseSlot(container, index, x, y));
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getTier() {
        return tier;
    }

    public long getEnergy() {
        return LongContainerData.read(data, DATA_ENERGY);
    }

    public long getCapacity() {
        return LongContainerData.read(data, DATA_CAPACITY);
    }

    /**
     * oldmod {@code transferStackInSlot}: addons and upgrades go to their slots, everything else to the ammo slots;
     * base slots move to the player inventory.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < baseSlotStart) {
            boolean module = stack.getItem() instanceof AddonItem || stack.getItem() instanceof UpgradeItem;
            int start = module ? baseSlotStart + TurretBaseInventory.AMMO_COUNT : baseSlotStart;
            int end = module ? baseSlotEnd : baseSlotStart + TurretBaseInventory.AMMO_COUNT;
            if (!moveToBaseSlots(stack, start, end)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, baseSlotStart, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack.getCount() == original.getCount() ? ItemStack.EMPTY : original;
    }

    /**
     * Like {@code moveItemStackTo}, but respects the per-item slot limits (vanilla only uses the item's max stack
     * size there, so shift-click filled addon and upgrade slots up to 64): fills matching stacks first, then empty
     * slots, through {@link Slot#safeInsert}.
     */
    private boolean moveToBaseSlots(ItemStack stack, int start, int end) {
        int count = stack.getCount();
        for (boolean emptySlots : new boolean[]{false, true}) {
            for (int i = start; i < end && !stack.isEmpty(); i++) {
                Slot slot = slots.get(i);
                if (slot.hasItem() != emptySlots) {
                    slot.safeInsert(stack);
                }
            }
        }
        return stack.getCount() != count;
    }

    /**
     * Checked by the server every tick: the base still exists, the player is close enough and may open its GUI.
     */
    @Override
    public boolean stillValid(Player player) {
        if (base == null) return true; // client side
        return !base.isRemoved()
                && player.distanceToSqr(Vec3.atCenterOf(pos)) < MAX_DISTANCE_SQR
                && base.getTrustManager().canOpenGui(PlayerAdapter.info(player));
    }

    /** Base slot with the oldmod slot rules (ammo / addon / upgrade, stack limits). */
    private static class BaseSlot extends Slot {
        BaseSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return TurretBaseInventory.mayPlace(getContainerSlot(), stack);
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return Math.min(super.getMaxStackSize(stack), TurretBaseInventory.getSlotLimit(getContainerSlot(), stack));
        }
    }

    private static ContainerData energyData(EnergyStore store) {
        return new LongContainerData(store::getAmount, store::getCapacity);
    }
}
