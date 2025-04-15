package com.test.test.bifurcationgraph.calculations.impl;

import com.test.test.surfacegraph.calculations.FuncF;
import com.test.test.surfacegraph.calculations.FuncG;
import javafx.geometry.Point2D;

import java.util.List;

public interface RungeKuttaWithImpacts {

    List<List<Point2D>> plotGraph(FuncF f, FuncG g,
                            double xBegin, double yBegin, double zBegin,
                            double xEnd,
                            double step, double tolerance,
                            double minStep, double maxStep,
                            int N, double P, double R);

}
