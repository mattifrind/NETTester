/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.awt.Rectangle;

/**
 *
 * @author matti
 */
public class BBoxParser {
    
    private static final int MIN_LENGTH = 8;
    
     /**
     * Parse BBox. Version 2 - seperated foots
     * @param x1String
     * @param y1String
     * @param x2String
     * @param y2String
     * @return Bounding Box
     */
    public static BoundingBox[] readBBox2(String x1String, String y1String, String x2String, String y2String) {
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
        BoundingBox bbox1 = new BoundingBox(rect1);
        BoundingBox bbox2 = new BoundingBox(rect2);
        return new BoundingBox[]{bbox1, bbox2};
    }
    
    private static Rectangle minBox(int x, int y) {
        return new Rectangle((int) (x-(((double) MIN_LENGTH)/2.0)), (int) (y-(((double) MIN_LENGTH)/2.0)), MIN_LENGTH, MIN_LENGTH);
    }
}