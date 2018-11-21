/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author matti
 */
public class ComputePrecRecallTests {
    @Test
    public void testPositives() {
        DatasetTester tester = new DatasetTester();
        Image img = createImg();
        Pair<Double, Double> expResult = new Pair<>(1.0, 1.0);
        assertEquals(expResult, tester.computePrecRecall(img, img, "ball", 0.5));
        
        Image img2 = createImg2("0.7");
        assertEquals(expResult, tester.computePrecRecall(img2, img, "ball", 0.5));
        
        img2 = createImg2("0.4");
        assertNull(tester.computePrecRecall(img2, img, "ball", 0.5));
        
        img2 = createImg2("0.8");
        img = createImgWithDifferentBoxes();
        assertEquals(new Pair<>(1.0, 1.0), tester.computePrecRecall(img2, img, "ball", 0.5));
        
        img = createImgWithEqualBoxes();
        assertEquals(new Pair<>(0.5, 1.0), tester.computePrecRecall(img2, img, "ball", 0.5));
    }
    
    private Image createImg() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "ball");
        return new Image("", box1);
    }
    
    private Image createImg2(String prob) {
        BoundingBox box1 = new BoundingBox("50", "50", "100", "100", prob, "ball");
        return new Image("", box1);
    }
    
    private Image createImgWithDifferentBoxes() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "ball");
        BoundingBox box2 = new BoundingBox("150", "0", "100", "100", "1", "foot");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
    
    private Image createImgWithEqualBoxes() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "ball");
        BoundingBox box2 = new BoundingBox("150", "0", "100", "100", "1", "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
}
