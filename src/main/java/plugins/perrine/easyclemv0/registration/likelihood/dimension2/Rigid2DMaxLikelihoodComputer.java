package plugins.perrine.easyclemv0.registration.likelihood.dimension2;

import Jama.Matrix;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.registration.TransformationComputer;
import plugins.perrine.easyclemv0.transformation.Similarity;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Rigid2DMaxLikelihoodComputer implements TransformationComputer {

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
        return s;
    }

    protected abstract double[] optimize(FiducialSet fiducialSet);
}
