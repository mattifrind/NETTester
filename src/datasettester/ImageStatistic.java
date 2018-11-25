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
public class ImageStatistic {
    private int false_negative = 0;
    private int false_positive = 0;
    private int true_positive = 0;

    /**
     * Statistics for one Image containing the
     * @param tp true positives
     * @param fp false positives
     * @param fn false_negatives
     */
    public ImageStatistic(int tp, int fp, int fn) {
        false_negative = fn;
        false_positive = fp;
        true_positive = tp;
    }

    public int getFalse_negative() {
        return false_negative;
    }

    public void setFalse_negative(int false_negative) {
        this.false_negative = false_negative;
    }

    public int getFalse_positive() {
        return false_positive;
    }

    public void setFalse_positive(int false_positive) {
        this.false_positive = false_positive;
    }

    public int getTrue_positive() {
        return true_positive;
    }

    public void setTrue_positive(int true_positive) {
        this.true_positive = true_positive;
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
        final ImageStatistic other = (ImageStatistic) obj;
        if (this.false_negative != other.false_negative) {
            return false;
        }
        if (this.false_positive != other.false_positive) {
            return false;
        }
        if (this.true_positive != other.true_positive) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImageStatistic{" + "tp=" + true_positive + ", fp=" + false_positive + ", fn=" + false_negative + '}';
    }
}
