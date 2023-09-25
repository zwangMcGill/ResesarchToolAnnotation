package ca.mcgill.cs.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SuppressWarnings("all")
//@SupportedAnnotationTypes("Tag")
//@SupportedSourceVersion(SourceVersion.RELEASE_12)
@AutoService(Processor.class)
public class SimpleAnnotationProcessor extends AbstractProcessor {
    @Override
    //TypeElement represents a class or interface element.
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true;
    }
}
