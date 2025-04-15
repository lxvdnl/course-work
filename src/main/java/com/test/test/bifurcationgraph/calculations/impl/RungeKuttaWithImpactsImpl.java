package com.test.test.bifurcationgraph.calculations.impl;

import com.test.test.config.Params;
import com.test.test.surfacegraph.calculations.FuncDerivativeSurface;
import com.test.test.surfacegraph.calculations.FuncF;
import com.test.test.surfacegraph.calculations.FuncG;
import com.test.test.surfacegraph.calculations.FuncSurface;
import com.test.test.surfacegraph.calculations.impl.FunctionProvider;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RungeKuttaWithImpactsImpl implements RungeKuttaWithImpacts {
    @Override
    public List<List<Point2D>> plotGraph(FuncF f, FuncG g,
                                         double xBegin, double yBegin, double zBegin,
                                         double xEnd, double step,
                                         double tolerance, double minStep, double maxStep,
                                         int N, double P, double R) {

        Map<FuncSurface, FuncDerivativeSurface> surfacesMap = new HashMap<>();
        surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_1,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_1);
        if (N >= 2) surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_2,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_2);
        if (N == 3) surfacesMap.put(FunctionProvider.DEFAULT_SURFACE_FUNCTION_3,
                FunctionProvider.DEFAULT_DERIVATIVE_SURFACE_3);

        List<List<Point2D>> postImpactSpeedsPerSurface = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            postImpactSpeedsPerSurface.add(new ArrayList<>());
        }

        int impactsCount = 0;
        double currentStep = step;
        double nextY, nextZ;

        while (impactsCount < Params.SKIP_IMPACTS_NUM + Params.IMPACTS_NUM) {
            int impactIndex = -1;
            while (true) {
                double k1_y = currentStep * f.compute(zBegin);
                double k1_z = currentStep * g.compute(P);
                double k2_y = currentStep * f.compute(zBegin + k1_z / 2);
                double k2_z = currentStep * g.compute(P);
                double k3_y = currentStep * f.compute(zBegin + k2_z / 2);
                double k3_z = currentStep * g.compute(P);
                double k4_y = currentStep * f.compute(zBegin + k3_z);
                double k4_z = currentStep * g.compute(P);

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

            int tmpIndex = 0;
            for (Map.Entry<FuncSurface, FuncDerivativeSurface> entry : surfacesMap.entrySet()) {
                double surfaceValue = entry.getKey().compute(xBegin);
                double df_dtau = entry.getValue().compute(xBegin);

                if (nextY <= surfaceValue && nextZ - df_dtau < 0) {
                    double t = (surfaceValue - yBegin) / (nextY - yBegin);
                    double zImpact = zBegin + t * (nextZ - zBegin);

                    nextY = surfaceValue;
                    nextZ = -R * zImpact + (1 + R) * df_dtau;

                    impactIndex = tmpIndex;
                }
                tmpIndex++;
            }

            xBegin += currentStep;
            yBegin = nextY;
            zBegin = nextZ;

            if (impactIndex == 0) {
                postImpactSpeedsPerSurface.get(0).add(new Point2D(P, nextZ));
                if (postImpactSpeedsPerSurface.get(0).size() > 200) {
                    postImpactSpeedsPerSurface.get(0).removeFirst();
                }
                impactsCount++;
            } else if (impactIndex == 1) {
                postImpactSpeedsPerSurface.get(1).add(new Point2D(P, nextZ));
                if (postImpactSpeedsPerSurface.get(1).size() > 200) {
                    postImpactSpeedsPerSurface.get(1).removeFirst();
                }
                impactsCount++;
            } else if (impactIndex == 2) {
                postImpactSpeedsPerSurface.get(2).add(new Point2D(P, nextZ));
                if (postImpactSpeedsPerSurface.get(2).size() > 200) {
                    postImpactSpeedsPerSurface.get(2).removeFirst();
                }
                impactsCount++;
            }
            if (currentStep < maxStep) {
                currentStep *= 2.0;
            }
        }

        return postImpactSpeedsPerSurface;
    }
}
