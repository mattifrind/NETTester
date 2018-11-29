/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.imageio.ImageIO;

/**
 *
 * @author matti
 */
public class PrecisionRecall extends Application {
    
    static List<Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>> liste = null;
    
    @Override
    public void start(Stage primaryStage) {
        TabPane pane = new TabPane(getBallTab(), getFootTab());
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Scene scene  = new Scene(pane,800,600);
        primaryStage.setTitle("Precision-Recall");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Tab getBallTab() {
        LineChart ball = createLineChart(getBallData(), "ball");
        Button btnBall = new Button("save Image");
        btnBall.setOnAction(evt -> {
            WritableImage image = ball.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("PrecRecall-Ball.png"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Your file was written to: " + System.getProperty("user.dir") + "\\PrecRecall-Ball.png");
                alert.showAndWait();
            } catch (IOException ex) {}
        });
        StackPane ballPane = new StackPane(ball, btnBall);
        ballPane.setAlignment(Pos.TOP_LEFT);
        Tab ballTap = new Tab("ball", ballPane);
        ballTap.setContent(ballPane);
        return ballTap;
    }
    
    private Tab getFootTab() {
        LineChart foot = createLineChart(getFootData(), "foot");
        Button btnFoot = new Button("save Image");
        btnFoot.setOnAction(evt -> {
            WritableImage image = foot.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File("PrecRecall-Foot.png"));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Your file was written to: " + System.getProperty("user.dir") + "\\PrecRecall-Foot.png");
                alert.showAndWait();
            } catch (IOException ex) {}
        });
        StackPane footPane = new StackPane(foot, btnFoot);
        footPane.setAlignment(Pos.TOP_LEFT);
        Tab footTap = new Tab("foot", footPane);
        footTap.setContent(footPane);
        return footTap;
    }
    
    private LineChart createLineChart(List<List<Pair<Double, Double>>> values, String title) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("recall");
        yAxis.setLabel("precision");
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Precision(Recall) - " + title);     
        lineChart.setCreateSymbols(false);
        
        if(title.equalsIgnoreCase("ball")) {
            addBallSeries(lineChart, values);
        } else {
            addFootSeries(lineChart, values);
        }
        
        return lineChart;
    }
    
    private void addBallSeries(LineChart chart, List<List<Pair<Double, Double>>> values) {
        //IoU 0.1
        XYChart.Series series = new XYChart.Series();
        series.setName("IoU=0%");
        values.get(0).forEach(e -> {
            series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series);  
        
        //IoU 0.2
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("IoU=20%");
        values.get(1).forEach(e -> {
            series2.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series2); 
        
        //IoU 0.3
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("IoU=30%");
        values.get(2).forEach(e -> {
            series3.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series3); 
    }
    
    private void addFootSeries(LineChart chart, List<List<Pair<Double, Double>>> values) {
        //IoU 0.1
        XYChart.Series series = new XYChart.Series();
        series.setName("IoU=0");
        values.get(0).forEach(e -> {
            series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series);  
        //IoU 0.3
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("IoU=0.3");
        values.get(2).forEach(e -> {
            series2.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series2); 
    }
    
    private List<List<Pair<Double, Double>>> getBallData() {
        List<List<Pair<Double, Double>>> balldata = new LinkedList<>();
        liste.forEach(e -> {
            balldata.add(e.getKey());
        });
        return balldata;
    }
    
    private List<List<Pair<Double, Double>>> getFootData() {
        List<List<Pair<Double, Double>>> footdata = new LinkedList<>();
        liste.forEach(e -> {
            footdata.add(e.getValue());
        });
        return footdata;
    }

    /**
     * @param list
     */
    public static void start(List<Pair<List<Pair<Double, Double>>, List<Pair<Double, Double>>>> list) {
        PrecisionRecall ui = new PrecisionRecall();
        liste = list;
        ui.launch(null);
    }
    
}
