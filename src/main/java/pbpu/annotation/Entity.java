package pbpu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Make sure any class annotated with @Entity has No-Arg constructor
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({
    ElementType.TYPE
})
public @interface Entity {
    String name() default "";
}
