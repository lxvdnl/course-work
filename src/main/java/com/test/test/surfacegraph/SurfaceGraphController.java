package com.test.test.surfacegraph;

import com.test.test.HelloApplication;
import com.test.test.bifurcationgraph.BifurcationController;
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
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SurfaceGraphController {

    @FXML
    public Button drawButton;
    public ScatterChart<Number, Number> scatterChart;

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
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private ChartConfig chartConfig;
    private final RungeKuttaSolver rungeKuttaSolver = new RungeKuttaSolverImpl();
    private final SurfaceRenderer surfaceRenderer = new SurfaceRendererImpl();

    List<Point2D> surfacePoints;

    @FXML
    public void initialize() {
        setupChartConfig();

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

        plotGraph();

        drawButton.setOnAction(event -> {
            if (updateParams()) surfacePoints = surfaceRenderer.render(N, Params.MIN_X, Params.X_END, Params.STEP);
            plotGraph();
        });

        bifurcationButton.setOnAction(event -> openBifurcationWindow(N, R));
    }

    private void setupChartConfig() {
        chartConfig = new ChartConfig(
                Params.MIN_X, Params.X_END, Params.MIN_Y, Params.MAX_Y
        );
        scatterChart.setLegendVisible(false);
    }

    private void plotGraph() {

        System.out.println("plotGraph");

        List<Point2D> points = calculateGraphPoints();

        System.out.println("points drawn: " + points.size());

        points.addAll(surfacePoints);

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
        return rungeKuttaSolver.plotGraph(
                FunctionProvider.DEFAULT_F,
                FunctionProvider.DEFAULT_G,
                Params.X_BEGIN, Params.Y_BEGIN, Params.Z_BEGIN,
                Params.X_END, Params.STEP, Params.TOLERANCE,
                Params.MIN_STEP, Params.MAX_STEP,
                N, P, R);
    }

    private List<XYChart.Data<Number, Number>> processGraphPoints(List<Point2D> points) {
        double newMaxY = 0, newMinY = chartConfig.getMinY();
        List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();
        for (Point2D point : points) {
            if (point.getY() > newMaxY) newMaxY = point.getY();
            if (point.getY() < newMinY) newMinY = point.getY();
            dataList.add(new XYChart.Data<>(point.getX(), point.getY()));
        }
        System.out.println("newMaxY: " + newMaxY);
        chartConfig.setMaxY(newMaxY);
        System.out.println("newMinY: " + newMinY);
        chartConfig.setMinY(newMinY);
        return dataList;
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
        // Создаем диалоговое окно
        Dialog<List<Double>> dialog = new Dialog<>();
        dialog.setTitle("Введите параметры");

        // Создаем кнопки OK и Cancel
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Создаем поля ввода
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

        // Конвертируем результат в список значений
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
