package datasettester;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Matti J. Frind
 */
public class ImageTest {
    
    @Test
    public void testCount() {
        Image img = createImgWith2Boxes();
        Assert.assertEquals(2, img.countBoxes("foot"));
        Assert.assertEquals(1, img.countBoxes("ball"));
        img = new Image("");
        Assert.assertEquals(0, img.countBoxes("foot"));
    }
    
    private Image createImgWith2Boxes() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "foot");
        BoundingBox box2 = new BoundingBox("150", "0", "100", "100", "1", "foot");
        BoundingBox box3 = new BoundingBox("150", "0", "100", "100", "1", "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        list.add(box3);
        return new Image("", list);
    }
}
