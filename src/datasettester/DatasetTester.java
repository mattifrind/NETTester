/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import datasettester.ui.PrecisionRecall;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class DatasetTester {
    
    private final double iou_thresh = 0.1;

    public void computeMean(Map<String, Image> detection, Map<String, Image> test) {
        computeAPScore(detection, test);
    }
    
    private void computeAPScore(Map<String, Image> detection, Map<String, Image> test) {
        List<Pair<Double, Double>> ballResult = new ArrayList<>();
        List<Pair<Double, Double>> footResult = new ArrayList<>();
        for (double i = 0.3; i < 1; i+=0.02) {
            List<ImageStatistic> ballStatistic = new ArrayList<>();
            List<ImageStatistic> footStatistic = new ArrayList<>();
            final double thresh = i;
            detection.values().stream().filter(img -> !img.getName().startsWith("COCO")).forEach((img) -> {
                Image valImg = test.get(img.getName());
                System.out.println("compute: " +  img.getName());
                    addPrecRecall(ballStatistic, img, valImg, "ball", thresh);
                    addPrecRecall(footStatistic, img, valImg, "foot", thresh);
            });
            ballResult.add(computePrecRecall(ballStatistic));
            footResult.add(computePrecRecall(footStatistic));
        }
        PrecisionRecall.main(ballResult, new String[0]);
    }
    
    private void addPrecRecall(List<ImageStatistic> list, Image img, Image valImg, String clasz, double thresh) {
        ImageStatistic item = computeImageStatistics(img, valImg, clasz, thresh);
        if (item != null) list.add(item);
    }
    
    /**
     * compute ImageStatistic of a detection and a GroundTruth Image;
     * @param detImg detected image
     * @param gtImg ground truth image
     * @param selectedClass
     * @param thresh
     * @return ImageStatistic with counts of tp, fp and fn.
     */
    protected ImageStatistic computeImageStatistics(Image detImg, Image gtImg, String selectedClass, double thresh) {
        int false_negative = 0, false_positive = 0, true_positive = 0;
        Classificator cls = new Classificator();
        //found - key BoundingBox, value Detection mit IoU
        IdentityHashMap<BoundingBox, Pair<BoundingBox, Double>> found = new IdentityHashMap<>();
        
        //für jede Groundtruth die beste (IoU) Detection.
        for (BoundingBox det : detImg.getBbox()) {
            if(!det.getClasz().equalsIgnoreCase(selectedClass)) continue;
            Pair<BoundingBox, Double> groundTruth = cls.findBoundingBox(det, gtImg);
            if (groundTruth == null) {
                if (det.getProb() >= thresh) false_positive++;
                continue;
            }
            Pair<BoundingBox, Double> pair = found.get(groundTruth.getKey());
            if (pair == null) {
                found.put(groundTruth.getKey(), new Pair(det, groundTruth.getValue()));
            } else {
                //ersetzen, wenn ehemalige beste Detection einen zu geringen prob hat oder IoU größer ist
                if (pair.getValue() < groundTruth.getValue() || found.get(groundTruth.getKey()).getKey().getProb() < thresh) {
                    found.put(groundTruth.getKey(), new Pair(det, groundTruth.getValue()));
                }
            }
        }
        
        for (Pair<BoundingBox, Double> pair : found.values()) {
            if (pair.getKey().getProb() > thresh && pair.getValue() > iou_thresh) {
                true_positive++;
            } else if (pair.getKey().getProb() > thresh) {
                false_positive++;
            }
        }
        false_negative = (int) gtImg.countBoxes(selectedClass) - true_positive;
        return new ImageStatistic(true_positive, false_positive, false_negative);
    }
    
    private Pair<Double, Double> computePrecRecall(List<ImageStatistic> list) {
        int true_positive = 0;
        int false_positive = 0;
        int false_negative = 0;
        for (ImageStatistic imageStatistic : list) {
            true_positive += imageStatistic.getTrue_positive();
            false_positive += imageStatistic.getFalse_positive();
            false_negative += imageStatistic.getFalse_negative();
        }
        double precision = (double) true_positive / (double) (true_positive + false_positive);
        double recall = (double) true_positive / (double) (true_positive + false_negative);
        return new Pair<>(recall, precision);
    }
    
    /**
     * Returns the count of relevant objects in an image.
     * @param img
     * @return Pair<ball-count, foot-count>
     */
    protected Pair<Integer, Integer> getObjectsInImage(Image img) {
        int ball = 0;
        int foot = 0;
        for (BoundingBox box : img.getBbox()) {
            switch (box.getClasz()) {
                case "ball":
                    ball++;
                    break;
                case "foot":
                    foot++;
                    break;
                default:
                    break;
            }
        }
        return new Pair<>(ball,foot);
    }
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        try {
            Parser parser = new Parser();
            Map<String, Image> testData = parser.readTrain();
            Map<String, Image> detectionData = parser.readDetections();
            new DatasetTester().computeMean(detectionData, testData);
            
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
