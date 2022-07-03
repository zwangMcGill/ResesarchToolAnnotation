import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestPoint2d {

    private Point2d point;

    @BeforeEach
    public void initEach(){
        point = new Point2d(1,1);
    }

    @Test
    public void testSetX(){
        point.setX(2);
        Assertions.assertEquals(2, point.getX());
    }

    @Test
    public void testSetY(){
        point.setY(2);
        Assertions.assertEquals(2, point.getY());
    }

    @Test
    public void testSetXY(){
        point.setXY(2,2);
        Assertions.assertEquals(2, point.getY());
        Assertions.assertEquals(2, point.getX());
    }

    @Test
    public void testDistanceFrom(){
        Point2d p1 = new Point2d(1,1);
        point.setXY(1,2);
        Double distance = point.distanceFrom(p1);
        Assertions.assertEquals(distance,1);

    }
}
