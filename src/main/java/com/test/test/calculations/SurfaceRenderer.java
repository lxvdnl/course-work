package com.test.test.calculations;

import javafx.geometry.Point2D;

import java.util.List;

@FunctionalInterface
public interface SurfaceRenderer {

    List<Point2D> render(List<FuncSurface> surfaces, double xBegin, double xEnd, double step);

}
