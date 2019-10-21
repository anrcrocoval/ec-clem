package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import Jama.Matrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
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
        return sigmaInv;
    }

    @Override
    protected void checkParameters(double[] point) {
        if(point.length != 6) {
            throw new DimensionMismatchException(point.length, 6);
        }
    }

    public DerivativeStructure getDerivativeStructure(double[] point) {
        DerivativeStructure tx = new DerivativeStructure(6, 1, 0, point[0]);
        DerivativeStructure ty = new DerivativeStructure(6, 1, 1, point[1]);
        DerivativeStructure theta = new DerivativeStructure(6, 1, 2, point[2]);
        DerivativeStructure lambda_11 = new DerivativeStructure(6, 1, 3, point[3]);
        DerivativeStructure lambda_12 = new DerivativeStructure(6, 1, 4, point[4]);
        DerivativeStructure lambda_22 = new DerivativeStructure(6, 1, 5, point[5]);

//        DerivativeStructure sigmoid_theta = theta.negate().exp().add(1).reciprocal().multiply(2 * Math.PI);
//        DerivativeStructure sigmoid_lambda_11 = theta.negate().exp().add(1).reciprocal().multiply(1000);
//        DerivativeStructure sigmoid_lambda_12 = theta.negate().exp().add(1).reciprocal().multiply(1000);
//        DerivativeStructure sigmoid_lambda_22 = theta.negate().exp().add(1).reciprocal().multiply(1000);

        DerivativeStructure det_lambda = lambda_11.multiply(lambda_22).subtract(lambda_12.pow(2));

        DerivativeStructure sum = null;

        for(int i = 0; i < fiducialSet.getN(); i++) {
            Point y = fiducialSet.getTargetDataset().getPoint(i);
            Point z = fiducialSet.getSourceDataset().getPoint(i);
            DerivativeStructure tmp1 = tx.negate()
                    .add(y.get(0))
                    .subtract(theta.cos().multiply(z.get(0)))
                    .add(theta.sin().multiply(z.get(1)));
            DerivativeStructure tmp2 = ty.negate()
                    .add(y.get(1))
                    .subtract(theta.sin().multiply(z.get(0)))
                    .subtract(theta.cos().multiply(z.get(1)));
            DerivativeStructure sum_i = tmp1.multiply(tmp1.multiply(lambda_11).add(tmp2.multiply(lambda_12)))
                    .add(tmp2.multiply(tmp1.multiply(lambda_12).add(tmp2.multiply(lambda_22))));

            if(sum == null) {
                sum = sum_i;
            } else {
                sum.add(sum_i);
            }
        }


        return det_lambda.divide(2 * Math.PI).sqrt().multiply(fiducialSet.getN()).subtract(sum.divide(2));
    }
}
