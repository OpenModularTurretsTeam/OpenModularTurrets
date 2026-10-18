package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.item.ItemSlot;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/** Test world with editable slots and entities. */
final class FakeWorld implements BaseWorld {
    final BlockPosition position = new BlockPosition(100, 64, 100);
    final List<ItemSlot> ammoSlots = new ArrayList<>();
    final List<ItemSlot> addonSlots = new ArrayList<>(List.of(FakeSlot.empty(), FakeSlot.empty()));
    final List<ItemSlot> upgradeSlots = new ArrayList<>(List.of(FakeSlot.empty(), FakeSlot.empty()));
    final List<TargetEntity> entities = new ArrayList<>();
    final List<TargetEntity> warned = new ArrayList<>();
    boolean solar;
    /** Where the last line of sight check started (x, y, z). */
    double[] lastSightFrom;
    /** Entities whose line of sight is blocked. */
    Predicate<TargetEntity> blockedView = entity -> false;

    @Override
    public BlockPosition getPosition() {
        return position;
    }

    @Override
    public List<ItemSlot> getAmmoSlots() {
        return ammoSlots;
    }

    @Override
    public List<ItemSlot> getAddonSlots() {
        return addonSlots;
    }

    @Override
    public List<ItemSlot> getUpgradeSlots() {
        return upgradeSlots;
    }

    @Override
    public boolean canGenerateSolarPower() {
        return solar;
    }

    @Override
    public List<TargetEntity> getLivingEntities(Box box) {
        return entities.stream().filter(e -> box.contains(e.getX(), e.getY(), e.getZ())).toList();
    }

    @Override
    public Optional<TargetEntity> getEntity(int id) {
        return entities.stream().filter(e -> e.getId() == id).findFirst();
    }

    @Override
    public boolean hasLineOfSight(double fromX, double fromY, double fromZ, TargetEntity target) {
        lastSightFrom = new double[]{fromX, fromY, fromZ};
        return !blockedView.test(target);
    }

    @Override
    public void warnPlayer(TargetEntity player) {
        warned.add(player);
    }
}
