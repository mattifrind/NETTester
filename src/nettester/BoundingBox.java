package nettester;

import java.awt.Rectangle;
import javafx.util.Pair;

/**
 * Class representing a bounding box in an image.
 * @author Matti J. Frind
 */
public class BoundingBox {
    private double x = 0.0;
    private double y = 0.0;
    private double width = 0.0;
    private double height = 0.0;
    private double prob = 0.0;
    private String clasz = "";

    /**
     *
     * @return
     */
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getProb() {
        return prob;
    }
    
    public String getRoundedProbString() {
        return Math.round(prob*100) + "%";
    }

    public void setProb(double prob) {
        this.prob = prob;
    }    

    public String getClasz() {
        return clasz;
    }

    public void setClasz(String clasz) {
        this.clasz = clasz;
    }
    
    public Pair<Double, Double> getCenter() {
        return new Pair<>(getX()+width/2.0, getY()+height/2.0);
    }
    
    /**
     * Computes the distance from this box to another by using the center point.
     * @param box another bounding box.
     * @return distance
     */
    public double distance(BoundingBox box) {
        Pair<Double, Double> center1 = getCenter();
        Pair<Double, Double> center2 = box.getCenter();
        double deltaX = Math.abs(center1.getKey() - center2.getKey());
        double deltaY = Math.abs(center1.getValue() - center2.getValue());
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
    
    /**
     * Creates a bounding box from two points with a probality of 1 and no class.
     * left-top and right-bottom corners
     * @param x1 point 1 x value
     * @param y1 point 1 y value
     * @param x2 point 2 x value
     * @param y2 point 2 y value
     */
    public BoundingBox(double x1, double y1, double x2, double y2) {
        this(x1, y1, x2, y2, 1, "");
    }
    
    /**
     * Creates a bounding box from two points with a given probality and a class.
     * left-top and right-bottom corners
     * @param x1 point 1 x value
     * @param y1 point 1 y value
     * @param x2 point 2 x value
     * @param y2 point 2 y value
     * @param probability from 0 to 1
     * @param clasz ball or foot
     */
    public BoundingBox(double x1, double y1, double x2, double y2, double probability, String clasz) {
        if (x1 < x2) {
            x = x1;
            width = x2 - x1;
        } else {
            x = x2;
            width = x1 - x2;
        }
        if (y1 < y2) {
            y = y1;
            height = y2 - y1;
        } else {
            y = y2;
            height = y1 - y2;
        }
        prob = probability;
        this.clasz= clasz;
    }
    
    /**
     * Constructor for usage while parsing foot annotations.
     * Creates a bounding box from a rectangle and a class.
     * @param rect
     * @param clasz ball or foot
     */
    public BoundingBox(Rectangle rect, String clasz) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
        prob = 1;
        this.clasz = clasz;
    }
    
    /**
     * Creates a new bounding box of a input line while parsing.
     * left-top and right-bottom corners
     * @param line for example " 72.00218  87.97475 476.6844  432.35834"
     * @param probability from 0 to 1
     * @param clasz ball or foot
     * @return new bounding box with the given properties
     */
    public static BoundingBox of(String line, String probability, String clasz) {
        String[] tempValues = line.trim().split("\\s+");
        if (tempValues.length != 4) {
            throw new IllegalArgumentException("Unexpected line: " + line);
        }
        return new BoundingBox(tempValues[0], tempValues[1], tempValues[2], tempValues[3], probability, clasz);
    }
    
    /**
     * Creates a bounding box from two points with a given probality and a class
     * in a string format.
     * left-top and right-bottom corners
     * @param x1 point 1 x value
     * @param y1 point 1 y value
     * @param x2 point 2 x value
     * @param y2 point 2 y value
     * @param pb probability from 0 to 1
     * @param clasz ball or foot
     */
    public BoundingBox(String x1, String y1, String x2, String y2, String pb, String clasz) {
        this(Double.valueOf(x1), Double.valueOf(y1), Double.valueOf(x2), Double.valueOf(y2), Double.valueOf(pb), clasz);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BoundingBox other = (BoundingBox) obj;
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.width) != Double.doubleToLongBits(other.width)) {
            return false;
        }
        if (Double.doubleToLongBits(this.height) != Double.doubleToLongBits(other.height)) {
            return false;
        }
        if (Double.doubleToLongBits(this.prob) != Double.doubleToLongBits(other.prob)) {
            return false;
        }
        return clasz.equalsIgnoreCase(other.clasz);
    }

    @Override
    public String toString() {
        return getX() + "," + getY() + "," + getWidth() + "," + getHeight();
    }
}
