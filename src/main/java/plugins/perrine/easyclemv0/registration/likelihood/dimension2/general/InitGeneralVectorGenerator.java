package plugins.perrine.easyclemv0.registration.likelihood.dimension2.general;

import Jama.Matrix;
import org.apache.commons.math3.random.*;
import plugins.perrine.easyclemv0.error.CovarianceMatrixComputer;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.matrix.MatrixUtil;
import javax.inject.Inject;

public class InitGeneralVectorGenerator implements RandomVectorGenerator {

    private UncorrelatedRandomVectorGenerator random;
    private MatrixUtil matrixUtil;
    private CovarianceMatrixComputer covarianceMatrixComputer;
    private Matrix sigmaInv;

    public InitGeneralVectorGenerator(FiducialSet fiducialSet) {
        DaggerInitGeneralVectorGeneratorComponent.create().inject(this);
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
        return new double[] {doubles[0], doubles[1], doubles[2], sigmaInv.get(0, 0), sigmaInv.get(0, 1), sigmaInv.get(1,1)};
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
