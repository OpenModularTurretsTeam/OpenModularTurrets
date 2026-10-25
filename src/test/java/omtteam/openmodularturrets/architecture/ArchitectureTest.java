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
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Layering rules (CLAUDE.md, "Architecture"), checked on the compiled main classes (tests excluded), ported from
 * the 1.20.1+ port. The client rule is frozen: the violations that existed when it was added are stored in
 * src/test/resources/archunit_store, new ones fail and fixed ones drop out. Never add to the store or to
 * {@link #KNOWN_CYCLES} without asking the user.
 */
class ArchitectureTest {
    private static final String BASE = "omtteam.openmodularturrets";

    /**
     * The package cycles that exist today, by package below {@value #BASE}: packages in one set depend on each other
     * in a loop. This list is a ratchet. A new cycle, or a package joining one, fails {@link #noNewPackageCycles}, and
     * so does a cycle that shrank or disappeared until it is updated here, so the list always shows the real state.
     */
    private static final List<Set<String>> KNOWN_CYCLES = Arrays.asList(
            // one large loop through almost every package (blocks, tile entities, items, containers, network, registry)
            // api.weapon holds the weapons that were in turret (phase 5 turret API); api.lists was removed;
            // turret is gone (TurretHeadUtil became tileentity.turret.TurretMount, 2026-10-25)
            set("(root)", "api", "api.tileentity", "api.weapon", "block.base", "block.misc", "block.turret", "compat",
                    "compat.computercraft", "compat.hwyla", "compat.opencomputers", "config", "container",
                    "entity.projectile", "fixes", "handler", "inventory", "item", "network", "registry", "tileentity.base",
                    "tileentity.misc", "tileentity.turret", "util"),
            // the core types reach each other as in 1.20.1+ (BaseWorld -> ItemSlot -> AmmoType, TurretType -> Face,
            // Neighbour -> TurretType, LogicSettings -> TurretType, TargetSelector -> LogicSettings,
            // TurretState -> TurretBaseLogic)
            set("core.base", "core.item", "core.settings", "core.target", "core.turret", "core.world"));

    private static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                // the workspace build puts test classes in build/<Mod>/classes/java/test, which the option above misses
                .withImportOption(location -> !location.contains("/classes/java/test/"))
                .importPackages(BASE);
    }

    /** Core stays plain Java: no Minecraft, Forge or other mod packages (also enforced by verifyCoreIsolation). */
    @Test
    void coreDependsOnlyOnCoreAndJava() {
        classes().that().resideInAPackage(BASE + ".core..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", "javax.annotation..", "omtteam.omlib.core..", BASE + ".core..")
                .because("the core game logic must stay testable without the game")
                .allowEmptyShould(true)
                .check(classes);
    }

    /** Client-only code (GUIs, renderers) must never be loaded on a dedicated server; it is reached through the proxy. */
    @Test
    void commonCodeReachesClientCodeOnlyThroughTheProxy() {
        freeze(noClasses().that().resideOutsideOfPackage(BASE + ".client..")
                .and().haveSimpleNameNotEndingWith("ClientProxy")
                .should().dependOnClassesThat().resideInAPackage(BASE + ".client..")
                .because("client classes do not exist on a dedicated server")
                .allowEmptyShould(true))
                .check(classes);
    }

    /** See {@link #KNOWN_CYCLES}. */
    @Test
    void noNewPackageCycles() {
        Set<Set<String>> actual = new HashSet<>(packageCycles());
        Set<Set<String>> known = new HashSet<>(KNOWN_CYCLES);
        if (actual.equals(known)) {
            return;
        }
        StringBuilder message = new StringBuilder("The package cycles changed.\n");
        for (Set<String> cycle : actual) {
            if (known.contains(cycle)) {
                continue;
            }
            Set<String> before = known.stream().filter(k -> k.stream().anyMatch(cycle::contains)).findFirst()
                    .orElse(Collections.emptySet());
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
                if (!to.getPackageName().startsWith(BASE)) {
                    continue;
                }
                String toPackage = packageOf(to);
                if (toPackage.equals(fromPackage)) {
                    continue;
                }
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
            if (!index.containsKey(start)) {
                strongConnect(start, graph, index, low, stack, onStack, cycles, counter);
            }
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
        for (String w : graph.getOrDefault(v, Collections.emptyMap()).keySet()) {
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
            if (component.size() > 1) {
                cycles.add(component);
            }
        }
    }

    /** The dependencies inside a cycle, fewest class dependencies first (the cheapest to cut). */
    private static String describeEdges(Set<String> cycle) {
        Map<String, Map<String, Set<String>>> graph = packageGraph();
        List<String> lines = new ArrayList<>();
        for (String from : cycle) {
            graph.getOrDefault(from, Collections.emptyMap()).forEach((to, deps) -> {
                if (cycle.contains(to)) {
                    lines.add(String.format("    %3d  %s -> %s: %s", deps.size(), from, to, deps));
                }
            });
        }
        Collections.sort(lines);
        return "  dependencies inside it, fewest first:\n" + String.join("\n", lines) + "\n";
    }

    private static String asJava(Set<Set<String>> cycles) {
        List<String> sets = cycles.stream().map(TreeSet::new).sorted((a, b) -> b.size() - a.size())
                .map(cycle -> "            set(" + cycle.stream().map(p -> '"' + p + '"').collect(Collectors.joining(", ")) + ")")
                .collect(Collectors.toList());
        return "Arrays.asList(\n" + String.join(",\n", sets) + ");";
    }

    private static Set<String> set(String... packages) {
        return new HashSet<>(Arrays.asList(packages));
    }
}
