package omtteam.openmodularturrets.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allowed values (inclusive) of an {@code int} or {@code double} config field. Values outside are clamped on load
 * by {@link ConfigRanges}, and {@link ConfigWriter} documents the range in the file. A missing bound is unlimited.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Range {
    double min() default Double.NEGATIVE_INFINITY;

    double max() default Double.POSITIVE_INFINITY;
}
