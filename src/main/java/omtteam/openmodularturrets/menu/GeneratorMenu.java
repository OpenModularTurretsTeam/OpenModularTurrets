package omtteam.openmodularturrets.menu;

import omtteam.omlib.menu.LongContainerData;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.registry.ModMenus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generator menu: a 3×3 grid of fuel slots on the left, the upgrade slot in the middle and the player inventory. The
 * rest of the area between the fuel grid and the flame is kept free for later slots. Energy, capacity and burn
 * progress are synced as container data.
 */
public class GeneratorMenu extends AbstractContainerMenu {
    private static final double MAX_DISTANCE_SQR = 64.0;
    private static final int DATA_ENERGY = 0;
    private static final int DATA_CAPACITY = 1;
    private static final int DATA_BURN_TIME = 2;
    private static final int DATA_BURN_DURATION = 3;
    /** Menu index of the first fuel slot (after the 36 player inventory slots). */
    private static final int FUEL_START = 36;
    private static final int FUEL_END = FUEL_START + GeneratorBlockEntity.FUEL_SLOTS;
    /** Position of the top left fuel slot's item; the grid has 3 columns. */
    public static final int FUEL_X = 8;
    public static final int FUEL_Y = 17;
    public static final int FUEL_COLUMNS = 3;
    /** Menu index of the upgrade slot, after the fuel slots. */
    private static final int UPGRADE_SLOT = FUEL_END;
    /** Position of the upgrade slot's item: centred in the free area, level with the middle fuel row. */
    public static final int UPGRADE_X = 90;
    public static final int UPGRADE_Y = 35;

    private final BlockPos pos;
    @Nullable
    private final GeneratorBlockEntity generator;
    private final ContainerData data;

    /** Client side. Uses its own container; the server keeps it in sync. */
    public static GeneratorMenu client(int containerId, Inventory playerInventory, BlockPos pos) {
        return new GeneratorMenu(containerId, playerInventory, pos, null, new SimpleContainer(GeneratorBlockEntity.FUEL_SLOTS),
                new SimpleContainer(1), new SimpleContainerData(LongContainerData.size(4)));
    }

    /** Server side. */
    public static GeneratorMenu server(int containerId, Inventory playerInventory, GeneratorBlockEntity generator) {
        return new GeneratorMenu(containerId, playerInventory, generator.getBlockPos(), generator,
                generator.getFuelContainer(), generator.getUpgradeContainer(), new LongContainerData(generator::getEnergy, generator::getCapacity,
                        generator::getBurnTime, generator::getBurnDuration));
    }

    private GeneratorMenu(int containerId, Inventory playerInventory, BlockPos pos,
                          @Nullable GeneratorBlockEntity generator, Container fuel, Container upgrades,
                          ContainerData data) {
        super(ModMenus.GENERATOR, containerId);
        this.pos = pos;
        this.generator = generator;
        this.data = data;
        Level level = playerInventory.player.level();
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int slot = 0; slot < GeneratorBlockEntity.FUEL_SLOTS; slot++) {
            addSlot(new Slot(fuel, slot, FUEL_X + slot % FUEL_COLUMNS * 18, FUEL_Y + slot / FUEL_COLUMNS * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return GeneratorBlockEntity.isFuel(level, stack);
                }
            });
        }
        addSlot(new Slot(upgrades, 0, UPGRADE_X, UPGRADE_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GeneratorBlockEntity.isUpgrade(stack);
            }

            @Override
            public int getMaxStackSize() {
                return GeneratorUpgradeType.ENERGY_MULTIPLIER.maxPerSlot();
            }
        });
        addDataSlots(data);
    }

    public BlockPos getPos() {
        return pos;
    }

    public long getEnergy() {
        return LongContainerData.read(data, DATA_ENERGY);
    }

    public long getCapacity() {
        return LongContainerData.read(data, DATA_CAPACITY);
    }

    /** Remaining burn time of the current fuel as a fraction (0 = not burning). */
    public float getBurnProgress() {
        long duration = LongContainerData.read(data, DATA_BURN_DURATION);
        return duration <= 0 ? 0F : Math.min(1F, (float) LongContainerData.read(data, DATA_BURN_TIME) / duration);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < FUEL_START) {
            if (GeneratorBlockEntity.isUpgrade(stack)) {
                // safeInsert keeps the slot limit of 4 (moveItemStackTo would fill up to 64)
                slots.get(UPGRADE_SLOT).safeInsert(stack);
                if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
            } else if (!GeneratorBlockEntity.isFuel(player.level(), stack)
                    || !moveItemStackTo(stack, FUEL_START, FUEL_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, FUEL_START, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack.getCount() == original.getCount() ? ItemStack.EMPTY : original;
    }

    @Override
    public boolean stillValid(Player player) {
        if (generator == null) return true; // client side
        return !generator.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(pos)) < MAX_DISTANCE_SQR;
    }
}
