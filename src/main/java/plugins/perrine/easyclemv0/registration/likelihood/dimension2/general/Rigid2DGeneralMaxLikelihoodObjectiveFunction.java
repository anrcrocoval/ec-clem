package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import Jama.Matrix;
import org.apache.commons.math3.exception.DimensionMismatchException;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.registration.likelihood.dimension2.Rigid2DMaxLikelihoodObjectiveFunction;

import javax.inject.Inject;

public class Rigid2DGeneralMaxLikelihoodObjectiveFunction extends Rigid2DMaxLikelihoodObjectiveFunction {

    private MatrixUtil matrixUtil;

    public Rigid2DGeneralMaxLikelihoodObjectiveFunction(FiducialSet fiducialSet) {
        super(fiducialSet);
//        DaggerRigid2DGeneralLikelihoodObjectiveFunctionComponent.create().inject(this);
    }

    @Override
    protected Matrix getSigmaInv(double[] point) {
        return new Matrix(new double[][] {
            { point[3], point[4] },
            { point[4], point[5] }
        });
    }

    @Override
    protected void checkParameters(double[] point) {
        if(point.length != 6) {
            throw new DimensionMismatchException(point.length, 6);
        }
    }
}
