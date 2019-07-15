package plugins.perrine.easyclemv0.error;

import Jama.Matrix;
import plugins.perrine.easyclemv0.factory.TransformationFactory;
import plugins.perrine.easyclemv0.model.Dataset;
import plugins.perrine.easyclemv0.model.TransformationSchema;
import plugins.perrine.easyclemv0.model.transformation.AffineTransformation;
import plugins.perrine.easyclemv0.model.transformation.Similarity;
import plugins.perrine.easyclemv0.model.transformation.SplineTransformation;
import plugins.perrine.easyclemv0.model.transformation.Transformation;

public class ErrorComputer {

    private TransformationFactory transformationFactory = new TransformationFactory();

    public void showError(TransformationSchema transformationSchema) {
        Transformation from = transformationFactory.getFrom(transformationSchema);
        if(from instanceof SplineTransformation) {
            throw new RuntimeException("Error estimation for SplineTransformation is not supported");
        }

        transformationSchema.getFiducialSet().getTargetDataset().getMatrix().print(1,5);
        transformationSchema.getFiducialSet().getSourceDataset().getMatrix().print(1,5);


        Similarity transformation = (Similarity) from;

        transformation.getR().print(1,5);
        transformation.getS().print(1,5);
        transformation.getT().print(1,5);

        //transformationSchema.getFiducialSet().getSourceDataset().getHomogeneousMatrixLeft().times(transformation.getMatrixLeft().transpose()).print(1,5);
        Matrix minus = transformationSchema.getFiducialSet().getTargetDataset().getMatrix().minus(
                transformationSchema.getFiducialSet().getSourceDataset().getHomogeneousMatrixRight().times(transformation.getMatrixRight().transpose())
        );
        minus.print(1,5);
        new Dataset(minus).getBarycentre().getMatrix().transpose().print(1,5);
    }
}
