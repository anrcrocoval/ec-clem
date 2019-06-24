package plugins.perrine.easyclemv0.model;

import Jama.Matrix;

public class DatasetTransformer {

    public Dataset apply(Dataset dataset, Matrix transformation) {
        Matrix M = dataset.getMatrix().times(transformation);
        return new Dataset(M.getArray());
    }

    public Dataset apply(Dataset dataset, Similarity similarity) {
        return similarity.apply(dataset);
    }

    public Dataset apply(Dataset dataset, AffineTransformation affineTransformation) {
        return affineTransformation.apply(dataset);
    }
}
