package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import omtteam.openmodularturrets.api.OMTPlugin;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.openmodularturrets.network.ConfigSync;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.PlatformServices;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.startup.OMTStartup;

import java.util.List;

/**
 * Fabric entrypoint: installs the platform services, starts the mod, registers the Fabric API lookups and wires
 * Fabric's callbacks to the shared code.
 */
public final class OMTFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.install(new PlatformServices(new FabricLoaderInfo(), new FabricFuel(), new FabricNetwork(),
                new FabricMenus(), new FabricEnergy()));
        OMTStartup.init(plugins());
        OMTStartup.registrationSteps().forEach(step -> step.register().run());
        FabricBlockLookups.register();
        UseBlockCallback.EVENT.register(InteractiveBlock::handleUse);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ConfigSync.onPlayerJoin(handler.getPlayer()));
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
