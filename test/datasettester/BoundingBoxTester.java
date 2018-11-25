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
public class BoundingBoxTester {
    
    @Test
    public void test() {
        BoundingBox box1 = new BoundingBox(0, 0, 100, 100);
        BoundingBox box2 = new BoundingBox(100, 0, 200, 100);
        assertEquals(100.0, box1.distance(box2), 0.001);
        
        box1 = new BoundingBox(0, 0, 200, 100);
        box2 = new BoundingBox(200, 0, 400, 100);
        assertEquals(200.0, box1.distance(box2), 0.001);
    }
}
