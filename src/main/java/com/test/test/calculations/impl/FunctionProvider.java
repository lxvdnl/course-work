package com.test.test.calculations.impl;

import com.test.test.Params;
import com.test.test.calculations.FuncDerivativeSurface;
import com.test.test.calculations.FuncF;
import com.test.test.calculations.FuncG;
import com.test.test.calculations.FuncSurface;

public class FunctionProvider {

    public static final FuncF DEFAULT_F = z -> z;

    public static final FuncG DEFAULT_G = (P) -> -P;

    public static final FuncSurface DEFAULT_SURFACE_FUNCTION_1 =
            (x) -> Params.E1 - Params.M * Params.Y1 * Math.cos(x - Params.U1);

    public static final FuncDerivativeSurface DEFAULT_DERIVATIVE_SURFACE_1 =
            (x) -> Params.M * Params.Y1 * Math.sin(x - Params.U1);

    public static final FuncSurface DEFAULT_SURFACE_FUNCTION_2 =
            (x) -> Params.E2 - Params.M * Params.Y2 * Math.cos(x - Params.U2);

    public static final FuncDerivativeSurface DEFAULT_DERIVATIVE_SURFACE_2 =
            (x) -> Params.M * Params.Y2 * Math.sin(x - Params.U2);

    public static final FuncSurface DEFAULT_SURFACE_FUNCTION_3 =
            (x) -> Params.E3 - Params.M * Params.Y3 * Math.cos(x - Params.U3);

    public static final FuncDerivativeSurface DEFAULT_DERIVATIVE_SURFACE_3 =
            (x) -> Params.M * Params.Y3 * Math.sin(x - Params.U3);

}
