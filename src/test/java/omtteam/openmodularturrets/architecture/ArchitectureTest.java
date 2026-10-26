package omtteam.openmodularturrets.architecture;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Architecture rules of the mod, checked on the compiled main and client classes (tests excluded). They see every
 * dependency in the bytecode, also fully qualified names and references that need no import. See
 * {@code docs/architecture.md} for the layers these rules protect.
 */
class ArchitectureTest {
    private static final String BASE = "omtteam.openmodularturrets";

    /**
     * The package cycles that exist today, by package below {@value #BASE}: packages in one set depend on each other
     * in a loop. This list is a ratchet. A new cycle, or a package joining one, fails {@link #noNewPackageCycles}, and
     * so does a cycle that shrank or disappeared until it is updated here, so the list always shows the real state.
     */
    private static final List<Set<String>> KNOWN_CYCLES = List.of(
            // registry, blocks, block entities, menus and network all reach each other; the turret API's weapons take
            // the block entities. Out of it since 2026-10-25: (root) (start-up in startup.OMTStartup), config (addon
            // turret defaults handed to OMTConfig.init, join sync in network.ConfigSync), security, item
            // (MemoryCardInteraction in block), camo (CamoMenus in menu)
            Set.of("api", "api.weapon", "block", "block.base", "block.defense", "block.misc", "block.turret",
                    "blockentity", "blockentity.base", "blockentity.defense", "blockentity.misc", "blockentity.turret",
                    "entity.projectile", "inventory", "menu", "network", "registry"),
            Set.of("core.base", "core.item", "core.settings", "core.target", "core.turret", "core.world"),
            Set.of("client.api", "client.model"));

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    /** Core stays plain Java: no Minecraft, Fabric or other mod packages (also enforced by verifyCoreIsolation). */
    @Test
    void coreDependsOnlyOnCoreAndJava() {
        classes().that().resideInAPackage(BASE + ".core..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(BASE + ".core..", "omtteam.omlib.core..", "java..")
                .because("the core game logic must stay testable without the game")
                .check(classes);
    }

    /** Client-only code (renderers, screens) must never be loaded on a dedicated server. */
    @Test
    void mainCodeNeverDependsOnClientCode() {
        noClasses().that().resideInAPackage(BASE + "..").and().resideOutsideOfPackage(BASE + ".client..")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".client..")
                .because("client classes do not exist on a dedicated server")
                .check(classes);
    }

    /** See {@link #KNOWN_CYCLES}. */
    @Test
    void noNewPackageCycles() {
        Set<Set<String>> actual = new HashSet<>(packageCycles());
        Set<Set<String>> known = new HashSet<>(KNOWN_CYCLES);
        if (actual.equals(known)) return;
        StringBuilder message = new StringBuilder("The package cycles changed.\n");
        for (Set<String> cycle : actual) {
            if (known.contains(cycle)) continue;
            Set<String> before = known.stream().filter(k -> k.stream().anyMatch(cycle::contains)).findFirst().orElse(Set.of());
            if (before.containsAll(cycle)) {
                message.append("Smaller (good, update KNOWN_CYCLES): ").append(cycle).append('\n');
            } else {
                Set<String> added = new TreeSet<>(cycle);
                added.removeAll(before);
                message.append("New or larger cycle ").append(cycle).append("\n  new in it: ").append(added).append('\n')
                        .append(describeEdges(cycle));
            }
        }
        for (Set<String> cycle : known) {
            if (actual.stream().noneMatch(a -> a.stream().anyMatch(cycle::contains))) {
                message.append("Gone (good, remove it from KNOWN_CYCLES): ").append(cycle).append('\n');
            }
        }
        message.append("\nKNOWN_CYCLES for the current state:\n").append(asJava(actual));
        fail(message.toString());
    }

    // ---- package graph ----

    /** Package below BASE ("(root)" for BASE itself) of a class. */
    private static String packageOf(JavaClass javaClass) {
        String name = javaClass.getPackageName();
        return name.equals(BASE) ? "(root)" : name.substring(BASE.length() + 1);
    }

    /** Package dependencies, with the class dependencies behind each one. */
    private static Map<String, Map<String, Set<String>>> packageGraph() {
        Map<String, Map<String, Set<String>>> graph = new TreeMap<>();
        for (JavaClass from : classes) {
            String fromPackage = packageOf(from);
            graph.computeIfAbsent(fromPackage, p -> new TreeMap<>());
            for (Dependency dependency : from.getDirectDependenciesFromSelf()) {
                JavaClass to = dependency.getTargetClass();
                if (!to.getPackageName().startsWith(BASE)) continue;
                String toPackage = packageOf(to);
                if (toPackage.equals(fromPackage)) continue;
                graph.get(fromPackage).computeIfAbsent(toPackage, p -> new TreeSet<>())
                        .add(from.getSimpleName() + " -> " + to.getSimpleName());
            }
        }
        return graph;
    }

    /** Strongly connected components with more than one package (Tarjan). */
    private static List<Set<String>> packageCycles() {
        Map<String, Map<String, Set<String>>> graph = packageGraph();
        Map<String, Integer> index = new HashMap<>();
        Map<String, Integer> low = new HashMap<>();
        Deque<String> stack = new ArrayDeque<>();
        Set<String> onStack = new HashSet<>();
        List<Set<String>> cycles = new ArrayList<>();
        int[] counter = {0};
        for (String start : graph.keySet()) {
            if (!index.containsKey(start)) strongConnect(start, graph, index, low, stack, onStack, cycles, counter);
        }
        return cycles;
    }

    private static void strongConnect(String v, Map<String, Map<String, Set<String>>> graph, Map<String, Integer> index,
                                      Map<String, Integer> low, Deque<String> stack, Set<String> onStack,
                                      List<Set<String>> cycles, int[] counter) {
        index.put(v, counter[0]);
        low.put(v, counter[0]);
        counter[0]++;
        stack.push(v);
        onStack.add(v);
        for (String w : graph.getOrDefault(v, Map.of()).keySet()) {
            if (!index.containsKey(w)) {
                strongConnect(w, graph, index, low, stack, onStack, cycles, counter);
                low.put(v, Math.min(low.get(v), low.get(w)));
            } else if (onStack.contains(w)) {
                low.put(v, Math.min(low.get(v), index.get(w)));
            }
        }
        if (low.get(v).equals(index.get(v))) {
            Set<String> component = new TreeSet<>();
            String w;
            do {
                w = stack.pop();
                onStack.remove(w);
                component.add(w);
            } while (!w.equals(v));
            if (component.size() > 1) cycles.add(component);
        }
    }

    /** The dependencies inside a cycle, fewest class dependencies first (the cheapest to cut). */
    private static String describeEdges(Set<String> cycle) {
        Map<String, Map<String, Set<String>>> graph = packageGraph();
        List<String> lines = new ArrayList<>();
        for (String from : cycle) {
            graph.getOrDefault(from, Map.of()).forEach((to, deps) -> {
                if (cycle.contains(to)) lines.add(String.format("    %3d  %s -> %s: %s", deps.size(), from, to, deps));
            });
        }
        lines.sort(null);
        return "  dependencies inside it, fewest first:\n" + String.join("\n", lines) + "\n";
    }

    private static String asJava(Set<Set<String>> cycles) {
        List<String> sets = cycles.stream().map(TreeSet::new).sorted((a, b) -> b.size() - a.size())
                .map(cycle -> "        Set.of(" + String.join(", ", cycle.stream().map(p -> '"' + p + '"').toList()) + ")")
                .toList();
        return "List.of(\n" + String.join(",\n", sets) + ");";
    }
}
