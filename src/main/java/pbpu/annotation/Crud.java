package pbpu.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/***
 * This annotation will tell that this class is used to perform CRUD operation.
 * You should provide EMPTY CONSTRUCTOR(mandatory) on every class that is
 * annotated with this annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Crud {
    Class<?> type();
}