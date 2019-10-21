package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import Jama.Matrix;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.exception.DimensionMismatchException;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
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

    public DerivativeStructure getDerivativeStructure(double[] point) {
        DerivativeStructure tx = new DerivativeStructure(6, 1, 0, point[0]);
        DerivativeStructure ty = new DerivativeStructure(6, 1, 1, point[1]);
        DerivativeStructure theta = new DerivativeStructure(6, 1, 2, point[2]);
        DerivativeStructure lambda11 = new DerivativeStructure(6, 1, 3, point[3]);

        DerivativeStructure sigmoid_theta = theta.negate().exp().add(1).reciprocal().multiply(2 * Math.PI);
        DerivativeStructure exp_lambda11 = lambda11.abs().add(0.00000001);

        DerivativeStructure sum = null;

        for(int i = 0; i < fiducialSet.getN(); i++) {
            Point y = fiducialSet.getTargetDataset().getPoint(i);
            Point z = fiducialSet.getSourceDataset().getPoint(i);
            DerivativeStructure tmp1 = tx.negate()
                    .add(y.get(0))
                    .subtract(sigmoid_theta.cos().multiply(z.get(0)))
                    .add(sigmoid_theta.sin().multiply(z.get(1)));
            DerivativeStructure tmp2 = ty.negate()
                    .add(y.get(1))
                    .subtract(sigmoid_theta.sin().multiply(z.get(0)))
                    .subtract(sigmoid_theta.cos().multiply(z.get(1)));
            DerivativeStructure sum_i = tmp1.multiply(tmp1.multiply(exp_lambda11))
                    .add(tmp2.multiply(tmp2.multiply(exp_lambda11)));

            if(sum == null) {
                sum = sum_i;
            } else {
                sum.add(sum_i);
            }
        }

        return sum;
    }

    @Override
    protected void checkParameters(double[] point) {
        if(point.length != 4) {
            throw new DimensionMismatchException(point.length,4);
        }
    }
}
