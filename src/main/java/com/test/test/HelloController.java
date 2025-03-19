package com.test.test;

import com.test.test.calculations.RungeKuttaSolver;
import com.test.test.calculations.impl.FunctionProvider;
import com.test.test.calculations.impl.RungeKuttaSolverImpl;
import com.test.test.config.ChartConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.List;

public class HelloController {
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private ChartConfig chartConfig;

    @FXML
    public void initialize() {
        chartConfig = new ChartConfig(
                Params.MIN_X, Params.X_END, Params.MIN_Y, Params.MAX_Y
        );

        chartConfig.applyAxisConfig(xAxis, yAxis);

        Platform.runLater(this::plotGraph);
    }

    private void plotGraph() {
        RungeKuttaSolver solver = new RungeKuttaSolverImpl();

        List<Point2D> points = solver.plotGraph(
                FunctionProvider.DEFAULT_F,
                FunctionProvider.DEFAULT_G,
                FunctionProvider.DEFAULT_SURFACE_FUNCTION,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE,
                Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                Params.X_END, Params.STEP, Params.TOLERANCE,
                Params.MIN_STEP, Params.MAX_STEP,
                Params.P, Params.E, Params.Y, Params.U, Params.U);


        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        lineChart.setLegendVisible(false);

        double newMaxY = chartConfig.getMaxY(), newMinY = chartConfig.getMinY();
        int i = 0;
        for (Point2D point : points) {
            if(point.getY() > newMaxY) newMaxY = point.getY();
            if(point.getY() < newMinY) newMinY = point.getY();
            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            i++;
        }

        chartConfig.setMaxY(newMaxY);
        chartConfig.setMinY(newMinY);

        chartConfig.applyAxisConfig(xAxis, yAxis);

        chartConfig.applySeriesConfig(series);

        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

}
