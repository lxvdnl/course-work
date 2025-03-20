package com.test.test.calculations.impl;

import com.test.test.Params;
import com.test.test.calculations.*;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RungeKuttaSolverImpl implements RungeKuttaSolver {

    @Override
    public List<Point2D> plotGraph(FuncF f, FuncG g,
                                   Map<FuncSurface, FuncDerivativeSurface> surfacesMap,
                                   double xBegin, double yBegin, double zBegin,
                                   double xEnd,
                                   double step, double tolerance,
                                   double minStep, double maxStep) {

        System.out.println("start runge-kutta");

        List<Point2D> points = new ArrayList<>();

        double currentStep = step;
        double nextY, nextZ;

        while (xBegin <= xEnd) {
            points.add(new Point2D(xBegin, yBegin));

            while (true) {
                double k1_y = currentStep * f.compute(zBegin);
                double k1_z = currentStep * g.compute();
                double k2_y = currentStep * f.compute(zBegin + k1_z / 2);
                double k2_z = currentStep * g.compute();
                double k3_y = currentStep * f.compute(zBegin + k2_z / 2);
                double k3_z = currentStep * g.compute();
                double k4_y = currentStep * f.compute(zBegin + k3_z);
                double k4_z = currentStep * g.compute();

                double tempY = yBegin + (k1_y + 2 * k2_y + 2 * k3_y + k4_y) / 6;
                double tempZ = zBegin + (k1_z + 2 * k2_z + 2 * k3_z + k4_z) / 6;

                double errorY = Math.abs(tempY - yBegin);
                double errorZ = Math.abs(tempZ - zBegin);

                if (errorY <= tolerance && errorZ <= tolerance) {
                    nextY = tempY;
                    nextZ = tempZ;
                    break;
                } else if (currentStep > minStep) {
                    currentStep /= 2.0;
                } else {
                    nextY = tempY;
                    nextZ = tempZ;
                    break;
                }
            }

            for (Map.Entry<FuncSurface, FuncDerivativeSurface> entry : surfacesMap.entrySet()) {
                double surfaceValue = entry.getKey().compute(xBegin);
                double df_dtau = entry.getValue().compute(xBegin);

                if (nextY <= surfaceValue && nextZ - df_dtau < 0) {
                    double t = (surfaceValue - yBegin) / (nextY - yBegin);
                    double xImpact = xBegin + t * currentStep;
                    double zImpact = zBegin + t * (nextZ - zBegin);

                    nextY = surfaceValue;
                    nextZ = -Params.R * zImpact + (1 + Params.R) * df_dtau;

                    points.add(new Point2D(xImpact, surfaceValue));
                }
            }

            xBegin += currentStep;
            yBegin = nextY;
            zBegin = nextZ;

            if (currentStep < maxStep) {
                currentStep *= 2.0;
            }
        }

        System.out.println("end runge-kutta");
        return points;
    }
}
