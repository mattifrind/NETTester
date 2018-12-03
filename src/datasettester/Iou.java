package datasettester;

/**
 * Computes the Intersection over Union (IoU).
 *
 * @author Matti J. Frind
 */
public class Iou {

    /**
     * Computes the IoU for 2 bounding boxes.
     * 
     * @param box1 bounding box
     * @param box2 bounding box
     * @return IoU value
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
