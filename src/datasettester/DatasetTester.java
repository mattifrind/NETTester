/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import datasettester.ui.PrecisionRecall;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class DatasetTester {

    private Map<String, Image> parseDetections(BufferedReader detections) throws IOException {
        HashMap<String, Image> images = new HashMap<>();
        String line;
        ParseState state = ParseState.OTHER;
        Image img = null;
        int pos;
        StringBuffer arrayContent = null;
        String probability = null;
        lineLoop: while ((line = detections.readLine()) != null) {
            String tempLine = line.trim();
            pos = 0;
            while (pos < tempLine.length()) {
                switch (state) {
                    case BOUNDINGBOX:
                        char currChar = tempLine.charAt(pos);
                        if (currChar == '[') {
                            arrayContent = new StringBuffer();
                        } else if(currChar == ']') {
                            state = ParseState.PROBABILITY;
                        } else if(currChar == '|') {
                            state = ParseState.OTHER;
                            if(img != null) {
                                images.put(img.getName(), img);
                                img = null;
                            }
                        } else if (arrayContent != null) {
                            arrayContent.append(currChar);
                        }
                        break;
                    case PROBABILITY:
                        probability = tempLine;
                        state = ParseState.CLASZ;
                        continue lineLoop;
                    case CLASZ:
                        String[] values = tempLine.split("\\s+");
                        img.addBBox(BoundingBox.of(arrayContent.toString(), probability, values[0]));
                        state = ParseState.BOUNDINGBOX;
                        continue lineLoop;
                    case OTHER:
                        if (tempLine.contains("/")) continue lineLoop;
                        if (img == null) {
                            img = new Image(tempLine);
                            state = ParseState.BOUNDINGBOX;
                            continue lineLoop;
                        }
                        break;
                }
                pos++;
            }
        }
        return images;
    }
    
    
    public Map<String, Image> readDetections() throws IOException {
        try(BufferedReader traindata = new BufferedReader(new FileReader("data//train3_detectionsNEW.txt"))) {
            return parseDetections(traindata);
        }
    }
    
    private Map<String, Image> parseTrainData(BufferedReader data) throws IOException {
        Map<String, Image> images = new HashMap<>();
        String line = "";
        while ((line = data.readLine()) != null) {
            Image img = Image.of(line);
            if (img == null) continue;
            Image found = images.get(img.getName());
            if (found == null) {
                images.put(img.getName(), img);
            } else {
                found.merge(img);
            }
        }
        return images;
    }
    
    public Map<String, Image> readTrain() throws FileNotFoundException, IOException {
        try(BufferedReader traindata = new BufferedReader(new FileReader("data//large_robot.csv"))) {
            return parseTrainData(traindata);
        }
    }
    
    public void computeMean(Map<String, Image> detection, Map<String, Image> test) {
        computeAPScore(detection, test);
    }
    
    private void computeAPScore(Map<String, Image> detection, Map<String, Image> test) {
        List<Pair<Double, Double>> ballResult = new ArrayList<>();
        List<Pair<Double, Double>> footResult = new ArrayList<>();
        detection.values().stream().filter(i -> !i.getName().startsWith("COCO")).forEach((img) -> {
            Image valImg = test.get(img.getName());
            System.out.println("compute: " +  img.getName());
            for (double i = 0.0; i < 1; i+=0.05) {
                addPrecRecall(ballResult, img, valImg, "ball", i);
                addPrecRecall(footResult, img, valImg, "foot", i);
            }
        });
        PrecisionRecall.main(ballResult, new String[0]);
    }
    
    private void addPrecRecall(List<Pair<Double, Double>> list, Image img, Image valImg, String clasz, double thresh) {
        Pair<Double, Double> item = computePrecRecall(img, valImg, clasz, thresh);
        if (item != null) list.add(item);
    }
    
    /**
     * compute precision and recall value.
     * @param img detected image
     * @param valImg ground truth image
     * @param selectedClass
     * @param thresh
     * @return Pair of recall and precision. if no object then null.
     */
    protected Pair<Double, Double> computePrecRecall(Image img, Image valImg, String selectedClass, double thresh) {
        int false_negative = 0, false_positive = 0, true_positive = 0;
        Classificator cls = new Classificator();
        for (BoundingBox box : img.getBbox()) {
            BoundingBox groundTruth = findBoundingBox(box, valImg);
            switch(cls.classify(groundTruth, box, selectedClass, thresh)) {
                case FALSE_NEGATIVE:
                    false_negative++;
                    break;
                case FALSE_POSITIVE:
                    false_positive++;
                    break;
                case TRUE_POSITIVE:
                    true_positive++;
                    break;
                case DEFAULT:
                    break;
            }
        }
        false_negative = (int) valImg.countBoxes(selectedClass) - true_positive;
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
     * Findet korrespondierende Ground Truth BoundingBox mittels der IoU.
     * @return 
     */
    private BoundingBox findBoundingBox(BoundingBox box, Image detImg) {
        double maxIoU = 0.0;
        BoundingBox maxBox = null;
        Iou algorithm = new Iou();
        for (BoundingBox detBox : detImg.getBbox()) {
            double iou = algorithm.compute(detBox, box);
            if (iou > maxIoU) {
                maxIoU = iou;
                maxBox = detBox;
            }
        }
        return maxBox;
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
            DatasetTester tester = new DatasetTester();
            Map<String, Image> testData = tester.readTrain();
            Map<String, Image> detectionData = tester.readDetections();
            tester.computeMean(detectionData, testData);
            
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
    
}
