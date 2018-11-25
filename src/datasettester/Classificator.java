/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class Classificator {
    
    /**
     * Klassifiziert eine Erkennung in FALSE_NEGATIVE, TRUE_NEGATIVE, TRUE_POSITIVE, FALSE_POSITIVE
     * @param groundTruth erkannte Bounding Box
     * @param box Ground Thruth Bounding Box
     * @param selectedClass zu evaluierende Klasse
     * @param thresh
     * @return 
     */
    public Classification classify(BoundingBox groundTruth, BoundingBox box, String selectedClass, double thresh) {
        if (box.getProb() <= 0.1) return Classification.DEFAULT;
        if(groundTruth == null) {
            if(box.getProb() >= thresh) {
                return Classification.FALSE_POSITIVE;
            }
            return Classification.DEFAULT;
        }
        if (!groundTruth.getClasz().equalsIgnoreCase(selectedClass)) {
            return Classification.DEFAULT;
        }
        if (groundTruth.getClasz().equalsIgnoreCase(box.getClasz())) {
            if (box.getProb() >= thresh) {
                return Classification.TRUE_POSITIVE;
            }
            return Classification.FALSE_NEGATIVE;
        }
        if (box.getProb() >= thresh) {
            return Classification.FALSE_POSITIVE;
        }
        return Classification.FALSE_NEGATIVE;
    }
    
    /**
     * Findet korrespondierende Ground Truth BoundingBox mittels der IoU.
     * @param box
     * @param gtImg
     * @return 
     */
    public Pair<BoundingBox, Double> findBoundingBox(BoundingBox box, Image gtImg) {
        double maxIoU = 0.0;
        BoundingBox maxBox = null;
        Iou algorithm = new Iou();
        for (BoundingBox detBox : gtImg.getBbox()) {
            if (!box.getClasz().equalsIgnoreCase(detBox.getClasz())) continue;
            double iou = algorithm.compute(detBox, box);
            if (iou > maxIoU) {
                maxIoU = iou;
                maxBox = detBox;
            }
        }
        if (maxBox == null) return null;
        return new Pair(maxBox, maxIoU);
    }
}
