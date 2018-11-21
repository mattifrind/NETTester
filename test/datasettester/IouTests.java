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
public class IouTests {
    
    @Test
    public void testSimpleIous() {
        BoundingBox temp1 = new BoundingBox(100, 100, 50, 50);
        assertEquals(1.0, new Iou().compute(temp1, temp1), 0.0);
        
        BoundingBox temp2 = new BoundingBox(125, 100, 75, 50);
        assertEquals(1.0/3.0, new Iou().compute(temp1, temp2), 0.001);
        
        BoundingBox temp3 = new BoundingBox(125, 125, 75, 75);
        assertEquals(1.0/7.0, new Iou().compute(temp1, temp3), 0.001);        
    }
}
