/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import datasettester.ui.PrecisionRecall;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
//TODO: Finden, warum der Recall von ball nich größer 0.5 wird.
public class DatasetTester {
    
    public static final int ANNOTATION_VERSION = 3; //1-joined foots, 2-seperated foot, 3-robot
    public static final String DET_FILE = "train3_detectionsNEW.txt";
    public static final String VIS_DIRECTORY = "vis/";
    public static final String IMAGE_DIRECTORY = "../RobotData/large_robot/large_robot_jpg/";
    public static final String PNG_IMAGE_DIRECTORY = "../RobotData/large_robot/large_robot/";
    public static final String VIS_CLASS = "foot"; //ball oder foot
    public static final int MAX_VIS = 200;
    public int vis = 0;

    public void computeMean(Map<String, Image> detection, Map<String, Image> test) {
        List<Pair<Double, Double>> sum = new LinkedList<>();
        for (File file: new File(VIS_DIRECTORY).listFiles()) if (!file.isDirectory()) file.delete();
        System.out.println("RESULTS");
        System.out.println("----------------");
        List<Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>> apscore = new LinkedList<>();
        double iou_thresh = 0;
        for (double i = 0; i < 1; i+=0.1) {
            sum.add(computeSingleMean(apscore, detection, test, i));
            System.out.println("----------------");
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PrecisionRecall.start(Arrays.asList(apscore.get(0), apscore.get(2), apscore.get(3)));
            }
        });
        thread.start();
        System.out.println("AVERAGE RESULTS");
        System.out.println("Ball mAP: " + formatOutput(getListSum(sum).getKey()/11));
        System.out.println("Foot mAP: " + formatOutput(getListSum(sum).getValue()/11));
        System.out.println("mAP: " + formatOutput((getListSum(sum).getKey() + getListSum(sum).getValue())/22));
    }
    
    public Pair<Double, Double> computeSingleMean(List<Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>> apscore, Map<String, Image> detection, Map<String, Image> test, double iou_thresh) {
        System.out.println("Compute Single Mean");
        apscore.add(computeAPScore(detection, test, iou_thresh));
        MeanComputer mc = new MeanComputer();
        double ballAP = mc.computeMean(apscore.get(apscore.size()-1).getKey());
        double footAP = mc.computeMean(apscore.get(apscore.size()-1).getValue());
        System.out.println("Ball " + Math.round(iou_thresh*100) + "-AP: " + formatOutput(ballAP));
        System.out.println("Foot " + Math.round(iou_thresh*100) + "-AP: " + formatOutput(footAP));
        System.out.println(Math.round(iou_thresh*100) + "-mAP: " + formatOutput(((ballAP + footAP) / 2)));
        return new Pair<>(ballAP, footAP);
    }
    
    private String formatOutput(double value) {
        value = value * 100;
        return Math.round(value*100.0)/100.0 + "%";
    }
    
    private Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>> computeAPScore(Map<String, Image> detection, Map<String, Image> test, double iou_thresh) {
        List<Pair<Double, Double>> ballResult = new ArrayList<>();
        List<Pair<Double, Double>> footResult = new ArrayList<>();
        for (double i = 0; i < 1; i+=0.01) {
            List<ImageStatistic> ballStatistic = new ArrayList<>();
            List<ImageStatistic> footStatistic = new ArrayList<>();
            final double thresh = i;
            detection.values().stream().filter(img -> !img.getName().startsWith("COCO")).forEach((img) -> {
                if(!img.getName().contains("AddionalBallSamples")) {
                    Image valImg = test.get(img.getName());
                    addPrecRecall(ballStatistic, img, valImg, "ball", thresh, iou_thresh);
                    addPrecRecall(footStatistic, img, valImg, "foot", thresh, iou_thresh);
                }
            });
            Pair<Double, Double> tempValue = computePrecRecall(ballStatistic);
            if (tempValue != null) {
                ballResult.add(tempValue);
            }
            tempValue = computePrecRecall(footStatistic);
            if (tempValue != null) {
                footResult.add(tempValue);
            }
        }
        return new Pair<>(ballResult, footResult);
    }
    
    private void addPrecRecall(List<ImageStatistic> list, Image img, Image valImg, String clasz, double thresh, double iou_thresh) {
        ImageStatistic item = computeImageStatistics(img, valImg, clasz, thresh, iou_thresh);
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
    protected ImageStatistic computeImageStatistics(Image detImg, Image gtImg, String selectedClass, double thresh, double iou_thresh) {
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
        ImageStatistic is = new ImageStatistic(true_positive, false_positive, false_negative);
        if(selectedClass.equalsIgnoreCase(VIS_CLASS) && vis < MAX_VIS) {
            vis++;
            Visualize vis = new Visualize();
            vis.visualizeBBoxes(gtImg.getName(), gtImg.getBbox(), detImg.getBbox(), is, selectedClass, thresh, iou_thresh);
        }
        return is;
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
        if ((true_positive + false_positive) == 0) {
            return null;
        }
        if ((true_positive + false_negative) == 0) {
            return null;
        }
        double precision = (double) true_positive / (double) (true_positive + false_positive);
        double recall = (double) true_positive / (double) (true_positive + false_negative);
        return new Pair<>(recall, precision);
    }
    
    private Pair<Double, Double> getListSum(List<Pair<Double, Double>> list) {
        double key = 0.0;
        double value = 0.0;
        for (Pair<Double, Double> pair : list) {
            key += pair.getKey();
            value += pair.getValue();
        }
        return new Pair<>(key, value);
    }
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        try {
            Parser parser = new Parser();
            Map<String, Image> testData = parser.readTest();
            Map<String, Image> detectionData = parser.readDetections();
            new DatasetTester().computeMean(detectionData, testData);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
