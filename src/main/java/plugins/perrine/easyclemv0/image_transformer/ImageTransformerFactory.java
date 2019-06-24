package plugins.perrine.easyclemv0.image_transformer;

import plugins.perrine.easyclemv0.model.TransformationType;

public class ImageTransformerFactory {

    public ImageTransformerInterface getFrom(TransformationType transformationType, int dimension) {
        switch (transformationType) {
            case RIGID: return createRigidImageTransformer(dimension);
            case NON_RIGID: return new NonRigidTranformationVTK();
            default: return null;
        }
    }

    private ImageTransformerInterface createRigidImageTransformer(int dimension) {
        switch (dimension) {
            case 2 : return new ImageTransformer();
            case 3 : return new Stack3DVTKTransformer();
            default: return null;
        }
    }
}
