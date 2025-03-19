package com.test.test.calculations.impl;

import com.test.test.Params;
import com.test.test.calculations.FuncDerivativeSurface;
import com.test.test.calculations.FuncF;
import com.test.test.calculations.FuncG;
import com.test.test.calculations.FuncSurface;

public class FunctionProvider {

    public static final FuncF DEFAULT_F = z -> z;

    public static final FuncG DEFAULT_G = () -> - Params.P;

    public static final FuncSurface DEFAULT_SURFACE_FUNCTION =
            (x, E, Y, U, M) -> E - M * Y * Math.cos(x - U);

    public static final FuncDerivativeSurface DEFAULT_DERIVATIVE_SURFACE =
            (x, Y, U, M) -> M * Y * Math.sin(x - U);

}
