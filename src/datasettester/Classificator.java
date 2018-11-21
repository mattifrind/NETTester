/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

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
        if(groundTruth == null) {
            if(box.getProb() >= thresh) {
                return Classification.FALSE_POSITIVE;
            }
            return Classification.DEFAULT;
        }
        if (!groundTruth.getClasz().equals(selectedClass)) {
            return Classification.DEFAULT;
        }
        if (groundTruth.getClasz().equals(box.getClasz())) {
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
}
