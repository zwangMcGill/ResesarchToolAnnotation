import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JavaParserUtils {

    public static List<MethodDeclaration> getSortedMethod(CompilationUnit cu){

        return cu.findAll(MethodDeclaration.class);
    }

    public static void replaceMethod(CompilationUnit cu){
        List<MethodDeclaration> methods = getSortedMethod(cu);
//        for (TypeDeclaration typeDec : cu.getTypes()) {
//            List<BodyDeclaration> members = typeDec.getMembers();
//            int count = 0;
//            if (members != null) {
//                for (BodyDeclaration member : members) {
//                    if (member.isMethodDeclaration()) {
//                        MethodDeclaration field = (MethodDeclaration) member;
//                        typeDec.replace(field, methods.get(methods.size()-count-1));
//                        count++;
//                        System.out.println("New Method name: " + field.getNameAsString());
//                    }
//                }
//            }
//        }

        for (int i = 0; i< methods.size(); i++) {
            for (TypeDeclaration typeDec : cu.getTypes()) {
                if (typeDec instanceof ClassOrInterfaceDeclaration) {
//                newOrder.stream().forEach(element -> typeDec.addMember(element));
                    typeDec.addMember(methods.get(i));
                }
            }

        }


    }


    public void addAnnotaion(CompilationUnit cu){

    }

    // Find test methods annotated with @Test annotation
    public static List<MethodDeclaration> findAllTestMethods(CompilationUnit cu){

        return new ArrayList<>(cu.findAll(MethodDeclaration.class));
    }

    public static MethodDeclaration getMethodBasedOnName(CompilationUnit cu, String name){
        List<MethodDeclaration> allMethods = findAllTestMethods(cu);
        for(MethodDeclaration methodDeclaration : allMethods){
            if (methodDeclaration.getNameAsString().equals(name)){
                return methodDeclaration;
            }
        }
    return null;
    }

    public static void main(String[] args) {
        FileInputStream file = null;
        try {
            file = new FileInputStream("D:\\Research\\TagAnnotationTest\\src\\main\\java\\MutableClassTest.java");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert file != null;
//        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(file, StandardCharsets.UTF_8));
        CompilationUnit cu = StaticJavaParser.parse(file);
//        List<MethodDeclaration> methods = getSortedMethod(cu);
        replaceMethod(cu);

//        List<MethodDeclaration> methods = getSortedMethod(cu);
//        methods.forEach(System.out::println);

        try {
            Files.write(new File("D:\\Research\\TagAnnotationTest\\src\\main\\java\\MutableClassTest.java").toPath(), Collections.singleton(cu.toString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
