package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import Jama.Matrix;
import org.apache.commons.math3.exception.DimensionMismatchException;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodObjectiveFunction;

public class Rigid2DIsotropicMaxLikelihoodObjectiveFunction extends Rigid2DMaxLikelihoodObjectiveFunction {

    public Rigid2DIsotropicMaxLikelihoodObjectiveFunction(FiducialSet fiducialSet) {
        super(fiducialSet);
    }

    @Override
    protected Matrix getSigmaInv(double[] point) {
        return new Matrix(new double[][] {
            { point[3], 0 },
            { 0, point[3] }
        });
    }

    @Override
    protected void checkParameters(double[] point) {
        if(point.length != 4) {
            throw new DimensionMismatchException(point.length,4);
        }
    }
}
