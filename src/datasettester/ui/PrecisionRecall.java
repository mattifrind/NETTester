/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 *
 * @author matti
 */
public class PrecisionRecall extends Application {
    
    static List<Pair<Double, Double>> liste = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("recall");
        yAxis.setLabel("precision");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Stock Monitoring, 2010");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        liste.forEach(e -> {
            series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        lineChart.setCreateSymbols(false);
        lineChart.getData().add(series);  
        
        Scene scene  = new Scene(lineChart,800,600);
        primaryStage.setTitle("Precision-Recall");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param list
     * @param args the command line arguments
     */
    public static void main(List<Pair<Double, Double>> list, String[] args) {
        PrecisionRecall ui = new PrecisionRecall();
        liste = list;
        ui.launch(args);
    }
    
}
