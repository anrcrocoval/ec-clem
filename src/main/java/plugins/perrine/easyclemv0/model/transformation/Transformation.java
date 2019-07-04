package plugins.perrine.easyclemv0.model.transformation;

import plugins.perrine.easyclemv0.model.Dataset;

public interface Transformation {
    Dataset apply(Dataset dataset);
}
