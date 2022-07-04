import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
//@SupportedAnnotationTypes("Tag")
//@SupportedSourceVersion(SourceVersion.RELEASE_12)
@AutoService(Processor.class)
public class SimpleAnnotationProcessor extends AbstractProcessor {
    private final List<Element> methods = new ArrayList<Element>();
    private final Map<String,Method> methodStorage = new HashMap<>();
    @Override
    //TypeElement represents a class or interface element.
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // RoundEnvironment get all the class elements with the annotation
//        System.out.println(1);

//        List<Element> methodWithTest = new ArrayList<>();
//        methodWithTest.addAll(roundEnv.getElementsAnnotatedWith(Test.class));
//
//        for (Element element : methodWithTest) {
//            Tag type = element.getAnnotation(Tag.class);
//            if(type != null){
//                System.out.println(type);
//            }else{
//                element.
//                System.out.println(element.getSimpleName() + " doesnt have Tag annotation ");
//            }
//        }
        methods.addAll(roundEnv.getElementsAnnotatedWith(Tag.class));

        for (Element element : methods) {
            String testMethod = element.getSimpleName().toString();
            String focalMethod = element.getAnnotation(Tag.class).method().toString();
            String scenario = element.getAnnotation(Tag.class).scenario().toString();
            Method method = new Method(testMethod,focalMethod,scenario);
            methodStorage.put(testMethod, method);
        }

//        System.out.println(getDistinctFocalMethods(methods).size());
//        methods.stream().forEach(element -> {
//            System.out.print("Test Method: " + element.getSimpleName() + '\t');
//            System.out.print("Focal Method: " + element.getAnnotation(Tag.class).method()+ '\t');
//            System.out.print("Scenario: " + element.getAnnotation(Tag.class).scenario());
//            System.out.println();
//
//            });

//        createNewName(methods).entrySet().stream().forEach(element -> System.out.println(element));

        FileInputStream file = null;
        try {
            file = new FileInputStream("D:\\Research\\TagAnnotationTest\\src\\main\\java\\MutableClassTest.java");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(file,StandardCharsets.UTF_8));
        CompilationUnit cu = StaticJavaParser.parse(file);

        cu.findAll(MethodDeclaration.class)
                .stream()
                .filter(e -> e.isAnnotationPresent(Test.class))
                .forEach(e-> {if (!e.isAnnotationPresent(Tag.class)){
                    e.addAnnotation(Tag.class);
                    AnnotationExpr annotationExpr = e.getAnnotationByClass(Tag.class).get();
                    ((NormalAnnotationExpr)annotationExpr).addPair("method","\"value\"");

                }});

        try {
            Files.write(new File("D:\\Research\\TagAnnotationTest\\src\\main\\java\\MutableClassTest.java").toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        System.out.println(test);

        Type a = Type.SCENARIO;
        List<Method> newOrder = newOrder(groupBy(a,methodStorage));
//        System.out.println();
        List<MethodDeclaration> oldOrder = getCurrentMethodOrder(cu);
        System.out.println("oldOrder:");
        System.out.println(oldOrder);
//        System.out.println("newOrder");
//        newOrder.forEach(element -> System.out.println(element.getFocalMethod()));
//        System.out.println(groupBy(a,methodStorage));
        List<MethodDeclaration> reoder = reorder(cu,newOrder,oldOrder);
//        reoder.forEach(System.out::println);
        removeAllMethod(cu);
        placeNewOrder(cu,reoder);
        rename(cu);
//
//        try {
//            Files.write(new File("D:\\Research\\TagAnnotationTest\\src\\main\\java\\MutableClassTest.java").toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return true;
    }


    public List<String> getDistinctFocalMethods(List<Element> methods) {
        List<String> testMethods = methods.stream()
                .map(element -> element.getAnnotation(Tag.class).method())
                .filter(element -> !element.equals(""))
                .distinct()
                .collect(Collectors.toList());
        return testMethods;
    }

    public List<String> getTestMethods(List<Element> methods) {
        List<String> testMethods = methods.stream()
                .map(element -> element.getSimpleName().toString())
                .collect(Collectors.toList());
        return testMethods;
    }

    public Map<String,String> createNewName(List<Element> methods){
        List<String> newMethodName = methods.stream()
                .map(element -> "test"
                        + element.getAnnotation(Tag.class).method()
                        + element.getAnnotation(Tag.class).scenario())
                .collect(Collectors.toList());

        List<String> oldMethodName = getTestMethods(methods);

        return IntStream
                .range(0, methods.size()).
                boxed().
                collect(Collectors.toMap(oldMethodName::get,newMethodName::get));
    }


    public  Map<String,List<Method>>  groupBy(Type type){
        return groupBy(type,methodStorage);
    }

    private Map<String,List<Method>>  groupBy(Type type,Map<String,Method> methodStorage) {
        List<Method> result = new ArrayList<>(methodStorage.values());
        Map<String, List<Method>> groupbyType = null;
        switch (type) {
            case METHOD:
                groupbyType = result.stream()
//                        .collect(Collectors.groupingBy(Method::getFocalMethod, Collectors.groupingBy(Method::getScenario)));
                        .collect(Collectors.groupingBy(Method::getScenario));
            case SCENARIO:
                groupbyType = result.stream()
//                        .collect(Collectors.groupingBy(Method::getScenario, Collectors.groupingBy(Method::getFocalMethod)));
                        .collect(Collectors.groupingBy(Method::getFocalMethod));
        }

        return groupbyType;
    }

    private List<Method> newOrder(Map<String,List<Method>> group){
        ArrayList<List<Method>> flat = new ArrayList<>(group.values());
        List<Method> newList = flat.stream().flatMap(Collection::stream).collect(Collectors.toList());
        return newList;
    }

    public void rename(CompilationUnit cu){
        for(TypeDeclaration typeDec : cu.getTypes()){
            List<BodyDeclaration> members = typeDec.getMembers();
            if (members != null) {
                for(BodyDeclaration member : members){
                    if(member.isMethodDeclaration()){
                        MethodDeclaration field = (MethodDeclaration) member;
                        renameMethod(field);
//                        System.out.println("New Method name: " + field.getNameAsString());
                    }
                }
            }
        }

    }

    private MethodDeclaration renameMethod(MethodDeclaration methodDeclaration){
        String newName = methodStorage.get(methodDeclaration.getNameAsString()).newMethodName();
        methodDeclaration.setName(newName);
        return methodDeclaration;
    }

    public List<MethodDeclaration> getCurrentMethodOrder(CompilationUnit cu){
        List<MethodDeclaration> fields = new ArrayList<>();
        for(TypeDeclaration typeDec : cu.getTypes()){
            List<BodyDeclaration> members = typeDec.getMembers();
            if (members != null) {
                for(BodyDeclaration member : members){
                    if(member.isMethodDeclaration()){
                        MethodDeclaration field = (MethodDeclaration) member;
                        fields.add(field);
//                        renameMethod(field);
//                        System.out.println("New Method name: " + field.getNameAsString());
                    }
                }
            }
        }
        return fields;
    }

    public List<MethodDeclaration> reorder(CompilationUnit cu, List<Method> newOder, List<MethodDeclaration>oldOrder){
        List<MethodDeclaration> newfields = new ArrayList<>();
        for(Method method : newOder){
            for (MethodDeclaration methodDeclaration : oldOrder){
                if (method.getTestMethod().equals(methodDeclaration.getNameAsString())){
                    newfields.add(methodDeclaration);
                }
            }
        }
        return newfields;
    }
    public void removeAllMethod(CompilationUnit cu){
        cu.walk(MethodDeclaration.class, e -> {
            e.remove();
        });
    }

    public void removeMethod(CompilationUnit cu, String methodName){
        cu.walk(MethodDeclaration.class, e -> {
            if(methodName.equals(e.getNameAsString())){
                e.remove();
            }
        });
    }

    public void placeNewOrder(CompilationUnit cu, List<MethodDeclaration> newOrder) {
        //在原有的source file上添加了新的方法，然后再删除旧的方法
        //现有的问题是只能添加一个method，第二个就报错了
        for (int i = 0; i< newOrder.size(); i++) {
            for (TypeDeclaration typeDec : cu.getTypes()) {
                if (typeDec instanceof ClassOrInterfaceDeclaration) {
//                newOrder.stream().forEach(element -> typeDec.addMember(element));
                    typeDec.addMember(newOrder.get(i));
                }
            }
        }
        //这个是replace原有位置的method with new method
//        for (TypeDeclaration typeDec : cu.getTypes()) {
//            List<BodyDeclaration> members = typeDec.getMembers();
//            int count = 0;
//            if (members != null) {
//                for (BodyDeclaration member : members) {
//                    if (member.isMethodDeclaration()) {
//                        MethodDeclaration field = (MethodDeclaration) member;
//                        typeDec.replace(field,newOrder.get(count));
//                        count++;
////                        System.out.println("New Method name: " + ((MethodDeclaration) member).getNameAsString());
//                    }
//                }
//            }
//        }
    }

}
