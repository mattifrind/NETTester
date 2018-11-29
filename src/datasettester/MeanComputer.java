/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class MeanComputer {
    public double computeMean(List<Pair<Double, Double>> list) {
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
    
    public double findNearestKey(List<Double> list, Double key) {
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
    
    public double getValue(List<Pair<Double, Double>> list, double key) {
        for (Pair<Double, Double> pair : list) {
            if (pair.getKey() == key) {
                return pair.getValue();
            }
        }
        return 0;
    }
    
    public List<Double> getKeys(List<Pair<Double, Double>> list) {
        List<Double> newList = new ArrayList<>();
        for (Pair<Double, Double> pair : list) {
            newList.add(pair.getKey());
        }
        return newList;
    }
}
