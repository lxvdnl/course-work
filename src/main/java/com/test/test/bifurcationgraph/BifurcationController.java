package com.test.test.bifurcationgraph;

import com.test.test.config.ChartConfig;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.RungeKuttaSolver;
import com.test.test.surfacegraph.calculations.impl.RungeKuttaSolverImpl;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class BifurcationController {

    public LineChart<Number, Number> lineChart;

    public NumberAxis xAxis;
    public NumberAxis yAxis;

    private ChartConfig chartConfig;
    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();

    @FXML
    public void initialize() {
        chartConfig = new ChartConfig(
                Params.MIN_X, Params.X_END, Params.MIN_Y, Params.MAX_Y // new intervals here for p ad y
        );
        chartConfig.applyAxisConfig(xAxis, yAxis);
    }
}