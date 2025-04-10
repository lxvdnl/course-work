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
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SurfaceGraphController {

    @FXML
    public Button drawButton;

    @FXML
    private Canvas canvas;

    @FXML
    private Button bifurcationButton;

    int N;
    double P, R;

    double minX, maxX, minY, maxY;

    public TextField textFieldN;
    public Slider sliderN;

    public TextField textFieldP;
    public Slider sliderP;

    public TextField textFieldR;
    public Slider sliderR;

    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();
    private final SurfaceRenderer surfaceRenderer = new SurfaceRendererImpl();

    List<Point2D> surfacePoints;

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

        // Слушатель для слайдера N
        sliderN.valueProperty().addListener((observable, oldValue, newValue) -> {
            textFieldN.setText(String.valueOf(newValue.intValue()));
            N = newValue.intValue();
            surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
        });

        // Слушатель для слайдера P
        sliderP.valueProperty().addListener((observable, oldValue, newValue) -> {
            textFieldP.setText(String.format("%.2f", newValue.doubleValue()));
            P = newValue.doubleValue();
            plotGraph();
        });

        // Слушатель для слайдера R
        sliderR.valueProperty().addListener((observable, oldValue, newValue) -> {
            textFieldR.setText(String.format("%.2f", newValue.doubleValue()));
            R = newValue.doubleValue();
            plotGraph();
        });

        Platform.runLater(() -> {
            surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
        });

        bifurcationButton.setOnAction(event -> openBifurcationWindow(N, R));
    }

    private void plotGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Очистка canvas перед рисованием

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        minX = Params.MIN_X;
        maxX = Params.X_END;
        minY = Params.MIN_Y;
        maxY = Params.MAX_Y;

        // Рисуем оси
        drawAxes(gc, canvasWidth, canvasHeight);

        List<Point2D> points = calculateGraphPoints();

        findMinMaxY(points, surfacePoints);
        // Отрисовка координатных прямых
        drawCoordinateLines(gc, canvasWidth, canvasHeight, minX, maxX, minY, maxY);

        // Отрисовка точек
        drawPoints(gc, surfacePoints, canvasWidth, canvasHeight, minX, maxX, minY, maxY, Color.RED);
        drawPoints(gc, points, canvasWidth, canvasHeight, minX, maxX, minY, maxY, Color.BLUE);
    }

    private void drawCoordinateLines(GraphicsContext gc, double width, double height, double minX, double maxX, double minY, double maxY) {
        gc.setStroke(Color.LIGHTGRAY);  // Устанавливаем более светлый цвет для сетки
        gc.setLineWidth(1);

        // Отрисовка вертикальных координатных линий
        double stepX = (width - 100) / 10;  // 10 делений по оси X
        for (int i = 0; i <= 10; i++) {
            double x = 50 + i * stepX;
            gc.strokeLine(x, 50, x, height - 50);  // Рисуем вертикальные линии
        }

        // Отрисовка горизонтальных координатных линий
        double stepY = (height - 100) / 10;  // 10 делений по оси Y
        for (int i = 0; i <= 10; i++) {
            double y = 50 + i * stepY;
            gc.strokeLine(50, y, width - 50, y);  // Рисуем горизонтальные линии
        }
    }

    private void findMinMaxY(List<Point2D> points, List<Point2D> surfacePoints) {
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        // Перебираем точки в points
        for (Point2D point : points) {
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            }
        }

        // Перебираем точки в surfacePoints
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

    private void drawAxes(GraphicsContext gc, double width, double height) {
        // Рисуем ось X (слева направо)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(50, height - 50, width - 50, height - 50);  // Ось X

        // Рисуем ось Y (сверху вниз)
        gc.strokeLine(50, 50, 50, height - 50);  // Ось Y

        // Подписи осей
        gc.setFill(Color.BLACK);
        gc.fillText("τ", width - 40, height - 20);  // Подпись оси X
        gc.fillText("x", 20, 40);  // Подпись оси Y

        // Деления на оси X
        double stepX = (width - 100) / 10;  // 10 делений
        for (int i = 0; i <= 10; i++) {
            double x = 50 + i * stepX;
            gc.fillText(String.format("%.2f", minX + i * (maxX - minX) / 10), x - 10, height - 30);  // Метки для оси X
        }

        // Деления на оси Y (снизу вверх)
        double stepY = (height - 100) / 10;  // 10 делений
        for (int i = 0; i <= 10; i++) {
            double y = height - 50 - i * stepY;  // Инвертируем Y, чтобы считать сверху вниз
            gc.strokeLine(50, y, 60, y);  // Деления на оси Y
            gc.fillText(String.format("%.2f", minY + i * (maxY - minY) / 10), 20, y + 5);  // Метки для оси Y
        }
    }

    private void drawPoints(GraphicsContext gc, List<Point2D> points, double width, double height,
                            double minX, double maxX, double minY, double maxY, Color color) {
        gc.setFill(color);
        for (Point2D point : points) {
            // Сдвиг по оси X на 50 пикселей, так как ось Y начинается с 50
            double x = (point.getX() - minX) / (maxX - minX) * (width - 100) + 50;  // Сдвиг от оси X

            // Сдвиг по оси Y на 50 пикселей, так как ось X заканчивается на height - 50
            double y = height - (point.getY() - minY) / (maxY - minY) * (height - 100) - 50; // Сдвиг от оси Y

            gc.fillOval(x - 1, y - 1, 2, 2); // Рисуем точку 2x2 пикселя
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

        grid.add(new Label("pBegin:"), 0, 0);
        grid.add(pBeginField, 1, 0);
        grid.add(new Label("pEnd:"), 0, 1);
        grid.add(pEndField, 1, 1);
        grid.add(new Label("pStep:"), 0, 2);
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
                Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
                Stage stage = new Stage();
                stage.setScene(scene);

                BifurcationController controller = fxmlLoader.getController();
                controller.initData(N, R, pBegin, pEnd, pStep);

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
