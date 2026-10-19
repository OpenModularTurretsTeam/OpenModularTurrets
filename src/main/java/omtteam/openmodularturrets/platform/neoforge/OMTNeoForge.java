package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.block.InteractiveBlock;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.entity.projectile.KillCounter;
import omtteam.openmodularturrets.inventory.ItemAccess;
import omtteam.openmodularturrets.platform.Platform;
import omtteam.openmodularturrets.platform.PlatformServices;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.RegistrationStep;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * NeoForge entrypoint: installs the platform services, starts the mod, runs each registration step in the
 * {@link RegisterEvent} of its registry and wires NeoForge's events to the shared code.
 */
@Mod(OMT.MOD_ID)
public final class OMTNeoForge {
    private final List<RegistrationStep> steps = OMT.registrationSteps();
    private final Set<RegistrationStep> done = new HashSet<>();

    public OMTNeoForge(IEventBus modBus) {
        Platform.install(new PlatformServices(new NeoForgeLoaderInfo(), new NeoForgeFuel(), new NeoForgeNetwork(modBus),
                new NeoForgeMenus(), new NeoForgeEnergy()));
        // other mods' turret plugins are not found on NeoForge yet ("Turret API plugin discovery"): built-in turrets only
        OMT.init(List.of());
        modBus.addListener(this::onRegister);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(OMTNeoForge::onRegisterCapabilities);
        NeoForge.EVENT_BUS.addListener(OMTNeoForge::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(OMTNeoForge::onPlayerLoggedIn);
        // lowest priority: mods that cancel the death have run, and cancelled deaths do not arrive here
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, OMTNeoForge::onLivingDeath);
    }

    private void onRegister(RegisterEvent event) {
        for (RegistrationStep step : steps) {
            if (step.registry().equals(event.getRegistryKey())) {
                step.register().run();
                done.add(step);
            }
        }
    }

    /** A step whose registry fired no event would leave its content unregistered: fail loudly instead. */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        if (done.size() != steps.size()) {
            List<RegistrationStep> missing = steps.stream().filter(step -> !done.contains(step)).toList();
            throw new IllegalStateException("Registration steps never ran: " + missing);
        }
    }

    /** Items and energy of the turret base and the generator, on all sides (as the Fabric lookups). */
    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        registerItems(event, ModBlockEntities.TURRET_BASE, (base, side) -> base.getItemAccess(side));
        registerEnergy(event, ModBlockEntities.TURRET_BASE, (base, side) -> base.getEnergyAccess(side));
        registerItems(event, ModBlockEntities.GENERATOR, (generator, side) -> generator.getItemAccess(side));
        registerEnergy(event, ModBlockEntities.GENERATOR, (generator, side) -> generator.getEnergyAccess(side));
    }

    private static <T extends BlockEntity> void registerItems(RegisterCapabilitiesEvent event, BlockEntityType<T> type,
                                                              BiFunction<T, Direction, ItemAccess> access) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type,
                (blockEntity, side) -> new NeoForgeItemHandler(access.apply(blockEntity, side)));
    }

    private static <T extends BlockEntity> void registerEnergy(RegisterCapabilitiesEvent event, BlockEntityType<T> type,
                                                               BiFunction<T, Direction, EnergyAccess> access) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, type, (blockEntity, side) -> {
            EnergyAccess energy = access.apply(blockEntity, side);
            return energy == null ? null : new NeoForgeEnergyHandler(energy);
        });
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = InteractiveBlock.handleUse(event.getEntity(), event.getLevel(), event.getHand(),
                event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            OMTConfig.onPlayerJoin(player);
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            KillCounter.onDeath(event.getEntity(), event.getSource());
        }
    }
}
