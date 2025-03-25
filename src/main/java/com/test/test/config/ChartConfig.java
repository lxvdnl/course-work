package com.test.test.config;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChartConfig {
    private double minX, maxX;
    private double minY, maxY;

    public ChartConfig(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void applyAxisConfig(NumberAxis xAxis, NumberAxis yAxis) {
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound((int) Math.floor(minY));
        yAxis.setUpperBound(Math.floor(maxY) + 1);
    }

    public void applySeriesConfig(XYChart.Series<Number, Number> series, boolean isSurface) {
        if (!isSurface) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Circle point = new Circle(1.5, Color.RED);
                data.setNode(point);
            }
        } else {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Circle point = new Circle(2, Color.ORANGE);
                data.setNode(point);
            }
        }
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinY() {
        return minY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }
}
