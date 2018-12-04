package nettester;

import javafx.util.Pair;

/**
 *
 * @author Matti J. Frind
 */
public class Classificator {
    
    /**
     * Classifies a detection in FALSE_NEGATIVE, TRUE_NEGATIVE, TRUE_POSITIVE, FALSE_POSITIVE.
     * @param groundTruth detected bounding box
     * @param box ground thruth bounding box
     * @param selectedClass class which will be evaluated
     * @param thresh probability threshhold
     * @return evaluation class of the detection
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
     * Finds the corresponding ground truth bounding box using the IoU
     * @param box detection bounding box
     * @param gtImg image containing the ground truth bounding boxes
     * @return Pair of the ground truth bounding box and his IoU, null if no
     * bounding box found
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
