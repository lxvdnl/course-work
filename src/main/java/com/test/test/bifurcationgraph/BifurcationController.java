package com.test.test.bifurcationgraph;

import com.test.test.bifurcationgraph.calculations.impl.RungeKuttaWithImpacts;
import com.test.test.bifurcationgraph.calculations.impl.RungeKuttaWithImpactsImpl;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class BifurcationController {

    @FXML
    private Canvas canvas1;
    @FXML
    private Canvas canvas2;
    @FXML
    private Canvas canvas3;

    private List<Canvas> canvases;

    private int N;
    private double R;
    private double pBegin, pEnd, pStep;
    double minX, maxX, minY, maxY;

    List<List<Point2D>> bifurcationPoints;

    private final RungeKuttaWithImpacts rungeKuttaSolver = new RungeKuttaWithImpactsImpl();

    public void initData(int N, double R, double pBegin, double pEnd, double pStep) {
        this.N = N;
        this.R = R;
        this.pBegin = pBegin;
        this.pEnd = pEnd;
        this.pStep = pStep;
    }

    @FXML
    public void initialize() {
        canvases = List.of(canvas1, canvas2, canvas3);
        Platform.runLater(this::plotGraph);
    }

    private void plotGraph() {
        double canvasWidth = canvas1.getWidth();
        double canvasHeight = canvas1.getHeight();

        minX = pBegin;
        maxX = pEnd;
        minY = Params.MIN_Y;
        maxY = Params.MAX_Y;

        bifurcationPoints = calculateBifurcationPoints();

        findMinMaxY(bifurcationPoints);

        for (int i = 0; i < Math.min(N, canvases.size()); i++) {
            Canvas currentCanvas = canvases.get(i);
            GraphicsContext gc = currentCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, currentCanvas.getWidth(), currentCanvas.getHeight());
            drawAxes(gc, canvasWidth, canvasHeight, minX, maxX, minY, maxY);
            drawPoints(gc, bifurcationPoints.get(i), canvasWidth, canvasHeight, minX, maxX, minY, maxY);
        }
    }

    private void drawPoints(GraphicsContext gc, List<Point2D> bifurcationPoints, double width, double height,
                            double minX, double maxX, double minY, double maxY) {
        gc.setFill(Color.BLUE);
        for (Point2D point : bifurcationPoints) {
            double x = (point.getX() - minX) / (maxX - minX) * (width - 100) + 50;
            double y = height - (point.getY() - minY) / (maxY - minY) * (height - 100) - 50;

            gc.fillOval(x - 1, y - 1, 2, 2); // Draw point
        }
    }

    private List<List<Point2D>> calculateBifurcationPoints() {
        List<List<Point2D>> bifurcationPoints = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            bifurcationPoints.add(new ArrayList<>());
        }

        for (double p = pBegin; p <= pEnd; p += pStep) {
            System.out.println("p=" + p);
            List<List<Point2D>> newPoints = rungeKuttaSolver.plotGraph(
                    FunctionProvider.DEFAULT_F,
                    FunctionProvider.DEFAULT_G,
                    Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                    Params.X_END, Params.STEP, Params.BIFURCATION_P_TOLERANCE,
                    Params.BIFURCATION_P_MIN_STEP, Params.MAX_STEP,
                    N, p, R
            );

            for (int i = 0; i < N; i++) {
                bifurcationPoints.get(i).addAll(newPoints.get(i));
            }
        }

        return bifurcationPoints;
    }

    private void findMinMaxY(List<List<Point2D>> points) {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (List<Point2D> sublist : points) {
            for (Point2D point : sublist) {
                if (point.getY() < minY) {
                    minY = point.getY();
                }
                if (point.getY() > maxY) {
                    maxY = point.getY();
                }
            }
        }

        this.minY = minY;
        this.maxY = maxY + maxY / 4;
    }

    private void drawCoordinateLines(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);

        double stepX = (width - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            double x = 50 + i * stepX;
            gc.strokeLine(x, 50, x, height - 50);
        }

        double stepY = (height - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            double y = 50 + i * stepY;
            gc.strokeLine(50, y, width - 50, y);
        }
    }

    private void drawAxes(GraphicsContext gc, double width, double height, double minX, double maxX, double minY, double maxY) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(50, height - 50, width - 50, height - 50);  // X
        gc.strokeLine(50, 50, 50, height - 50);  // Y

        gc.setFill(Color.BLACK);
        gc.fillText("P", width - 40, height - 20);
        gc.fillText("Z", 20, 40);

        double stepX = (width - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            double x = 50 + i * stepX;
            gc.fillText(String.format("%.2f", minX + i * (maxX - minX) / 10), x - 10, height - 30);
        }

        double stepY = (height - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            double y = height - 50 - i * stepY;
            gc.strokeLine(50, y, 60, y);
            gc.fillText(String.format("%.2f", minY + i * (maxY - minY) / 10), 20, y + 5);
        }

        drawCoordinateLines(gc, width, height);
    }

}