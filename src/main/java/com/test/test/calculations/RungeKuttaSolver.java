package com.test.test.calculations;

import javafx.geometry.Point2D;

import java.util.List;
import java.util.Map;

public interface RungeKuttaSolver {

    List<Point2D> plotGraph(FuncF f, FuncG g,
                            FuncSurface surface,
                            FuncDerivativeSurface derivativeSurface,
                            double xBegin, double yBegin, double zBegin,
                            double xEnd,
                            double step, double tolerance,
                            double minStep, double maxStep,
                            double R, double E, double Y, double U, double M);

}
