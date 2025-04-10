package com.test.test.config;

public class Params {

    public static final int N = 1;

    public static final double P = 1.5;
    public static final double R = 0.4;
    public static final double M = 0.1;

    public static final double Y1 = 4;
    public static final double U1 = Math.PI;
    public static final double E1 = 0.02;

    public static final double Y2 = 3;
    public static final double U2 = Math.PI * 2;
    public static final double E2 = 0.018;

    public static final double Y3 = 3;
    public static final double U3 = Math.PI / 2;
    public static final double E3 = 0.019;

    public static final double STEP = 0.0004;
    public static final double MIN_X = 0;
    public static final double MIN_Y = 0;
    public static final double MAX_Y = 4;

    public static final double X_BEGIN = 0;
    public static final double Y_BEGIN = 0.5;
    public static final double Z_BEGIN = 0.5;

    public static final double X_END = 50 + STEP;

    public static final double TOLERANCE = 1e-6;
    public static final double MIN_STEP = 0.001;
    public static final double MAX_STEP = 1.0;

    public static final int IMPACTS_NUM = 100;
    public static final int SKIP_IMPACTS_NUM = 200;
    public static final double BIFURCATION_P_BEGIN = 0.196;
    public static final double BIFURCATION_P_END = 0.218;
    public static final double BIFURCATION_P_STEP = 0.001;

}
