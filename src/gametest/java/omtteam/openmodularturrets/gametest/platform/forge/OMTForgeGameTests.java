package omtteam.openmodularturrets.gametest.platform.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;
import omtteam.openmodularturrets.registry.RegistrationStep;

import java.util.List;

/**
 * Forge entrypoint of the test mod. The test classes are found through their {@code @GameTestHolder} annotation.
 */
@Mod(OMTGameTestMod.MOD_ID)
public final class OMTForgeGameTests {
    private final List<RegistrationStep> steps = OMTGameTestMod.registrationSteps();

    public OMTForgeGameTests() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((RegisterEvent event) -> steps.stream()
                .filter(step -> step.registry().equals(event.getRegistryKey()))
                .forEach(step -> step.register().run()));
        MinecraftForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> {
            if (!event.getLevel().isClientSide()) {
                ProjectileTurretGameTests.onEntityLoad(event.getEntity());
            }
        });
    }
}
