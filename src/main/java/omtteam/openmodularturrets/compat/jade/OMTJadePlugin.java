package omtteam.openmodularturrets.compat.jade;

import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration, only loaded if Jade is installed: Fabric Jade finds it through the {@code jade} entrypoint in
 * fabric.mod.json, NeoForge and Forge Jade through {@link WailaPlugin} (Fabric has no annotation scanning, so it is not
 * registered twice). Replaces the 1.12.2 WAILA/HWYLA handlers.
 * <p>
 * Only the server half; the tooltips are registered by {@code client.compat.jade.OMTJadeClientPlugin}, because Jade's
 * tooltip classes are client classes (from Jade 21 on 1.21.11, {@code ITooltip} extends one), which main cannot see.
 */
@WailaPlugin
public class OMTJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(TurretBaseJadeProvider.INSTANCE, TurretBaseBlockEntity.class);
        registration.registerBlockDataProvider(TurretJadeProvider.INSTANCE, TurretBlockEntity.class);
        registration.registerBlockDataProvider(OwnedBlockJadeProvider.INSTANCE, OwnedBlockEntity.class);
    }
}
