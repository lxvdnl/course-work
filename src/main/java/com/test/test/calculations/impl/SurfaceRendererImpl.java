package com.test.test.calculations.impl;

import com.test.test.calculations.FuncSurface;
import com.test.test.calculations.SurfaceRenderer;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRendererImpl implements SurfaceRenderer {
    @Override
    public List<Point2D> render(int N, double xBegin, double xEnd, double step) {
        List<Point2D> surfacePoints = new ArrayList<>();

        List<FuncSurface> surfaces = new ArrayList<>();
        surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_1);
        if (N >= 2) surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_2);
        if (N == 3) surfaces.add(FunctionProvider.DEFAULT_SURFACE_FUNCTION_3);

        for (double x = xBegin; x <= xEnd; x += step) {
            double y = -Double.MAX_VALUE;
            for (FuncSurface surface : surfaces) {
                double yVal = surface.compute(x);
                if (yVal > y) y = yVal;
            }

            surfacePoints.add(new Point2D(x, y));
        }

        return surfacePoints;
    }
}
