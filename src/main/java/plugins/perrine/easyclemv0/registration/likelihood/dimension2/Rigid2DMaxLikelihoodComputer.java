package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import plugins.perrine.easyclemv0.registration.TransformationComputer;
import plugins.perrine.easyclemv0.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Rigid2DMaxLikelihoodComputer implements TransformationComputer {

    protected MatrixUtil matrixUtil;

    public Rigid2DMaxLikelihoodComputer() {
        DaggerRigid2DMaxLikelihoodComputerComponent.create().inject(this);
    }

    @Override
    public Similarity compute(FiducialSet fiducialSet) {
        double[] optimize = optimize(fiducialSet);
        Similarity s = new Similarity(
            new Matrix(new double [][] {
                { cos(optimize[2]), -sin(optimize[2]) },
                { sin(optimize[2]), cos(optimize[2]) }
            }),
            new Matrix(new double[][] {
                {  optimize[0] },
                { optimize[1] }
            }),
            Matrix.identity(2,2)
        );

        double lambdaInv11 = (Math.pow(optimize[3], 2) + Math.pow(optimize[5], 2));
        double lambdaInv22 = (Math.pow(optimize[6], 2) + Math.pow(optimize[4], 2));
        double lambdaInv12 = (optimize[3] * optimize[4]) + (optimize[6] * optimize[5]);
        double det = (lambdaInv11 * lambdaInv22) - (lambdaInv12 * lambdaInv12);
        System.out.println(lambdaInv22 / det);
        System.out.println(-lambdaInv12 / det);
        System.out.println(lambdaInv11 / det);
        return s;
    }

    protected abstract double[] optimize(FiducialSet fiducialSet);

    @Inject
    public void setMatrixUtil(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }
}
