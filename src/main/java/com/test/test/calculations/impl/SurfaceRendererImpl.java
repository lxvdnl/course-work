package com.test.test.calculations.impl;

import com.test.test.calculations.FuncSurface;
import com.test.test.calculations.SurfaceRenderer;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRendererImpl implements SurfaceRenderer {
    @Override
    public List<Point2D> render(FuncSurface surface, double xBegin, double xEnd, double step) {
        List<Point2D> surfacePoints = new ArrayList<>();
        for (double x = xBegin; x <= xEnd; x += step) {
            double y = surface.compute(x);
            surfacePoints.add(new Point2D(x, y));
        }

        return surfacePoints;
    }
}
