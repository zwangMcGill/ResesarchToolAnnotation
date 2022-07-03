import com.github.javaparser.printer.lexicalpreservation.changes.ListAdditionChange;
import com.github.javaparser.utils.SourceRoot;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import static guru.nidi.graphviz.model.Factory.*;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.sql.SQLOutput;
import java.util.*;

public class FCA<T> {

    List<T> objects;
    List<T> features;

    private static final HashMap<List<Method>, List<String>> intersections = new HashMap<List<Method>, List<String>>();

    public FCA(List<T> objects) {
        this.objects = objects;
//        this.features = new HashSet<>(features);
//        fcaMap = (T[][]) new Object[objects.size()][features.size()];
        this.intersections.put((List<Method>)this.objects,Collections.emptyList());
    }

    public List<List<T>> powerSet(){
        return powerSet(this.objects);
    }

    private List<List<T>> powerSet(List<T> originalList){
        List<List<T>> lists = new ArrayList<>();
        if (originalList.isEmpty()){
            lists.add(new ArrayList<>());
            return lists;
        }

        List<T> aList = new ArrayList<>(originalList);
        T head = aList.get(0);
        List<T> rest = new ArrayList<>(aList.subList(1, aList.size()));
        for(List<T> list : powerSet(rest)){
            List<T> newList = new ArrayList<>();
            newList.add(head);
            newList.addAll(list);
            lists.add(newList);
            lists.add(list);
        }
        return lists;
    }

    public void intersection(List<Method> input) {
        List<String> intersectionFeatures = new ArrayList<>(input.get(0).getProperties());

        for (Method object : input) {
            List<String> features = object.getProperties();
            intersectionFeatures.retainAll(features);
        }
        if (!intersectionFeatures.isEmpty()) {
            intersections.put(input, intersectionFeatures);
        }
    }

    public HashMap<List<Method>,List<String>> getAllIntersection(List<List<Method>> input){
        input.removeIf(List::isEmpty);
        for(List<Method> objects : input){
            intersection(objects);
        }

        return intersections;
    }

    public static HashMap<List<Method>, List<String>> getIntersections() {
        return intersections;
    }

    public Graph<List<Method>, DefaultEdge> connectEdge(){
        Graph<List<Method>, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);
        List<List<Method>> vertices = sortBySize();
        List<List<Method>> addedV = new ArrayList<>();
        HashMap<List<Method>, List<Method>> addedE = new HashMap<>();

//        g.addVertex((List<Method>)this.objects);
//        addedV.add((List<Method>)this.objects);

//        System.out.println("vertices" + vertices);
        for(List<Method> vertex : vertices){
            g.addVertex(vertex);
            addedV.add(vertex);
            //all vertices that contains this vertax.
            List<List<Method>> all = allContained(vertex,addedV);
//            System.out.println("all" + all);
            List<List<Method>> copy = new ArrayList<>(all);

            for (List<Method> element : all){
                List<List<Method>> result = allContained(element,all);
//                System.out.println("result"+ result);
                if (result.size() != 0){
                     result.forEach(p -> copy.remove(p));
                }
            }
//            System.out.println("copy"+copy);
            copy.forEach(q -> {g.addEdge(q,vertex);addedE.put(q,vertex);});
        }
//        System.out.println("Vertices: "+vertices);
//        System.out.println(addedE);
        return g;
    }

    public void addEdge(List<Method> p, List<Method> q){
        return;
    }

    public List<List<Method>> sortBySize(){
        List<List<Method>> methods = new ArrayList<>(intersections.keySet());
        methods.sort(new Comparator<List<Method>>() {
            @Override
            public int compare(List<Method> o1, List<Method> o2) {
                return Integer.compare(o2.size(), o1.size());
            }
        });
        return methods;
    }

    public List<List<Method>> allContained(List<Method> p, List<List<Method>> all){
        List<List<Method>> result = new ArrayList<>();
        for(List<Method> element : all){
            if (!p.equals(element) && isContained(p,element)){
                result.add(element);
            }
        }
        return result;
    }
    // q contains p
    public Boolean isContained(List<Method> p, List<Method> q){
        if(p.equals(q))
            return false;

        for(Method element : p){
            if (!q.contains(element)){
                return false;
            }
        }
        return true;
    }

    public List<Method> getMethodsWithSpecifiedFeature(String... features){
        for(Map.Entry<List<Method>,List<String>> entry : intersections.entrySet()){
            List<String> value = entry.getValue();

            if(value.equals(List.of(features))){
                return entry.getKey();
            }
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) throws IOException {
        Method method1 = new Method("testGetNull","get","null");
        Method method2 = new Method("testGetNotNull","get","notNull");
        Method method3 = new Method("testSetNull","set","null");
        Method method4 = new Method("testSetNotNull","set","notNull");
        List<Method> objects = new ArrayList<>();
        objects.add(method1);
        objects.add(method2);
        objects.add(method3);
        objects.add(method4);
        FCA<Method> test = new FCA<>(objects);
//        System.out.println(test.powerSet());
//        System.out.println(test.getAllIntersection(test.powerSet()));
        test.powerSet();
        test.getAllIntersection(test.powerSet());

//        System.out.println(getIntersections());


        Graph<List<Method>, DefaultEdge> g = test.connectEdge();
        for(DefaultEdge e : g.edgeSet()){
//            System.out.println(List.of(g.getEdgeSource(e)));
//            System.out.println(getIntersections().get(g.getEdgeSource(e)));
//        }
            System.out.println('"'+g.getEdgeSource(e).toString()
                    + ", " + getIntersections().get(g.getEdgeSource(e)) +'"'
                    + " -- " + '"' + g.getEdgeTarget(e).toString()
                    + ", " + getIntersections().get(g.getEdgeTarget(e)) + '"');
        }

        System.out.println(test.getMethodsWithSpecifiedFeature("null"));
    }
}
