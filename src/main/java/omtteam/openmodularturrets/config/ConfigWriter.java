package omtteam.openmodularturrets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Writes a config object as JSON with {@code //} comments (JSONC). Every key gets its {@link Comment}, its
 * {@link Range} and its default value as comment lines. Gson reads the result back (lenient parsing skips the
 * comments).
 */
final class ConfigWriter {
    private static final Gson VALUE = new GsonBuilder().disableHtmlEscaping().create();
    private static final String INDENT = "  ";

    private ConfigWriter() {
    }

    /**
     * @param config   the values to write
     * @param defaults an object of the same class holding the defaults
     */
    static String write(Object config, Object defaults) {
        StringBuilder out = new StringBuilder();
        writeObject(out, config, defaults, 0);
        return out.append('\n').toString();
    }

    private static void writeObject(StringBuilder out, Object config, Object defaults, int depth) {
        String indent = INDENT.repeat(depth + 1);
        out.append("{\n");
        Field[] fields = ConfigRanges.fields(config.getClass());
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                Object value = field.get(config);
                Object def = field.get(defaults);
                boolean section = ConfigRanges.isSection(field.getType());
                if (i > 0 && (section || hasComment(field))) out.append('\n');
                comments(out, field, section ? null : def, indent);
                out.append(indent).append(VALUE.toJson(ConfigRanges.keyOf(field))).append(": ");
                if (section) {
                    writeObject(out, value, def, depth + 1);
                } else if (value instanceof List<?> list) {
                    writeList(out, list, indent);
                } else {
                    out.append(VALUE.toJson(value));
                }
                if (i < fields.length - 1) out.append(',');
                out.append('\n');
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        out.append(INDENT.repeat(depth)).append('}');
    }

    private static void writeList(StringBuilder out, List<?> list, String indent) {
        if (list.isEmpty()) {
            out.append("[]");
            return;
        }
        out.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            out.append(indent).append(INDENT).append(VALUE.toJson(list.get(i)));
            if (i < list.size() - 1) out.append(',');
            out.append('\n');
        }
        out.append(indent).append(']');
    }

    private static boolean hasComment(Field field) {
        return field.isAnnotationPresent(Comment.class) || field.isAnnotationPresent(Range.class);
    }

    /** @param def the default to document, or null for none */
    private static void comments(StringBuilder out, Field field, Object def, String indent) {
        Comment comment = field.getAnnotation(Comment.class);
        if (comment != null) {
            for (String line : comment.value().split("\n")) {
                out.append(indent).append("// ").append(line).append('\n');
            }
        }
        Range range = field.getAnnotation(Range.class);
        if (range != null) {
            boolean integer = field.getType() == int.class;
            boolean hasMin = !Double.isInfinite(range.min());
            boolean hasMax = !Double.isInfinite(range.max());
            String min = number(range.min(), integer);
            String max = number(range.max(), integer);
            if (hasMin && hasMax) {
                out.append(indent).append("// Range: ").append(min).append(" to ").append(max).append('\n');
            } else if (hasMin) {
                out.append(indent).append("// Minimum: ").append(min).append('\n');
            } else if (hasMax) {
                out.append(indent).append("// Maximum: ").append(max).append('\n');
            }
        }
        if (def != null) {
            out.append(indent).append("// Default: ").append(VALUE.toJson(def)).append('\n');
        }
    }

    private static String number(double value, boolean integer) {
        return integer ? Long.toString((long) value) : Double.toString(value);
    }
}
