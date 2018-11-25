/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author matti
 */
public class ImageTest {
    
    @Test
    public void testCount() {
        Image img = createImgWith2Boxes();
        Assert.assertEquals(2, img.countBoxes("foot"));
    }
    
    private Image createImgWith2Boxes() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "foot");
        BoundingBox box2 = new BoundingBox("150", "0", "100", "100", "1", "foot");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
}
