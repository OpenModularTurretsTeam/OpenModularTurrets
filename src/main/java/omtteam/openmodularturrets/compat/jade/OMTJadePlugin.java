package omtteam.openmodularturrets.compat.jade;

import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
//? if >=1.21 {
import snownee.jade.api.JadeIds;
//?} else {
/*import snownee.jade.api.Identifiers;
*///?}

/**
 * Jade integration (optional: Jade loads this through the {@code jade} entrypoint, so the classes are only touched
 * when Jade is installed). Replaces the 1.12.2 WAILA/HWYLA handlers.
 */
public class OMTJadePlugin implements IWailaPlugin {
    /** Id Jade tags the lines of its universal item storage view with. */
    private static final ResourceLocation ITEM_STORAGE =
            //? if >=1.21 {
            JadeIds.UNIVERSAL_ITEM_STORAGE;
            //?} else {
            /*Identifiers.UNIVERSAL_ITEM_STORAGE;
            *///?}

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(TurretBaseJadeProvider.INSTANCE, TurretBaseBlockEntity.class);
        registration.registerBlockDataProvider(TurretJadeProvider.INSTANCE, TurretBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(TurretBaseJadeProvider.INSTANCE, TurretBaseBlock.class);
        registration.registerBlockComponent(TurretJadeProvider.INSTANCE, TurretBlock.class);
        // Jade's item view would list the ammo slots of the base; it stalls on "Collecting items..." there
        registration.addTooltipCollectedCallback(
                //? if >=1.21 {
                (box, accessor) -> hideItemStorage(box.getTooltip(), accessor));
                //?} else {
                /*(tooltip, accessor) -> hideItemStorage(tooltip, accessor));
                *///?}
    }

    private static void hideItemStorage(ITooltip tooltip, Accessor<?> accessor) {
        if (accessor instanceof BlockAccessor block && (block.getBlockEntity() instanceof TurretBaseBlockEntity
                || block.getBlockEntity() instanceof TurretBlockEntity)) {
            tooltip.remove(ITEM_STORAGE);
        }
    }
}
