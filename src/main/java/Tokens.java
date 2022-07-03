import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// define where is the annotation may appear
@Target(ElementType.METHOD)
// three types: SOURCE, CLASS, RUNTIME
@Retention(RetentionPolicy.CLASS)
public @interface Tokens {
    String focalMethod() default "";
    String scenario() default "";
    String expectedResult() default "";
}
