package com.test.test.surfacegraph;

import com.test.test.HelloApplication;
import com.test.test.bifurcationgraph.BifurcationController;
import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.RungeKuttaSolver;
import com.test.test.surfacegraph.calculations.SurfaceRenderer;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import com.test.test.surfacegraph.calculations.impl.RungeKuttaSolverImpl;
import com.test.test.surfacegraph.calculations.impl.SurfaceRendererImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SurfaceGraphController {

    @FXML
    private Canvas canvas;

    @FXML
    private Button bifurcationButton;

    public TextField textFieldN;
    public Slider sliderN;

    public TextField textFieldP;
    public Slider sliderP;

    public TextField textFieldR;
    public Slider sliderR;


    int N;
    double P, R;
    double minX, maxX, minY, maxY;

    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();
    private final SurfaceRenderer surfaceRenderer = new SurfaceRendererImpl();

    List<Point2D> surfacePoints, graphPoints;

    @FXML
    public void initialize() {
        P = Params.P;
        R = Params.R;
        N = Params.N;

        sliderN.setValue(N);
        textFieldN.setText(String.valueOf(N));
        sliderP.setValue(P);
        textFieldP.setText(String.format("%.2f", P));
        sliderR.setValue(Params.R);
        textFieldR.setText(String.format("%.2f", Params.R));

        // N slider listener
        sliderN.valueProperty().addListener((_, _, newValue) -> {
            textFieldN.setText(String.valueOf(newValue.intValue()));
            N = newValue.intValue();
            surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
        });

        // P slider listener
        sliderP.valueProperty().addListener((_, _, newValue) -> {
            textFieldP.setText(String.format("%.2f", newValue.doubleValue()));
            P = newValue.doubleValue();
            plotGraph();
        });

        // R slider listener
        sliderR.valueProperty().addListener((_, _, newValue) -> {
            textFieldR.setText(String.format("%.2f", newValue.doubleValue()));
            R = newValue.doubleValue();
            plotGraph();
        });

        // Entry plotting
        Platform.runLater(() -> {
            surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
        });

        bifurcationButton.setOnAction(_ -> openBifurcationWindow(N, R));
    }

    // Main method for plotting all visuals in canvas
    private void plotGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        minX = Params.MIN_X;
        maxX = Params.X_END;

        graphPoints = calculateGraphPoints();
        findMinMaxY(graphPoints, surfacePoints);

        drawAxes(gc, canvasWidth, canvasHeight, minX, maxX, minY, maxY);

        drawPoints(gc, surfacePoints, canvasWidth, canvasHeight, minX, maxX, minY, maxY, Color.RED);
        drawPoints(gc, graphPoints, canvasWidth, canvasHeight, minX, maxX, minY, maxY, Color.BLUE);
    }

    private void findMinMaxY(List<Point2D> graphPoints, List<Point2D> surfacePoints) {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point2D point : graphPoints) {
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            }
        }

        for (Point2D point : surfacePoints) {
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            }
        }

        this.minY = minY;
        this.maxY = maxY;
    }

    private void drawAxes(GraphicsContext gc, double width, double height, double minX, double maxX, double minY, double maxY) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(50, height - 50, width - 50, height - 50); // X
        gc.strokeLine(50, 50, 50, height - 50); // Y

        gc.setFill(Color.BLACK);
        gc.fillText("τ", width - 40, height - 20);
        gc.fillText("x", 20, 40);

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

    private void drawPoints(GraphicsContext gc, List<Point2D> points, double width, double height,
                            double minX, double maxX, double minY, double maxY, Color color) {
        gc.setFill(color);
        for (Point2D point : points) {
            double x = (point.getX() - minX) / (maxX - minX) * (width - 100) + 50;
            double y = height - (point.getY() - minY) / (maxY - minY) * (height - 100) - 50;
            gc.fillOval(x - 1, y - 1, 2, 2); // Draw point
        }
    }

    private List<Point2D> calculateGraphPoints() {
        return rungeKuttaSolver.plotGraph(
                FunctionProvider.DEFAULT_F,
                FunctionProvider.DEFAULT_G,
                Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                Params.X_END, Params.STEP, Params.TOLERANCE,
                Params.MIN_STEP, Params.MAX_STEP,
                N, P, R);
    }

    private void openBifurcationWindow(int N, double R) {
        Dialog<List<Double>> dialog = new Dialog<>();
        dialog.setTitle("Введите параметры");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField pBeginField = new TextField(String.valueOf(Params.BIFURCATION_P_BEGIN));
        TextField pEndField = new TextField(String.valueOf(Params.BIFURCATION_P_END));
        TextField pStepField = new TextField(String.valueOf(Params.BIFURCATION_P_STEP));

        grid.add(new Label("Начальное P:"), 0, 0);
        grid.add(pBeginField, 1, 0);
        grid.add(new Label("Конечное P:"), 0, 1);
        grid.add(pEndField, 1, 1);
        grid.add(new Label("Шаг:"), 0, 2);
        grid.add(pStepField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    double pBegin = Double.parseDouble(pBeginField.getText());
                    double pEnd = Double.parseDouble(pEndField.getText());
                    double pStep = Double.parseDouble(pStepField.getText());
                    return List.of(pBegin, pEnd, pStep);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: Введены некорректные значения!");
                    return null;
                }
            }
            return null;
        });

        Optional<List<Double>> result = dialog.showAndWait();
        result.ifPresent(params -> {
            try {
                double pBegin = params.get(0);
                double pEnd = params.get(1);
                double pStep = params.get(2);

                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("bifurcation-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1200, 600);
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(scene);

                BifurcationController controller = fxmlLoader.getController();
                controller.initData(N, R, pBegin, pEnd, pStep);

                stage.show();
            } catch (IOException e) {
                System.err.println("Exception: " + e);
            }
        });
    }

}
