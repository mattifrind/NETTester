/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

/**
 * Berechnet die intersection over union.
 *
 * @author matti
 */
public class Iou {

    /**
     * Berechnet den iou Wert für 2 boxen.
     * 
     * @param box1
     * @param box2
     * @return 
     */
    public double compute(BoundingBox box1, BoundingBox box2) {
        double xA = Math.max(box1.getX(), box2.getX());
        double yA = Math.max(box1.getY(), box2.getY());
        double xB = Math.min(box1.getX() + box1.getWidth(), box2.getX() + box2.getWidth());
        double yB = Math.min(box1.getY() + box1.getHeight(), box2.getY() + box2.getHeight());

        double overlap = Math.max(0, xB - xA) * Math.max(0, yB - yA);
        double area1 = box1.getWidth() * box1.getHeight();
        double area2 = box2.getWidth() * box2.getHeight();
        return overlap / (area1 + area2 - overlap);
    }
}
