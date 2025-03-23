package com.test.test.surfacegraph;

import com.test.test.HelloApplication;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.RungeKuttaSolver;
import com.test.test.surfacegraph.calculations.SurfaceRenderer;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import com.test.test.surfacegraph.calculations.impl.RungeKuttaSolverImpl;
import com.test.test.surfacegraph.calculations.impl.SurfaceRendererImpl;
import com.test.test.config.ChartConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SurfaceGraphController {

    @FXML
    public Button drawButton;

    @FXML
    private Button bifurcationButton;

    int N;
    double P, R;

    public TextField textFieldN;
    public Slider sliderN;

    public TextField textFieldP;
    public Slider sliderP;

    public TextField textFieldR;
    public Slider sliderR;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private ChartConfig chartConfig;
    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();
    private final SurfaceRenderer surfaceRenderer = new SurfaceRendererImpl();

    List<Point2D> surfacePoints;

    @FXML
    public void initialize() {
        chartConfig = new ChartConfig(
                Params.MIN_X, Params.X_END, Params.MIN_Y, Params.MAX_Y
        );
        lineChart.setLegendVisible(false);
        chartConfig.applyAxisConfig(xAxis, yAxis);

        P = Params.P;
        R = Params.R;
        N = Params.N;

        sliderN.setValue(N);
        textFieldN.setText(String.valueOf(N));
        sliderP.setValue(P);
        textFieldP.setText(String.format("%.2f", P));
        sliderR.setValue(Params.R);
        textFieldR.setText(String.format("%.2f", Params.R));

        sliderN.valueProperty().addListener((observable, oldValue, newValue) ->
            textFieldN.setText(String.valueOf(newValue.intValue())));

        sliderP.valueProperty().addListener((observable, oldValue, newValue) ->
            textFieldP.setText(String.format("%.2f", newValue.doubleValue())));

        sliderR.valueProperty().addListener((observable, oldValue, newValue) ->
            textFieldR.setText(String.format("%.2f", newValue.doubleValue())));

        surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);

        Platform.runLater(this::plotGraph);

        drawButton.setOnAction(event -> {
            if (updateParams()) surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
            System.out.println("graph must be on screen");
        });

        bifurcationButton.setOnAction(event -> openBifurcationWindow());
    }

    private void plotGraph() {

        System.out.println("plotGraph");

        List<Point2D> points = rungeKuttaSolver.plotGraph(
                FunctionProvider.DEFAULT_F,
                FunctionProvider.DEFAULT_G,
                Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                Params.X_END, Params.STEP, Params.TOLERANCE,
                Params.MIN_STEP, Params.MAX_STEP,
                N, P, R);

        System.out.println("points drawn: " + points.size());

        points.addAll(surfacePoints);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        double newMaxY = 0, newMinY = chartConfig.getMinY();

        System.out.println("start transfer points");

        List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();
        for (Point2D point : points) {
            if (point.getY() > newMaxY) newMaxY = point.getY();
            if (point.getY() < newMinY) newMinY = point.getY();
            dataList.add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        System.out.println("end transfer points");

        series.getData().addAll(dataList);

        System.out.println("series drawn: " + series.getData().size());

        chartConfig.setMaxY(newMaxY);
        chartConfig.setMinY(newMinY);

        chartConfig.applyAxisConfig(xAxis, yAxis);

        chartConfig.applySeriesConfig(series);

        lineChart.setAnimated(false);
        if (!lineChart.getData().isEmpty()) {
            lineChart.getData().removeFirst();
        }
        lineChart.getData().add(series);
        lineChart.setAnimated(true);

        System.out.println("graph drawn");
    }

    private boolean updateParams() {
        P = sliderP.getValue();
        R = sliderR.getValue();
        int tmpN = (int) sliderN.getValue();
        if (tmpN != N) {
            N = tmpN;
            return true;
        }
        return false;
    }

    private void openBifurcationWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("bifurcation-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
