package plugins.perrine.easyclemv0.transformation;

import plugins.perrine.easyclemv0.fiducialset.dataset.Dataset;

public interface Transformation {
    Dataset apply(Dataset dataset);
}
