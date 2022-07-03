import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// define where is the annotation may appear
@Target(ElementType.METHOD)
// three types: SOURCE, CLASS, RUNTIME
@Retention(RetentionPolicy.CLASS)
public @interface Tag {
    String method() default "";
    String scenario() default "";
}
