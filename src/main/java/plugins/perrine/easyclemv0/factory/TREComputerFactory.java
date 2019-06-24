package plugins.perrine.easyclemv0.factory;

import Jama.Matrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.perrine.easyclemv0.error.FLEComputer;
import plugins.perrine.easyclemv0.error.FREComputer;
import plugins.perrine.easyclemv0.error.TREComputer;
import plugins.perrine.easyclemv0.inertia.InertiaMatrixComputer;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.FiducialSet;

public class TREComputerFactory {

    private Mean mean = new Mean();
    private InertiaMatrixComputer inertiaMatrixComputer = new InertiaMatrixComputer();
    private FREComputer freComputer = new FREComputer();
    private FLEComputer fleComputer = new FLEComputer();

    public TREComputer getFrom(FiducialSet fiducialSet) {
        return getFrom(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    public TREComputer getFrom(Dataset sourceDataset, Dataset targetDataset) {
        Matrix barycentre = sourceDataset.getBarycentre().getmatrix();
        Matrix eigenVectors = inertiaMatrixComputer.getInertiaMatrix(sourceDataset).eig().getV();
        double[] f = getF(sourceDataset, eigenVectors);
        return new TREComputer(
            sourceDataset.getN(),
            f,
            eigenVectors,
            barycentre,
            fleComputer.getExpectedSquareFLE(
                freComputer.getExpectedSquareFRE(
                    sourceDataset,
                    targetDataset
                ), sourceDataset.getN()
            )
        );
    }

    public TREComputer getFrom(Dataset dataset, double expectedSquareFLE) {
        Matrix barycentre = dataset.getBarycentre().getmatrix();
        Matrix eigenVectors = inertiaMatrixComputer.getInertiaMatrix(dataset).eig().getV();
        double[] f = getF(dataset, eigenVectors);
        return new TREComputer(
            dataset.getN(),
            f,
            eigenVectors,
            barycentre,
            expectedSquareFLE
        );
    }

    private double[] getF(Dataset dataset, Matrix eigenVectors) {
        double[] f = new double[dataset.getDimension()];

        for(int j = 0; j < dataset.getDimension(); j++) {
            mean.clear();
            for (int i = 0; i < dataset.getN(); i++) {
                mean.increment(
                    dataset.getPoint(i).getSquareDistance(
                        eigenVectors.getMatrix(
                            0, eigenVectors.getRowDimension() - 1, j, j
                        ),
                        dataset.getBarycentre().getmatrix()
                    )
                );
            }
            f[j] += mean.getResult();
        }
        return f;
    }
}
