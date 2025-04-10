package com.test.test.bifurcationgraph;

import com.test.test.bifurcationgraph.calculations.impl.RungeKuttaWithImpactsImpl;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.RungeKuttaSolver;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class BifurcationController {

    @FXML
    private Canvas canvas;

    public NumberAxis xAxis;
    public NumberAxis yAxis;

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
        Platform.runLater(this::plotGraph);
    }

    private void plotGraph() {
        // Получаем графический контекст для Canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Очищаем canvas

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        double minX = 0.29;
        double maxX = 0.3;
        double minY = Params.MIN_Y;
        double maxY = Params.MAX_Y;

        List<Point2D> points = calculateGraphPoints();

        gc.setFill(Color.RED);
        for (Point2D point : points) {
            double x = (point.getX() - minX) / (maxX - minX) * canvasWidth;
            double y = canvasHeight - (point.getY() - minY) / (maxY - minY) * canvasHeight;
            gc.fillOval(x - 1, y - 1, 2, 2);
        }

        System.out.println("Graph drawn on canvas");
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
}