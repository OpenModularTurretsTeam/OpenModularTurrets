package omtteam.openmodularturrets.config;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Clamps every {@link Range}-annotated field of a config object, including those of nested config objects. Keys
 * in log messages are the dotted JSON paths.
 */
final class ConfigRanges {
    private ConfigRanges() {
    }

    /** @param path prefix of the keys of {@code target}, empty or ending with a dot */
    static void validate(Object target, ConfigValidator v, String path) {
        for (Field field : fields(target.getClass())) {
            try {
                String name = path + keyOf(field);
                Range range = field.getAnnotation(Range.class);
                if (range != null && field.getType() == int.class) {
                    field.setInt(target, v.range(name, field.getInt(target), (int) range.min(), (int) range.max()));
                } else if (range != null && field.getType() == double.class) {
                    field.setDouble(target, v.range(name, field.getDouble(target), range.min(), range.max()));
                } else if (isSection(field.getType())) {
                    validate(field.get(target), v, name + ".");
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /** The persisted fields of a config class, in declaration order. */
    static Field[] fields(Class<?> type) {
        return java.util.Arrays.stream(type.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()))
                .peek(f -> f.setAccessible(true))
                .toArray(Field[]::new);
    }

    /** The JSON key of a field. */
    static String keyOf(Field field) {
        SerializedName serialized = field.getAnnotation(SerializedName.class);
        return serialized != null ? serialized.value() : field.getName();
    }

    /** Whether a field holds a nested group of settings (as opposed to a value). */
    static boolean isSection(Class<?> type) {
        return type.getPackage() == ConfigRanges.class.getPackage() && !type.isEnum() && !type.isInterface();
    }
}
