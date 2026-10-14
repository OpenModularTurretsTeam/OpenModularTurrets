package omtteam.openmodularturrets.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description of a config field. {@link ConfigWriter} writes it as {@code //} comment lines above the key
 * ({@code \n} starts a new line).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Comment {
    String value();
}
