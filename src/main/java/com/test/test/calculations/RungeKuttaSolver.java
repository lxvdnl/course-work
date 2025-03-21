package com.test.test.calculations;

import javafx.geometry.Point2D;

import java.util.List;
import java.util.Map;

public interface RungeKuttaSolver {

    List<Point2D> plotGraph(FuncF f, FuncG g,
                            double xBegin, double yBegin, double zBegin,
                            double xEnd,
                            double step, double tolerance,
                            double minStep, double maxStep,
                            int N, double P, double R);

}
