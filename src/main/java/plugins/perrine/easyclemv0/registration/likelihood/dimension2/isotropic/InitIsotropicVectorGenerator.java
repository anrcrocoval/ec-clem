package plugins.perrine.easyclemv0.registration.likelihood.dimension2.isotropic;

import Jama.Matrix;
import org.apache.commons.math3.random.*;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;

import javax.inject.Inject;

public class InitIsotropicVectorGenerator implements RandomVectorGenerator {

    private UncorrelatedRandomVectorGenerator random;
    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private Matrix sigmaInv;

    public InitIsotropicVectorGenerator(FiducialSet fiducialSet) {
        DaggerInitIsotropicVectorGeneratorComponent.create().inject(this);
        random = new UncorrelatedRandomVectorGenerator(3, new UniformRandomGenerator(
            new SynchronizedRandomGenerator(
                new JDKRandomGenerator()
            )
        ));
        sigmaInv = matrixUtil.pseudoInverse(
            covarianceMatrixComputer.compute(
                fiducialSet.getTargetDataset().getMatrix()
            )
        );
    }

    @Override
    public double[] nextVector() {
        double[] doubles = random.nextVector();
        return new double[] {doubles[0], doubles[1], doubles[2], (sigmaInv.get(0, 0) + sigmaInv.get(1, 1)) / 2};
    }

    @Inject
    public void setMatrixUtil(MatrixUtil matrixUtil) {
        this.matrixUtil = matrixUtil;
    }

    @Inject
    public void setCovarianceMatrixComputer(CovarianceMatrixComputer covarianceMatrixComputer) {
        this.covarianceMatrixComputer = covarianceMatrixComputer;
    }
}
