package nettester;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 * Contains functions which calculate the mean precision with a list with 
 * precision and recall pairs using 11 evenly spaced recall values.
 * @author Matti J. Frind
 */
public class MeanComputer {
    
    /**
     * Computes the mean precision with a list with 
     * precision and recall pairs using 11 evenly spaced recall values.
     * @param list
     * @return
     */
    public double computeMean(PrecRecResult list) {
        double sum = 0.0;
        for (double i = 0; i < 1; i+=0.1) {
            double key = findNearestKey(getKeys(list), i);
            double tempValue = getValue(list, key);
            sum += tempValue;
            if (Double.isNaN(sum)) {
                System.out.println("");
            }
        }
        return sum/11.0;
    }
    
    /**
     * Finds the nearest available key, because the list contains discreet values.
     * @param list of all possible keys
     * @param key exact key
     * @return the nearest available key
     */
    private double findNearestKey(List<Double> list, Double key) {
        if (list.contains(key)) return key;
        double minDistance = Double.MAX_VALUE;
        double minKey = 0;
        for (Double value : list) {
            if (Math.abs(value-key) < minDistance) {
                minDistance = Math.abs(value-key);
                minKey = value;
            }
        }
        return minKey;
    }
    
    /**
     * Returns the value to a specific key.
     * @param list
     * @param key
     */
    private double getValue(PrecRecResult list, double key) {
        for (Pair<Double, Double> pair : list.getPrecRecResult()) {
            if (pair.getKey() == key) {
                return pair.getValue();
            }
        }
        return 0;
    }
    
    /**
     * Creates a list with all keys from a list.
     * @param list
     */
    private List<Double> getKeys(PrecRecResult list) {
        List<Double> newList = new ArrayList<>();
        for (Pair<Double, Double> pair : list.getPrecRecResult()) {
            newList.add(pair.getKey());
        }
        return newList;
    }
}
