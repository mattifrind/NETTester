package datasettester;

import java.util.LinkedList;
import java.util.List;
import javafx.util.Pair;

/**
 * Represents a list of Precall-Recall-Pairs.
 * @author Matti J. Frind
 */
public class PrecRecResult {
    private List<Pair<Double, Double>> precRecResult = new LinkedList<>();

    public List<Pair<Double, Double>> getPrecRecResult() {
        return precRecResult;
    }

    public void setPrecRecResult(List<Pair<Double, Double>> precRecResult) {
        this.precRecResult = precRecResult;
    }
    
    public void add(double value1, double value2) {
        precRecResult.add(new Pair<>(value1, value2));
    }
    
    public void add(Pair<Double, Double> pair) {
        precRecResult.add(pair);
    }
}
