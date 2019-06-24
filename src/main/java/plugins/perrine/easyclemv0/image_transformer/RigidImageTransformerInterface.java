package plugins.perrine.easyclemv0.image_transformer;

import Jama.Matrix;

public interface RigidImageTransformerInterface extends ImageTransformerInterface {
    void setParameters(Matrix M);
}
