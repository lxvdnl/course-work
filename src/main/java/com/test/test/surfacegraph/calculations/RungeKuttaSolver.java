package com.test.test.surfacegraph.calculations;

import javafx.geometry.Point2D;

import java.util.List;

public interface RungeKuttaSolver {

    List<Point2D> plotGraph(FuncF f, FuncG g,
                            double xBegin, double yBegin, double zBegin,
                            double xEnd,
                            double step, double tolerance,
                            double minStep, double maxStep,
                            int N, double P, double R);

}
