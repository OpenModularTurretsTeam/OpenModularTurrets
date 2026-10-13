package omtteam.openmodularturrets.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.config.OMTConfig;

public final class ModCreativeTabs {
    public static final CreativeModeTab MAIN = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, OMT.id("main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.openmodularturrets"))
                    .icon(() -> new ItemStack(ModItems.AMMO_ROCKET)) // 1.12.2 ModularTurretsTab: ammo meta 4
                    // disabled turrets are hidden (1.12.2 did not register them at all)
                    .displayItems((parameters, output) -> ModItems.ALL.stream()
                            .filter(ModCreativeTabs::isEnabled)
                            .forEach(output::accept))
                    .build());

    private ModCreativeTabs() {
    }

    private static boolean isEnabled(Item item) {
        return !(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof TurretBlock turret)
                || OMTConfig.get().turrets.get(turret.getTurretType()).enabled;
    }

    /** Loads the class, registering all creative tabs. */
    public static void init() {
    }
}
