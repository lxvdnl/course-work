package com.test.test.bifurcationgraph;

import com.test.test.bifurcationgraph.calculations.impl.RungeKuttaWithImpactsImpl;
import com.test.test.config.ChartConfig;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.RungeKuttaSolver;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class BifurcationController {


    public NumberAxis xAxis;
    public NumberAxis yAxis;
    public ScatterChart<Number, Number> scatterChart;

    private ChartConfig chartConfig;
    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaWithImpactsImpl();

    private int N;
    private double R;
    private double pBegin, pEnd, pStep;

    public void initData(int N, double R, double pBegin, double pEnd, double pStep) {
        this.N = N;
        this.R = R;
        this.pBegin = pBegin;
        this.pEnd = pEnd;
        this.pStep = pStep;
    }

    @FXML
    public void initialize() {
        setupChartConfig();
        Platform.runLater(this::plotGraph);
    }

    private void setupChartConfig() {
        chartConfig = new ChartConfig(
                Params.BIFURCATION_P_BEGIN, Params.BIFURCATION_P_END, Params.MIN_Y, Params.MAX_Y
        );
        scatterChart.setLegendVisible(false);
    }

    private void plotGraph() {
        System.out.println("plotGraph");

        List<Point2D> points = calculateGraphPoints();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        List<XYChart.Data<Number, Number>> dataList = processGraphPoints(points);

        series.getData().addAll(dataList);

        System.out.println("series drawn: " + series.getData().size());

        chartConfig.applyAxisConfig(xAxis, yAxis);
        chartConfig.applySeriesConfig(series);

        scatterChart.setAnimated(false);
        if (!scatterChart.getData().isEmpty()) {
            scatterChart.getData().removeFirst();
        }
        scatterChart.getData().add(series);
        scatterChart.setAnimated(true);

        System.out.println("graph drawn");
    }

    private List<Point2D> calculateGraphPoints() {
        List<Point2D> points = new ArrayList<>();
        for (double p = pBegin; p <= pEnd; p += pStep) {
            System.out.println("p=" + p);
            points.addAll(
                    rungeKuttaSolver.plotGraph(
                            FunctionProvider.DEFAULT_F,
                            FunctionProvider.DEFAULT_G,
                            Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                            Params.X_END, Params.STEP, Params.TOLERANCE,
                            Params.MIN_STEP, Params.MAX_STEP,
                            N, p, R)
            );
        }
        return points;
    }

    private List<XYChart.Data<Number, Number>> processGraphPoints(List<Point2D> points) {
        double newMaxY = 0, newMinY = chartConfig.getMinY();
        List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();
        for (Point2D point : points) {
            if (point.getY() > newMaxY) newMaxY = point.getY();
            if (point.getY() < newMinY) newMinY = point.getY();
            dataList.add(new XYChart.Data<>(point.getX(), point.getY()));
        }
        chartConfig.setMaxY(newMaxY);
        chartConfig.setMinY(newMinY);
        chartConfig.setMinX(pBegin);
        chartConfig.setMaxX(pEnd);
        return dataList;
    }
}