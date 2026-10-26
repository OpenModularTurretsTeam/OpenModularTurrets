package omtteam.openmodularturrets.platform.fabric;

import omtteam.omlib.platform.fabric.FabricBlockLookups;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.startup.OMTStartup;

import java.util.List;

/**
 * Fabric entrypoint: installs the platform services, starts the mod, registers the Fabric API lookups and wires
 * Fabric's callbacks to the shared code.
 */
public final class OMTFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OMTStartup.init(plugins());
        OMTStartup.registrationSteps().forEach(step -> step.register().run());
        FabricBlockLookups.register(ModBlockEntities.transferTypes());
        ServerLivingEntityEvents.AFTER_DEATH.register(KillCounter::onDeath);
    }

    /** Other mods' turret plugins: the {@value OMTPlugin#ENTRYPOINT} entrypoints in their {@code fabric.mod.json}. */
    private static List<TurretRegistry.Plugin> plugins() {
        return FabricLoader.getInstance().getEntrypointContainers(OMTPlugin.ENTRYPOINT, OMTPlugin.class).stream()
                .map(container -> new TurretRegistry.Plugin(container.getProvider().getMetadata().getId(),
                        container.getEntrypoint()))
                .toList();
    }
}
