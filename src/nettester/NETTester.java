package nettester;

import nettester.parsing.Parser;
import nettester.ui.PrecisionRecall;
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
 * Computes Precall-Recall-Pairs and the mAP.
 * @author Matti J. Frind
 */
public class NETTester {
    
    public static int annotationVersion = 2; //1-joined foots, 2-seperated foot, 3-robot
    public static final String DET_FILE = "train2_detections.txt"; //train2 needs annotationVersion=2 -> train3=3
    public static final String TEST_FILE = "large_robot.csv"; //ground truth annotations
    public static final String VIS_DIRECTORY = "vis/"; //output directory for debug visualizations
    public static final String IMAGE_DIRECTORY = "../RobotData/large_robot/large_robot_jpg/"; //input directory for dataset images
    public static final String PNG_IMAGE_DIRECTORY = "../RobotData/large_robot/large_robot/"; //input directory for dataset images png format
    public static final String VIS_CLASS = "ball"; //class which will be visualized, if empty -> no visualization ("ball" or "foot")
    public static final int MAX_VIS = 200;
    
    
    public int vis = 0;
    public static boolean version3Availability = true;

    /**
     * Computes the Mean Average Precision and starts the GUI.
     * @param detection detection images mapped to the image name.
     * @param test test images mapped to the image name.
     * @see Result
     */
    public void computeMean(Map<String, Image> detection, Map<String, Image> test) {
        PrecRecResult sum = new PrecRecResult();
        for (File file: new File(VIS_DIRECTORY).listFiles()) if (!file.isDirectory()) file.delete();
        System.out.println("----------------");
        System.out.println("RESULTS");
        System.out.println("----------------");
        List<Result> apscore = new LinkedList<>();
        for (double i = 0; i < 1; i+=0.1) {
            sum.add(computeSingleMean(apscore, detection, test, i));
            System.out.println("----------------");
        }
        
        //starts the GUI (chart)
        Thread thread = new Thread(() -> {
            PrecisionRecall.start(Arrays.asList(apscore.get(0), apscore.get(2), apscore.get(3), apscore.get(4), apscore.get(5)));
        });
        thread.start();
    }
    
    /**
     * Computes the mAP for a specific IoU thresh and prints the results.
     * @param apscore List of all Results to which the new AP will be added
     * @param detection detection images mapped to the image name.
     * @param test test images mapped to the image name.
     * @param iou_thresh the IoU threshhold from which a BoundingBox will be evaluated as correct
     * @return Pair of the ballAP and the footAP
     * @see Result
     * @see PrecRecResult
     */
    public Pair<Double, Double> computeSingleMean(List<Result> apscore, Map<String, Image> detection, Map<String, Image> test, double iou_thresh) {
        System.out.println("Compute Single Mean");
        apscore.add(computeAPScore(detection, test, iou_thresh));
        MeanComputer mc = new MeanComputer();
        double ballAP = mc.computeMean(apscore.get(apscore.size()-1).getBallResult());
        double footAP = mc.computeMean(apscore.get(apscore.size()-1).getFootResult());
        System.out.println("Ball " + Math.round(iou_thresh*100) + "-AP: " + formatOutput(ballAP));
        System.out.println("Foot " + Math.round(iou_thresh*100) + "-AP: " + formatOutput(footAP));
        return new Pair<>(ballAP, footAP);
    }
    
    /**
     * Formats the value to a printable string
     * @param value from 0 to 1
     * @return rounded percentage with two decimals
     */
    private String formatOutput(double value) {
        value = value * 100;
        return Math.round(value*100.0)/100.0 + "%";
    }
    
    /**
     * Computes the Precision and Recall Pairs of the ball and the foot category 
     * for a specific iou_thresh using detection probality threshholds 
     * from 0 to 1 and an increment of 0.01.
     * @param detection detection images mapped to the image name.
     * @param test test images mapped to the image name.
     * @param iou_thresh the IoU threshold from which a BoundingBox will be evaluated as correct
     * @return Result containing all Precision and Recall Pairs seperated in the two classes.
     * @see Result
     * @see PrecRecResult
     */
    private Result computeAPScore(Map<String, Image> detection, Map<String, Image> test, double iou_thresh) {
        PrecRecResult ballResult = new PrecRecResult();
        PrecRecResult footResult = new PrecRecResult();
        for (double i = 0; i < 1; i+=0.01) {
            //iterates all possible thresholds
            List<ImageStatistic> ballStatistic = new ArrayList<>();
            List<ImageStatistic> footStatistic = new ArrayList<>();
            final double thresh = i;
            detection.values().stream().filter(img -> !img.getName().startsWith("COCO")).forEach((img) -> {
                //iterates every evaluated image from the robot dataset
                if(!img.getName().contains("AddionalBallSamples")) {
                    Image valImg = test.get(img.getName());
                    addPrecRecall(ballStatistic, img, valImg, "ball", thresh, iou_thresh);
                    addPrecRecall(footStatistic, img, valImg, "foot", thresh, iou_thresh);
                }
            });
            //computing Precall-Recall-Pairs if possible
            Pair<Double, Double> tempValue = computePrecRecall(ballStatistic);
            if (tempValue != null) {
                ballResult.add(tempValue);
            }
            tempValue = computePrecRecall(footStatistic);
            if (tempValue != null) {
                footResult.add(tempValue);
            }
        }
        return new Result(ballResult, footResult);
    }
    
    /**
     * Adds the computed ImageStatistics for a specific iou_thresh and 
     * class threshhold to a list.
     * @param list list the ImageStatistic will be added to
     * @param detImg Image containing all detections
     * @param gtImg Image containing all ground truth bounding boxes
     * @param clasz class which will be evaluated (ball or foot)
     * @param thresh detection probality thresholds
     * @param iou_thresh the IoU threshold from which a BoundingBox will be evaluated as correct
     * @see ImageStatistic
     */
    private void addPrecRecall(List<ImageStatistic> list, Image detImg, Image gtImg, String clasz, double thresh, double iou_thresh) {
        ImageStatistic item = computeImageStatistics(detImg, gtImg, clasz, thresh, iou_thresh);
        if (item != null) list.add(item);
    }
    
    /**
     * Computes a ImageStatistic of a detection and a ground truth image for a 
     * specific category, IoU threshhold and detection probality threshholds.
     * @param detImg detected image
     * @param gtImg ground truth image
     * @param selectedClass class which detections will be counted
     * @param thresh detection probality threshholds
     * @param iou_thresh the IoU threshhold from which a BoundingBox will be evaluated as correct
     * @return ImageStatistic with counts of tp, fp and fn.
     * @see ImageStatistic
     */
    protected ImageStatistic computeImageStatistics(Image detImg, Image gtImg, String selectedClass, double thresh, double iou_thresh) {
        int false_negative = 0, false_positive = 0, true_positive = 0;
        Classificator cls = new Classificator();
        //found: key BoundingBox, value Detection mit IoU
        IdentityHashMap<BoundingBox, Pair<BoundingBox, Double>> found = new IdentityHashMap<>();
        
        //compute the best IoU detection for every ground truth box
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
                //replace, if the old best detection has a too low probality or the IoU is better
                if (pair.getValue() < groundTruth.getValue() || found.get(groundTruth.getKey()).getKey().getProb() < thresh) {
                    found.put(groundTruth.getKey(), new Pair(det, groundTruth.getValue()));
                }
            }
        }
        
        //counting the positive detections
        for (Pair<BoundingBox, Double> pair : found.values()) {
            if (pair.getKey().getProb() > thresh && pair.getValue() > iou_thresh) {
                true_positive++;
            } else if (pair.getKey().getProb() > thresh) {
                false_positive++;
            }
        }
        false_negative = (int) gtImg.countBoxes(selectedClass) - true_positive;
        ImageStatistic is = new ImageStatistic(true_positive, false_positive, false_negative);
        
        //visualize for debug reasons, if needed.
        if(selectedClass.equalsIgnoreCase(VIS_CLASS) && vis < 2.5*MAX_VIS) {
            vis++;
            new Visualize().visualizeBBoxes(gtImg.getName(), gtImg.getBbox(), detImg.getBbox(), is, selectedClass, thresh, iou_thresh);
        }
        return is;
    }
    
    /**
     * Computes the precision and the recall value for a list of ImageStatistics
     * @param list List of ImageStatistic containing the counted detections
     * @return pair of the calculated recall and precision values
     * @return null, if no precision and recall values can be calculated
     */
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
    
    /**
     * Sums the key values up and the value values up separately.
     * @param list PrecRecResult
     * @return Pair of the sums of the two results.
     * @see PrecRecResult
     */
    private Pair<Double, Double> getListSum(PrecRecResult list) {
        double key = 0.0;
        double value = 0.0;
        for (Pair<Double, Double> pair : list.getPrecRecResult()) {
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
            System.out.println("Reading Test Data...");
            Map<String, Image> testData = parser.readTest();
            if (!version3Availability) {
                System.out.println("Annotation version 3 can't be used, because the files couldn't be found. Annotation Version 2 was used.");
            }
            System.out.println("Reading Detections Data...");
            Map<String, Image> detectionData = parser.readDetections();
            new NETTester().computeMean(detectionData, testData);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
