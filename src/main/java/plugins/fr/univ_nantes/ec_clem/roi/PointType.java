package plugins.fr.univ_nantes.ec_clem.roi;

import java.awt.*;

public enum PointType {
    FIDUCIAL, NOT_FIDUCIAL, ERROR;

    public Color getColor() {
        switch (this) {
            case NOT_FIDUCIAL: return Color.LIGHT_GRAY;
            case FIDUCIAL: return Color.ORANGE;
            case ERROR: return Color.RED;
            default: throw new RuntimeException("Point type not supported");
        }
    }
}
