package com.test.test;

import com.test.test.calculations.FuncDerivativeSurface;
import com.test.test.calculations.FuncSurface;
import com.test.test.calculations.RungeKuttaSolver;
import com.test.test.calculations.SurfaceRenderer;
import com.test.test.calculations.impl.FunctionProvider;
import com.test.test.calculations.impl.RungeKuttaSolverImpl;
import com.test.test.calculations.impl.SurfaceRendererImpl;
import com.test.test.config.ChartConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloController {
    public TextField textFieldR;
    public Slider sliderR;
    public TextField textFieldP;
    public Slider sliderP;
    public TextField textFieldN;
    public Slider sliderN;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private ChartConfig chartConfig;
    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();
    private final SurfaceRenderer surfaceRenderer = new SurfaceRendererImpl();

    @FXML
    public void initialize() {
        chartConfig = new ChartConfig(
                Params.MIN_X, Params.X_END, Params.MIN_Y, Params.MAX_Y
        );

        chartConfig.applyAxisConfig(xAxis, yAxis);

        Platform.runLater(this::plotGraph);
    }

    private void plotGraph() {

        Map<FuncSurface, FuncDerivativeSurface> surfacesMap = new HashMap<>();
        surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_1,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_1);
        surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_2,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_2);
        surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_3,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_3);

        System.out.println("Runge-kutta start");

        List<Point2D> points = rungeKuttaSolver.plotGraph(
                FunctionProvider.DEFAULT_F,
                FunctionProvider.DEFAULT_G,
                surfacesMap,
                Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                Params.X_END, Params.STEP, Params.TOLERANCE,
                Params.MIN_STEP, Params.MAX_STEP);

        System.out.println("Runge-kutta ends");

        List<FuncSurface> surfaces = new ArrayList<>();
        surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_1);
        surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_2);
        surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_3);
        List<Point2D> surfacePoints = surfaceRenderer.render(surfaces, Params.MIN_X, Params.X_END, Params.STEP);

        System.out.println("Surface rendered");

        points.addAll(surfacePoints);

        System.out.println("Points added");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        lineChart.setLegendVisible(false);

        double newMaxY = chartConfig.getMaxY(), newMinY = chartConfig.getMinY();

        List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();
        for (Point2D point : points) {
            if (point.getY() > newMaxY) newMaxY = point.getY();
            if (point.getY() < newMinY) newMinY = point.getY();
            dataList.add(new XYChart.Data<>(point.getX(), point.getY()));
        }
        series.getData().addAll(dataList);

        System.out.println("Points added");

        chartConfig.setMaxY(newMaxY);
        chartConfig.setMinY(newMinY);

        chartConfig.applyAxisConfig(xAxis, yAxis);

        chartConfig.applySeriesConfig(series);

        lineChart.getData().clear();
        lineChart.getData().add(series);

        System.out.println("Graph plotted");
    }

}
