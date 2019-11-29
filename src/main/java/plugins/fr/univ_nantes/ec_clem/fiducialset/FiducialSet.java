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
package plugins.fr.univ_nantes.ec_clem.fiducialset;

import plugins.fr.univ_nantes.ec_clem.fiducialset.dataset.Dataset;
import plugins.fr.univ_nantes.ec_clem.roi.PointType;

public class FiducialSet implements Cloneable {
    private Dataset sourceDataset;
    private Dataset targetDataset;
    private int n;

    public FiducialSet(Dataset sourceDataset, Dataset targetDataset) {
        this.sourceDataset = sourceDataset;
        this.targetDataset = targetDataset;
        n = sourceDataset.getN();
        if(n < 3) {
            throw new RuntimeException("Minimum number of fiducial points is 3");
        }
        if(targetDataset.getPointType() != PointType.FIDUCIAL || sourceDataset.getPointType() != PointType.FIDUCIAL) {
            throw new RuntimeException("Source dataset and target dataset should have FIDUCIAL type");
        }
        if(sourceDataset.getN() != targetDataset.getN()) {
            throw new RuntimeException("Source dataset and target dataset do not have the same number of points");
        }
    }

    @Override
    public FiducialSet clone() {
        FiducialSet clone = null;
        try {
            clone = (FiducialSet) super.clone();
            clone.sourceDataset = sourceDataset.clone();
            clone.targetDataset = targetDataset.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public void remove(int i) {
        sourceDataset.removePoint(i);
        targetDataset.removePoint(i);
        n--;
    }

    public Dataset getSourceDataset() {
        return sourceDataset;
    }

    public Dataset getTargetDataset() {
        return targetDataset;
    }

    public int getN() {
        return n;
    }

    public FiducialSet sort(Integer[] indices) {
        sourceDataset.sort(indices);
        targetDataset.sort(indices);
        return this;
    }
}
