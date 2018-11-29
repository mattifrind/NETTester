/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.awt.Rectangle;
import java.util.Objects;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class BoundingBox {
    private double x = 0.0;
    private double y = 0.0;
    private double width = 0.0;
    private double height = 0.0;
    private double prob = 0.0;
    private String clasz = "";

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
    
    public double distance(BoundingBox box) {
        Pair<Double, Double> center1 = getCenter();
        Pair<Double, Double> center2 = box.getCenter();
        double deltaX = Math.abs(center1.getKey() - center2.getKey());
        double deltaY = Math.abs(center1.getValue() - center2.getValue());
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
    
    /**
     * erstellt eine Bounding Box aus 2 Punkten (links-oben, rechts-unten)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public BoundingBox(double x1, double y1, double x2, double y2) {
        this(x1, y1, x2, y2, 1, "");
    }
    
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
     * @param rect
     * @param clasz
     */
    public BoundingBox(Rectangle rect, String clasz) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
        prob = 1;
        this.clasz = clasz;
    }
    
    public static BoundingBox of(String line, String probability, String clasz) {
        String[] tempValues = line.trim().split("\\s+");
        if (tempValues.length != 4) {
            throw new IllegalArgumentException("Unexpected line: " + line);
        }
        return new BoundingBox(tempValues[0], tempValues[1], tempValues[2], tempValues[3], probability, clasz);
    }
    
    public BoundingBox(String x, String y, String width, String height, String pb, String clasz) {
        this(Double.valueOf(x), Double.valueOf(y), Double.valueOf(width), Double.valueOf(height), Double.valueOf(pb), clasz);
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
        if (!Objects.equals(this.clasz, other.clasz)) {
            return false;
        }
        return true;
    }
    
    
    
}
