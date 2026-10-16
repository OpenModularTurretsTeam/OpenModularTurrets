package omtteam.openmodularturrets.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.camo.CamoHost;
import omtteam.openmodularturrets.camo.CamoRules;
import omtteam.openmodularturrets.registry.ModMenus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Camouflage menu of turret bases and camouflage traps: one phantom slot for the disguise, the player inventory to
 * pick blocks from, and (for hosts with adjustable light, see {@code CamoScreen}) the light sliders.
 * <p>
 * The phantom slot never holds an item: clicking it with a block in the cursor sets that block as the disguise
 * (the block stays in the cursor), clicking it with an empty cursor resets the disguise, and shift-clicking a block
 * in the inventory sets it as well. The server checks the permission on every change.
 */
public class CamoMenu extends AbstractContainerMenu {
    private static final double MAX_DISTANCE_SQR = 64.0;
    /** Index of the phantom slot (after the 36 inventory slots). */
    public static final int PHANTOM_SLOT = 36;
    public static final int PHANTOM_X = 80;
    public static final int PHANTOM_Y = 22;
    private static final int INVENTORY_SLOTS = 36;

    private final BlockPos pos;
    private final boolean showLight;
    /** Server side only. */
    @Nullable
    private final CamoHost host;
    private final SimpleContainer display = new SimpleContainer(1);

    /** Client side: the host is found through the (synced) block entity at {@code pos}. */
    public static CamoMenu client(int containerId, Inventory inventory, BlockPos pos) {
        CamoHost clientHost = inventory.player.level().getBlockEntity(pos) instanceof CamoHost found ? found : null;
        return new CamoMenu(containerId, inventory, pos, null, clientHost != null && clientHost.supportsLight());
    }

    /** Server side. */
    public static CamoMenu server(int containerId, Inventory inventory, CamoHost host) {
        CamoMenu menu = new CamoMenu(containerId, inventory, host.getBlockPos(), host, host.supportsLight());
        menu.display.setItem(0, CamoRules.itemFor(host.getCamo()));
        return menu;
    }

    private CamoMenu(int containerId, Inventory inventory, BlockPos pos, @Nullable CamoHost host, boolean showLight) {
        super(ModMenus.CAMO, containerId);
        this.pos = pos;
        this.host = host;
        this.showLight = showLight;
        int inventoryTop = inventoryTop(showLight);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, inventoryTop + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(inventory, x, 8 + x * 18, inventoryTop + 58));
        }
        addSlot(new Slot(display, 0, PHANTOM_X, PHANTOM_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return false;
            }
        });
    }

    /** Top of the inventory slots; lower when the light sliders are shown. */
    public static int inventoryTop(boolean showLight) {
        return showLight ? 106 : 62;
    }

    public static int imageHeight(boolean showLight) {
        return inventoryTop(showLight) + 82;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean showsLight() {
        return showLight;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId == PHANTOM_SLOT) {
            if (player instanceof ServerPlayer serverPlayer && clickType == ClickType.PICKUP) {
                setDisguiseFrom(serverPlayer, getCarried());
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    /** Shift-click on an inventory slot sets the disguise (the stack stays where it is). */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index >= 0 && index < INVENTORY_SLOTS && player instanceof ServerPlayer serverPlayer) {
            setDisguiseFrom(serverPlayer, slots.get(index).getItem());
        }
        return ItemStack.EMPTY;
    }

    /** An empty stack resets the disguise, a block sets it; anything else is ignored. */
    private void setDisguiseFrom(ServerPlayer player, ItemStack stack) {
        if (host == null || host.isRemoved()) return;
        if (!host.canSetCamo(player)) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.not_owner"));
            return;
        }
        BlockState disguise = stack.isEmpty() ? null : CamoRules.disguiseFor(stack);
        if (!stack.isEmpty() && disguise == null) {
            player.sendSystemMessage(Component.translatable("message.openmodularturrets.camo_invalid"));
            return;
        }
        host.setCamo(disguise);
        display.setItem(0, CamoRules.itemFor(disguise));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (host == null) return true; // client side
        return !host.isRemoved() && player.distanceToSqr(Vec3.atCenterOf(pos)) < MAX_DISTANCE_SQR
                && (host.canSetCamo(player) || host.canChangeLight(player));
    }
}
