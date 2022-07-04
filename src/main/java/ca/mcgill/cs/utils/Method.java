package ca.mcgill.cs.utils;

import java.util.ArrayList;
import java.util.List;

public class Method {
    private final String testMethod;
    private final String focalMethod;
    private final String scenario;

    public Method(String testMethod, String focalMethod, String scenario){
        this.testMethod = testMethod;
        this.focalMethod = focalMethod;
        this.scenario = scenario;
    }

    public String getTestMethod(){
        return testMethod;
    }

    public String getFocalMethod() {
        return focalMethod;
    }

    public String getScenario() {
        return scenario;
    }

    public String newMethodName(){
        return "test" + focalMethod + scenario;
    }

    public List<String> getProperties(){
        List<String> properties = new ArrayList<>();
        properties.add(testMethod);
        properties.add(focalMethod);
        properties.add(scenario);
        return properties;
    }

    @Override
    public String toString() {
        return testMethod;
//        return "Method{" +
//                "testMethod='" + testMethod + '\'' +
//                ", focalMethod='" + focalMethod + '\'' +
//                ", scenario='" + scenario + '\'' +
//                '}';
    }
}
