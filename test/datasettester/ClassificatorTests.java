/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author matti
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
}
