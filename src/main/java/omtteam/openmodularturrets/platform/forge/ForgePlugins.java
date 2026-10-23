package omtteam.openmodularturrets.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Finds other mods' plugins on Forge, like {@code NeoForgePlugins}: the classes carrying a marker annotation
 * ({@code OMTPlugin.Register}, {@code OMTClientPlugin.Register}) in the mod scan data, which is complete before any mod
 * is constructed. Only the annotated classes are loaded, so client plugins are never loaded on a dedicated server.
 */
public final class ForgePlugins {
    private record Found(String modId, String className) {
    }

    private ForgePlugins() {
    }

    /**
     * Creates every class annotated with {@code annotation}, sorted by mod id and then class name.
     *
     * @param type   the interface the classes must implement
     * @param plugin turns the mod id (of the jar the class is in) and the instance into the caller's plugin record
     * @throws IllegalStateException if a class does not implement {@code type} or cannot be created
     */
    public static <T, P> List<P> find(Class<? extends Annotation> annotation, Class<T> type,
                                      BiFunction<String, T, P> plugin) {
        Type annotationType = Type.getType(annotation);
        List<Found> found = new ArrayList<>();
        for (ModFileScanData scan : ModList.get().getAllScanData()) {
            String modId = modId(scan);
            scan.getAnnotations().stream()
                    .filter(data -> data.targetType() == ElementType.TYPE && data.annotationType().equals(annotationType))
                    .forEach(data -> found.add(new Found(modId, data.clazz().getClassName())));
        }
        found.sort(Comparator.comparing(Found::modId).thenComparing(Found::className));
        List<P> plugins = new ArrayList<>();
        for (Found entry : found) {
            plugins.add(plugin.apply(entry.modId(), create(entry, type)));
        }
        return plugins;
    }

    /** The first mod of the jar (a jar with several mods is rare). */
    private static String modId(ModFileScanData scan) {
        return scan.getIModInfoData().stream()
                .map(IModFileInfo::getMods)
                .filter(mods -> !mods.isEmpty())
                .map(mods -> mods.get(0).getModId())
                .findFirst()
                .orElse("unknown");
    }

    private static <T> T create(Found entry, Class<T> type) {
        try {
            Class<?> clazz = Class.forName(entry.className());
            if (!type.isAssignableFrom(clazz)) {
                throw new IllegalStateException("Mod " + entry.modId() + ": " + entry.className() + " is marked as a plugin "
                        + "but does not implement " + type.getSimpleName());
            }
            return type.cast(clazz.getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Mod " + entry.modId() + ": could not create plugin " + entry.className(), e);
        }
    }
}
