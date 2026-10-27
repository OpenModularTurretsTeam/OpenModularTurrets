package omtteam.openmodularturrets.compat.jade;

import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.blockentity.OwnedBlockEntity;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration, only loaded if Jade is installed: Fabric Jade finds it through the {@code jade} entrypoint in
 * fabric.mod.json, NeoForge and Forge Jade through {@link WailaPlugin} (Fabric has no annotation scanning, so it is not
 * registered twice). Replaces the 1.12.2 WAILA/HWYLA handlers.
 * <p>
 * The server half is here; the tooltips are in {@code client.compat.jade.OMTJadeClientPlugin}, because Jade's tooltip
 * classes are client classes (from Jade 21 on 1.21.11, {@code ITooltip} extends one), which main cannot see.
 * Jade creates every plugin it finds on a dedicated server too, so the client half is not one of them: this plugin
 * loads it by name in {@link #registerClient}, which Jade calls only on a client.
 */
@WailaPlugin
public class OMTJadePlugin implements IWailaPlugin {
    private static final String CLIENT_PLUGIN = "omtteam.openmodularturrets.client.compat.jade.OMTJadeClientPlugin";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(TurretBaseJadeProvider.INSTANCE, TurretBaseBlockEntity.class);
        registration.registerBlockDataProvider(TurretJadeProvider.INSTANCE, TurretBlockEntity.class);
        registration.registerBlockDataProvider(OwnedBlockJadeProvider.INSTANCE, OwnedBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        try {
            Class.forName(CLIENT_PLUGIN).asSubclass(IWailaPlugin.class).getDeclaredConstructor().newInstance()
                    .registerClient(registration);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not create the Jade client plugin " + CLIENT_PLUGIN, e);
        }
    }
}
