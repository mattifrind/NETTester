package datasettester;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Matti J. Frind
 */
public class ClassificatorTests {
    
    @Test
    public void test() {
        BoundingBox groundtruth = new BoundingBox(0, 0, 0, 0, 1, "ball");
        BoundingBox detection = new BoundingBox(0, 0, 0, 0, 0.8, "ball");
        Classificator cls = new Classificator();
        assertEquals(Classification.TRUE_POSITIVE, cls.classify(groundtruth, detection, "ball", 0.5));
        
        groundtruth = new BoundingBox(0, 0, 0, 0, 1, "ball");
        detection = new BoundingBox(0, 0, 0, 0, 0.8, "foot");
        assertEquals(Classification.FALSE_POSITIVE, cls.classify(groundtruth, detection, "ball", 0.5));
        
        groundtruth = new BoundingBox(0, 0, 0, 0, 1, "ball");
        detection = new BoundingBox(0, 0, 0, 0, 0.3, "ball");
        assertEquals(Classification.FALSE_NEGATIVE, cls.classify(groundtruth, detection, "ball", 0.5));
        
        groundtruth = new BoundingBox(0, 0, 0, 0, 1, "ball");
        detection = new BoundingBox(0, 0, 0, 0, 0.8, "ball");
        assertEquals(Classification.DEFAULT, cls.classify(groundtruth, detection, "foot", 0.5));
        
    }
    
    @Test
    public void testFindBox() {
        Classificator cls = new Classificator();
        
        BoundingBox groundtruth = new BoundingBox(0, 0, 100, 100, 1, "ball");
        BoundingBox groundtruth2 = new BoundingBox(100, 0, 200, 100, 1, "ball");
        Image img = new Image("", Arrays.asList(groundtruth, groundtruth2));
        
        BoundingBox detection = new BoundingBox(0, 0, 100, 100, 0.8, "ball");
        assertEquals(groundtruth, cls.findBoundingBox(detection, img));
        
        detection = new BoundingBox(50, 0, 100, 100, 0.8, "ball");
        assertEquals(groundtruth, cls.findBoundingBox(detection, img));
        
        detection = new BoundingBox(200, 0, 200, 100, 0.8, "ball");
        img = new Image("", Arrays.asList(groundtruth));
        assertEquals(groundtruth, cls.findBoundingBox(detection, img));
        
        img = new Image("", Arrays.asList(groundtruth, groundtruth2));
        detection = new BoundingBox(0, 100, 100, 200, 0.8, "ball");
        assertEquals(groundtruth, cls.findBoundingBox(detection, img));
        
        img = new Image("", Arrays.asList(groundtruth, groundtruth2));
        detection = new BoundingBox(100, 100, 200, 200, 0.8, "ball");
        assertEquals(groundtruth2, cls.findBoundingBox(detection, img));
    }
    
    @Test
    public void testIgnoreLowProbabilites() {
        Classificator cls = new Classificator();
        BoundingBox box = new BoundingBox(0, 0, 100, 100, 1, "ball");
        BoundingBox box2 = new BoundingBox(100, 0, 200, 100, 0.05, "ball");
        assertEquals(Classification.DEFAULT, cls.classify(box, box2, "ball", 0.5));
    }
}
