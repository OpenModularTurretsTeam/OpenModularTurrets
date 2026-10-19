package omtteam.openmodularturrets.gametest.platform.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import omtteam.openmodularturrets.gametest.OMTGameTestMod;
import omtteam.openmodularturrets.gametest.ProjectileTurretGameTests;
import omtteam.openmodularturrets.registry.RegistrationStep;

import java.util.List;

/**
 * NeoForge entrypoint of the test mod. The test classes are found through their {@code @GameTestHolder} annotation.
 */
@Mod(OMTGameTestMod.MOD_ID)
public final class OMTNeoForgeGameTests {
    private final List<RegistrationStep> steps = OMTGameTestMod.registrationSteps();

    public OMTNeoForgeGameTests(IEventBus modBus) {
        modBus.addListener((RegisterEvent event) -> steps.stream()
                .filter(step -> step.registry().equals(event.getRegistryKey()))
                .forEach(step -> step.register().run()));
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> {
            if (!event.getLevel().isClientSide()) {
                ProjectileTurretGameTests.onEntityLoad(event.getEntity());
            }
        });
    }
}
