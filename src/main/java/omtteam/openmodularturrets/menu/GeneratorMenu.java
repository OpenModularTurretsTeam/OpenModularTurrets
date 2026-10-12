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
import omtteam.openmodularturrets.blockentity.misc.GeneratorBlockEntity;
import omtteam.openmodularturrets.registry.ModMenus;
import org.jetbrains.annotations.Nullable;

/**
 * Generator menu: one fuel slot and the player inventory. Energy, capacity and burn progress are synced as container
 * data.
 */
public class GeneratorMenu extends AbstractContainerMenu {
    private static final double MAX_DISTANCE_SQR = 64.0;
    private static final int DATA_ENERGY = 0;
    private static final int DATA_CAPACITY = 1;
    private static final int DATA_BURN_TIME = 2;
    private static final int DATA_BURN_DURATION = 3;
    private static final int FUEL_SLOT = 36;

    private final BlockPos pos;
    @Nullable
    private final GeneratorBlockEntity generator;
    private final ContainerData data;

    /** Client side. Uses its own container; the server keeps it in sync. */
    public static GeneratorMenu client(int containerId, Inventory playerInventory, BlockPos pos) {
        return new GeneratorMenu(containerId, playerInventory, pos, null, new SimpleContainer(1),
                new SimpleContainerData(LongContainerData.size(4)));
    }

    /** Server side. */
    public static GeneratorMenu server(int containerId, Inventory playerInventory, GeneratorBlockEntity generator) {
        return new GeneratorMenu(containerId, playerInventory, generator.getBlockPos(), generator,
                generator.getFuelContainer(), new LongContainerData(generator::getEnergy, generator::getCapacity,
                        generator::getBurnTime, generator::getBurnDuration));
    }

    private GeneratorMenu(int containerId, Inventory playerInventory, BlockPos pos,
                          @Nullable GeneratorBlockEntity generator, Container fuel, ContainerData data) {
        super(ModMenus.GENERATOR, containerId);
        this.pos = pos;
        this.generator = generator;
        this.data = data;
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        addSlot(new Slot(fuel, 0, 80, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return GeneratorBlockEntity.isFuel(stack.getItem());
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
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < FUEL_SLOT) {
            if (!GeneratorBlockEntity.isFuel(stack.getItem()) || !moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, FUEL_SLOT, false)) {
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
