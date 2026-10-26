package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import omtteam.openmodularturrets.platform.Energy;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public final class FabricEnergy implements Energy {
    @Override
    public boolean isBattery(ItemStack stack) {
        if (stack.isEmpty()) return false;
        EnergyStorage storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        return storage != null && storage.supportsExtraction();
    }

    @Override
    public long dischargeItem(Container container, int slot, EnergyStore target, long maxAmount) {
        // a slot context, so that the battery's changed data is written back into the container
        ContainerItemContext context = ContainerItemContext.ofSingleSlot(InventoryStorage.of(container, null).getSlot(slot));
        EnergyStorage battery = EnergyStorage.ITEM.find(context.getItemVariant().toStack(), context);
        return battery == null ? 0 : EnergyStorageUtil.move(battery, FabricEnergyStorages.of(target), maxAmount, null);
    }

    @Override
    public long pushToBlock(Level level, BlockPos pos, Direction side, EnergyStore source, long maxAmount) {
        if (!level.isLoaded(pos)) return 0;
        EnergyStorage target = EnergyStorage.SIDED.find(level, pos, side);
        return target == null ? 0 : EnergyStorageUtil.move(FabricEnergyStorages.of(source), target, maxAmount, null);
    }
}
