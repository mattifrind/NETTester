package nettester;

import nettester.Iou;
import nettester.BoundingBox;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Matti J. Frind
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
