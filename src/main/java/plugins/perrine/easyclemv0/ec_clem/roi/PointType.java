package plugins.perrine.easyclemv0.ec_clem.roi;

import java.awt.*;

public enum PointType {
    FIDUCIAL, NOT_FIDUCIAL, PREDICTED_ERROR, MEASURED_ERROR;

    public Color getColor() {
        switch (this) {
            case NOT_FIDUCIAL: return Color.LIGHT_GRAY;
            case FIDUCIAL: return Color.ORANGE;
            case PREDICTED_ERROR: return Color.RED;
            case MEASURED_ERROR: return Color.RED;
            default: throw new RuntimeException("Point type not supported");
        }
    }
}
