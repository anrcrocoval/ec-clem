/**
 * Copyright 2010-2018 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
 * Copyright 2019 Guillaume Potier <guillaume.potier@univ-nantes.fr>, INSERM.
 *
 * This file is part of EC-CLEM.
 *
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 **/
package plugins.perrine.easyclemv0.registration;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;
import plugins.perrine.easyclemv0.fiducialset.FiducialSet;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;
import plugins.perrine.easyclemv0.transformation.Similarity;

import javax.inject.Inject;

import static java.lang.Math.max;

public class SimilarityTransformationComputer implements TransformationComputer {

    @Inject
    public SimilarityTransformationComputer() {}

    public Similarity compute(FiducialSet fiducialSet) {
        return compute(fiducialSet.getSourceDataset(), fiducialSet.getTargetDataset());
    }

    private Similarity compute(Dataset source, Dataset target) {
        if (source.getN() < 2) {
            int dimension = max(source.getDimension(), target.getDimension());
            return new Similarity(
                    Matrix.identity(
                            dimension,
                            dimension
                    ),
                    new Matrix(dimension, 1, 0),
                    Matrix.identity(dimension, dimension)
            );
        }

        Dataset clonedSourceDataset = source.clone();
        Dataset clonedTargetDataset = target.clone();
        Point sourceBarycentre = clonedSourceDataset.getBarycentre();
        Point targetBarycentre = clonedTargetDataset.getBarycentre();

        clonedSourceDataset.substractBarycentre();
        clonedTargetDataset.substractBarycentre();

        Matrix R = getR(clonedSourceDataset, clonedTargetDataset);
        Matrix S = getS(clonedSourceDataset.getMatrix(), clonedTargetDataset.getMatrix(), R);
        Matrix T = getT(sourceBarycentre.getMatrix(), targetBarycentre.getMatrix(), R, S);
        return new Similarity(R, T, S);
    }

    protected Matrix getS(Matrix sourceDataset, Matrix targetDataset, Matrix R) {
        Dataset ratioDataset = new Dataset(targetDataset.arrayRightDivide(R.times(sourceDataset.transpose()).transpose()));
        Point ratioPoint = ratioDataset.getBarycentre();
        Matrix scale = Matrix.identity(R.getRowDimension(), R.getColumnDimension());
        for(int i = 0; i < R.getRowDimension(); i++) {
            if(Double.isNaN(ratioPoint.get(i)) || ratioPoint.get(i) == 0) {
                scale.set(i, i, 1);
            } else {
                scale.set(i, i, ratioPoint.get(i));
            }
        }
        return scale;
    }

    private Matrix getR(Dataset source, Dataset target) {
        Matrix S = target.getMatrix().transpose().times(source.getMatrix());
        SingularValueDecomposition svd = S.svd();
        Matrix E = Matrix.identity(svd.getS().getRowDimension(), svd.getS().getColumnDimension());
        E.set(E.getRowDimension() - 1, E.getColumnDimension() - 1, Math.signum(svd.getU().times(svd.getV().transpose()).det()));
        return svd.getU().times(E).times(svd.getV().transpose());
    }

    private Matrix getT(Matrix sourceBarycentre, Matrix targetBarycentre, Matrix R, Matrix scale) {
        return targetBarycentre.minus(R.times(scale).times(sourceBarycentre));
    }
}
