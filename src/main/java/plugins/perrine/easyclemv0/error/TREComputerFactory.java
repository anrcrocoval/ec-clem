package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import plugins.perrine.easyclemv0.fiducialset.dataset.DatasetFactory;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchema;
import plugins.perrine.easyclemv0.transformation.schema.TransformationSchemaFactory;
import plugins.perrine.easyclemv0.workspace.Workspace;

import javax.inject.Inject;

public class TREComputerFactory {

    private Mean mean = new Mean();
    private InertiaMatrixComputer inertiaMatrixComputer;
    private FREComputer freComputer;
    private FLEComputer fleComputer;
    private TransformationSchemaFactory transformationSchemaFactory;
    private DatasetFactory datasetFactory;

    @Inject
    public TREComputerFactory(InertiaMatrixComputer inertiaMatrixComputer, FREComputer freComputer, FLEComputer fleComputer, TransformationSchemaFactory transformationSchemaFactory, DatasetFactory datasetFactory) {
        this.inertiaMatrixComputer = inertiaMatrixComputer;
        this.freComputer = freComputer;
        this.fleComputer = fleComputer;
        this.transformationSchemaFactory = transformationSchemaFactory;
        this.datasetFactory = datasetFactory;
    }

    public TREComputer getFrom(TransformationSchema transformationSchema) {
        return getFrom(
            datasetFactory.getFrom(
                transformationSchema.getFiducialSet().getSourceDataset(),
                transformationSchema
            ),
            transformationSchema.getFiducialSet().getTargetDataset()
        );
    }

    public TREComputer getFrom(Workspace workspace) {
        if(workspace.getTransformationSchema() != null) {
            return getFrom(workspace.getTransformationSchema());
        }

        return getFrom(transformationSchemaFactory.getFrom(workspace));
    }

    private TREComputer getFrom(Dataset sourceTransformedDataset, Dataset targetDataset) {
        Matrix barycentre = targetDataset.getBarycentre().getMatrix();
        Dataset clone = targetDataset.clone();
        clone.substractBarycentre();
        Matrix eigenVectors = inertiaMatrixComputer.getInertiaMatrix(clone).eig().getV();
        double[] f = getF(clone, eigenVectors);
        return new TREComputer(
            targetDataset.getN(),
            f,
            eigenVectors,
            barycentre,
            fleComputer.getExpectedSquareFLE(
                freComputer.getExpectedSquareFRE(
                    sourceTransformedDataset,
                    targetDataset
                ), targetDataset.getN()
            )
        );
    }

    public TREComputer getFrom(Dataset dataset, double expectedSquareFLE) {
        Matrix barycentre = dataset.getBarycentre().getMatrix();
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
                        dataset.getBarycentre().getMatrix()
                    )
                );
            }
            f[j] += mean.getResult();
        }
        return f;
    }
}
