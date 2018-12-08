package nettester.ui;

import nettester.PrecRecResult;
import nettester.Result;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javax.imageio.ImageIO;

/**
 *
 * @author Matti J. Frind
 */
public class PrecisionRecall extends Application {
    
    private static List<Result> liste = null;
    
    @Override
    public void start(Stage primaryStage) {
        TabPane pane = new TabPane(getBallTab(), getFootTab());
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Scene scene  = new Scene(pane,800,600);
        URL url = this.getClass().getResource("style.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm(); 
        scene.getStylesheets().add(css);
        primaryStage.setTitle("Precision-Recall");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates a Tab containing the chart with the ball data and a button,
     * which saves the chart.
     * @return chart-tab
     */
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
    
    /**
     * Creates a Tab containing the chart with the foot data and a button,
     * which saves the chart.
     * @return chart-tab
     */
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
    
    /**
     * Computes a line chart with values from PrecRecResult list and a title
     * @param values
     * @param title
     * @return LineChart with the given values
     */
    private LineChart createLineChart(List<PrecRecResult> values, String title) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("recall");
        yAxis.setLabel("precision");
        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Precision(Recall) - " + title);     
        lineChart.setCreateSymbols(true);
        
        if(title.equalsIgnoreCase("ball")) {
            addBallSeries(lineChart, values);
            yAxis.setUpperBound(1.05);
            yAxis.setLowerBound(0.5);
            yAxis.setTickUnit(0.05);
            xAxis.setUpperBound(0.65);
            xAxis.setLowerBound(0);
            xAxis.setTickUnit(0.05);
        } else {
            addFootSeries(lineChart, values);
            yAxis.setUpperBound(1.05);
            yAxis.setLowerBound(0.2);
            yAxis.setTickUnit(0.05);
            xAxis.setUpperBound(0.275);
            xAxis.setLowerBound(0);
            xAxis.setTickUnit(0.025);
        }
        
        return lineChart;
    }
    
    private void addBallSeries(LineChart chart, List<PrecRecResult> values) {
        //IoU 0.1
        XYChart.Series series = new XYChart.Series();
        series.setName("IoU=0%");
        values.get(0).getPrecRecResult().forEach(e -> {
            series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series);  
        
        //IoU 0.2
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("IoU=20%");
        values.get(1).getPrecRecResult().forEach(e -> {
            series2.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series2); 
        
        //IoU 0.3
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("IoU=30%");
        values.get(2).getPrecRecResult().forEach(e -> {
            series3.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series3); 
        
        //IoU 0.4
        XYChart.Series series4 = new XYChart.Series();
        series4.setName("IoU=40%");
        values.get(3).getPrecRecResult().forEach(e -> {
            series4.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series4); 
        
        //IoU 0.5
        XYChart.Series series5 = new XYChart.Series();
        series5.setName("IoU=50%");
        values.get(4).getPrecRecResult().forEach(e -> {
            series5.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series5);
    }
    
    private void addFootSeries(LineChart chart, List<PrecRecResult> values) {
        //IoU 0.1
        XYChart.Series series = new XYChart.Series();
        series.setName("IoU=0");
        values.get(0).getPrecRecResult().forEach(e -> {
            series.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series);  
        //IoU 0.3
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("IoU=0.3");
        values.get(2).getPrecRecResult().forEach(e -> {
            series2.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        });
        chart.getData().add(series2); 
    }
    
    /**
     * @return List with the PrecRecResult of the ball class
     */
    private List<PrecRecResult> getBallData() {
        List<PrecRecResult> balldata = new LinkedList<>();
        liste.forEach(e -> {
            balldata.add(e.getBallResult());
        });
        return balldata;
    }
    
    /**
     * @return List with the PrecRecResult of the foot class
     */
    private List<PrecRecResult> getFootData() {
        List<PrecRecResult> footdata = new LinkedList<>();
        liste.forEach(e -> {
            footdata.add(e.getFootResult());
        });
        return footdata;
    }

    /**
     * @param list
     */
    public static void start(List<Result> list) {
        PrecisionRecall ui = new PrecisionRecall();
        liste = list;
        ui.launch(null);
    }
    
}
