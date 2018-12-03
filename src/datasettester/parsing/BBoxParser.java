package datasettester.parsing;

import datasettester.BoundingBox;
import datasettester.DatasetTester;
import java.awt.Rectangle;

/**
 * FROM ANNOTATION CONVERTER
 * Parses the given coordinates in the needed bounding boxes.
 * @author Matti J. Frind
 */
public class BBoxParser {
    
    private static final int MIN_LENGTH = 8;
    
    public static BoundingBox[] readFootBBox(String x1String, String y1String, String x2String, String y2String, String clasz, double[] metadata) {
        switch (DatasetTester.annotationVersion) {
            case 1:
                return readBBox1(x1String, y1String, x2String, y2String, clasz);
            case 2:
                return readBBox2(x1String, y1String, x2String, y2String, clasz);
            case 3:
                if (DatasetTester.version3Availability) {
                    return readBBox3(x1String, y1String, x2String, y2String, clasz, metadata);
                } else {
                    return readBBox2(x1String, y1String, x2String, y2String, clasz);
                }
        }
        return null;
    }
    
    /**
     * Parse BBox. Version 1 - both foots
     * @param x1String
     * @param y1String
     * @param x2String
     * @param y2String
     * @return Bounding Box
     */
    public static BoundingBox[] readBBox1(String x1String, String y1String, String x2String, String y2String, String clasz) {
        int x1 = Integer.parseInt(x1String),
                y1 = Integer.parseInt(y1String),
                x2 = Integer.parseInt(x2String),
                y2 = Integer.parseInt(y2String),
                dx = x2 - x1, //xEntfernung
                dy = y2 - y1, //yEntfernung
                d = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); //Entfernung per Satz des Phytagoras
        Rectangle rect = new Rectangle(x1 + (dx - d) / 2, y1 + (dy - d) / 2, d, d);
        /*if (!"Ball".equals(split[3]))
        printBBox(rect, split[0]);*/
        BoundingBox bbox = new BoundingBox(rect, clasz);
        return new BoundingBox[]{bbox};
    }
    
     /**
     * Parse BBox. Version 2 - seperated foots
     * @param x1String
     * @param y1String
     * @param x2String
     * @param y2String
     * @return Bounding Box
     */
    public static BoundingBox[] readBBox2(String x1String, String y1String, String x2String, String y2String, String clasz) {
        int x1 = Integer.parseInt(x1String),
                y1 = Integer.parseInt(y1String),
                x2 = Integer.parseInt(x2String),
                y2 = Integer.parseInt(y2String),
                dx = x2 - x1, //xEntfernung
                dy = y2 - y1, //yEntfernung
                d = (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); //Entfernung per Satz des Phytagoras
        Rectangle rect1 = new Rectangle(), rect2 = new Rectangle();
        if ((double) Math.abs(dy) < Math.abs((double) (0.3*((double) d)))) {
            int length = (int) ((6.0/8.0)*d);
            if (length < MIN_LENGTH) {
                rect1 = minBox(x1, y1);
                rect2 = minBox(x2, y2);
            } else {
                rect1 = new Rectangle((int) (x1 - 0.5*length), (int) (y1 - 0.5*length), length, length);
                rect2 = new Rectangle((int) (x2 - 0.5*length), (int) (y2 - 0.5*length), length, length);
            }
        } else if (Math.abs(0.3*d) < Math.abs(dy) && Math.abs(dy) < Math.abs(0.5*d)) {
            if (Math.abs(dx) < MIN_LENGTH || d < MIN_LENGTH) {
                rect1 = minBox(x1, y1);
                rect2 = minBox(x2, y2);
            } else {
                rect1 = new Rectangle((int) (x1 - Math.abs(0.5*dx)), (int)(y1 - Math.abs(0.5*d)), Math.abs(dx), d);
                rect2 = new Rectangle((int) (x2 - Math.abs(0.5*dx)), (int) (y2 - Math.abs(0.5*d)), Math.abs(dx), d);
            }
        } else if (Math.abs(dy) > Math.abs(0.5*d)) {
            int length = (int) Math.abs(d);
            if (length < MIN_LENGTH) {
                rect1 = minBox(x1, y1);
                rect2 = minBox(x2, y2);
            } else {
                rect1 = new Rectangle((int) (x1 - 0.5*length), (int) (y1 - 0.5*length), length, length);
                rect2 = new Rectangle((int) (x2 - 0.5*length), (int) (y2 - 0.5*length), length, length);
            }
        }
        //printBBox(rect1, split[0]);
        //printBBox(rect2, split[0]);
        BoundingBox bbox1 = new BoundingBox(rect1, clasz);
        BoundingBox bbox2 = new BoundingBox(rect2, clasz);
        return new BoundingBox[]{bbox1, bbox2};
    }
    
    private static Rectangle minBox(int x, int y) {
        return new Rectangle((int) (x-(((double) MIN_LENGTH)/2.0)), (int) (y-(((double) MIN_LENGTH)/2.0)), MIN_LENGTH, MIN_LENGTH);
    }
    
    
    /**
     * Parse BBox. Version 3 - complete Robot
     * @param metadata
     * @return Bounding Box
     */
    public static BoundingBox[] readBBox3(String x1String, String y1String, String x2String, String y2String, String clasz, double[] metadata) {
        int x1 = Integer.parseInt(x1String),
                y1 = Integer.parseInt(y1String),
                x2 = Integer.parseInt(x2String),
                y2 = Integer.parseInt(y2String);
        int xpos = (x1 + x2)/2;
        int ypos = (y1 + y2)/2;
        int size = (int) Triangulation.getRobotHeight(xpos, ypos, metadata);
        Rectangle rect = new Rectangle(xpos-size/4, ypos-size, size/2, (int) 1.7 * size);
        
        return new BoundingBox[]{new BoundingBox(rect, clasz)};
    }
    
}