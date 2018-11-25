/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author matti
 */
public class ComputePrecRecallTests {
    @Test
    public void testPositives() {
        DatasetTester tester = new DatasetTester();
        
        //gleiche Boxen
        Image img = createImg();
        ImageStatistic is = new ImageStatistic(1, 0, 0);
        assertEquals("gleiche Boxen", is, tester.computeImageStatistics(img, img, "ball", 0.5));
        
        Image img2 = createImg2("0.7", "ball");
        assertEquals("IoU 0.5 - cls=cls - 0.7 prob", is, tester.computeImageStatistics(img2, img, "ball", 0.5));
        
        img2 = createImg2("0.4", "ball");
        assertEquals("IoU 0.5 - cls=cls - 0.4 prob", new ImageStatistic(0, 0, 1), tester.computeImageStatistics(img2, img, "ball", 0.5));
        
        img2 = createImg2("0.8", "foot");
        assertEquals("IoU 0.5 - cls!=cls - 0.8", new ImageStatistic(0, 0, 1), tester.computeImageStatistics(img2, img, "ball", 0.5));

        img2 = createImg3("0.8", "ball");
        is = tester.computeImageStatistics(img2, img, "ball", 0.5);
        assertEquals("IoU 0.2 - cls=cls - 0.8 prob", new ImageStatistic(0, 1, 1), is);
        
        img2 = createImgWithDifferentBoxes("1","0.8");
        assertEquals("Rudelerkennung - cls=cls - 0.8 prob", new ImageStatistic(1, 0, 0), tester.computeImageStatistics(img2, img, "ball", 0.5));
        
        img2 = createImgWithVeryDifferentBoxes("1","0.8");
        assertEquals("2 dets, 1 richtig - cls=cls - 0.8 prob", new ImageStatistic(1, 1, 0), tester.computeImageStatistics(img2, img, "ball", 0.5));
    
        img2 = createImgWith2DifferentBoxes();
        assertEquals("2 true det, bessere iou schlechtere prob", new ImageStatistic(1, 0, 0), tester.computeImageStatistics(img2, img, "ball", 0.5));
        
        img2 = createImgWith3VeryDifferentBoxes("0.9");
        assertEquals("3 dets, 2 richtig - cls=cls - hohe prob", new ImageStatistic(1, 1, 0), tester.computeImageStatistics(img2, img, "ball", 0.5));
    }
    
    private Image createImg() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "1", "ball");
        return new Image("", box1);
    }
    
    private Image createImg2(String prob, String clasz) {
        BoundingBox box1 = new BoundingBox("25", "0", "100", "100", prob, clasz);
        return new Image("", box1);
    }
    
    private Image createImg3(String prob, String clasz) {
        BoundingBox box1 = new BoundingBox("90", "90", "100", "100", prob, clasz);
        return new Image("", box1);
    }
    
    private Image createImgWithDifferentBoxes(String prob1, String prob2) {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", prob1, "ball");
        BoundingBox box2 = new BoundingBox("50", "0", "100", "100", prob2, "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
    
    private Image createImgWithVeryDifferentBoxes(String prob1, String prob2) {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", prob1, "ball");
        BoundingBox box2 = new BoundingBox("120", "0", "100", "100", prob2, "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
    
    private Image createImgWith3VeryDifferentBoxes(String prob) {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", prob, "ball");
        BoundingBox box2 = new BoundingBox("5", "0", "100", "100", "0.8", "ball");
        BoundingBox box3 = new BoundingBox("120", "0", "100", "100", "0.9", "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        list.add(box3);
        return new Image("", list);
    }
    
    private Image createImgWith2DifferentBoxes() {
        BoundingBox box1 = new BoundingBox("0", "0", "100", "100", "0.3", "ball");
        BoundingBox box2 = new BoundingBox("5", "0", "100", "100", "0.8", "ball");
        List<BoundingBox> list = new ArrayList();
        list.add(box1);
        list.add(box2);
        return new Image("", list);
    }
}
