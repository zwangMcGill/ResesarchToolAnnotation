import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.jgrapht.graph.InvalidGraphWalkException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;


public class IdentifyFocalMethod {
    private double totalMethodScore =0;
    private CompilationUnit cu;
    private String test;
    private String processedTest;
    private String focal;
    private String processedFocal;

    public IdentifyFocalMethod(){

    }

    public IdentifyFocalMethod(CompilationUnit cu,String test, String focal){
        this.cu = cu;
        this.test = test;
        this.processedTest = test.toLowerCase();
        this.focal = focal;
        this.processedFocal = focal.toLowerCase();

    }

    // naming convention
    private void NC(){
        totalMethodScore += processedTest.equals(processedFocal)? 1: 0;
    }

    // naming convention contain
    private void NCC(){
        totalMethodScore += processedTest.contains(processedFocal)? 1: 0;
    }

    // Longest common sequence
    private void LCSB(){
        double similarityScore = new LongestCommonSubsequence().apply(processedTest,
                processedFocal);
        double lcsBClassScore =
                similarityScore / Math.max(processedTest.length(),
                        processedFocal.length());
        totalMethodScore += lcsBClassScore;
    }

    // Levenshtein
    private void Levenshtein(){

        double distance =  new LevenshteinDistance().apply(processedTest, processedFocal);
        double levenMethodScore =1.0 - (distance / Math.max(processedTest.length(),
                processedFocal.length()));
        totalMethodScore += levenMethodScore;
    }

    // Last call before assertion
    private void LCBA(){
        MethodDeclaration methodDeclaration = JavaParserUtils.getMethodBasedOnName(cu,test);
        assert methodDeclaration != null;
        List<MethodCallExpr> methodInvocations = getMethodInvocations(methodDeclaration);

        if(methodInvocations.size() == 0){
            return;
        }

        for (int i = methodInvocations.size()-1; i > 0; i--){
            if (methodInvocations.get(i).getNameAsString().toLowerCase().contains("assert"))

                totalMethodScore += methodInvocations.get(i-1).getNameAsString().equals(focal) ? 1:0;
            }
    }

    public boolean isFocal(){
        NC();
        NCC();
        LCSB();
        Levenshtein();
        LCBA();
        System.out.println(totalMethodScore);
        return totalMethodScore >= 2.0;
    }

    public List<MethodCallExpr> getMethodInvocations(MethodDeclaration test){
        assert test != null;
        return test.findAll(MethodCallExpr.class);
    }

    public static void main(String[] args) {
        FileInputStream file = null;
        try {
            file = new FileInputStream("D:\\Research\\TagAnnotation\\src\\main\\java\\TestPoint2d.java");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert file != null;
//        CompilationUnit cu = LexicalPreservingPrinter.setup(StaticJavaParser.parse(file, StandardCharsets.UTF_8));
        CompilationUnit cu = StaticJavaParser.parse(file);

        IdentifyFocalMethod test = new IdentifyFocalMethod(cu,"testDistanceFrom","distanceFrom");
        System.out.println(test.isFocal());

    }
}
