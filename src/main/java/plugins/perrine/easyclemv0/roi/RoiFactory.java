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
package plugins.perrine.easyclemv0.roi;

import icy.roi.ROI;
import icy.type.point.Point5D;
import plugins.kernel.roi.roi2d.ROI2DPoint;
import plugins.kernel.roi.roi3d.ROI3DPoint;
import plugins.perrine.easyclemv0.fiducialset.dataset.point.Point;

import javax.inject.Inject;

public class RoiFactory {

    @Inject
    public RoiFactory() {}

    public ROI getFrom(Point point) {
        ROI roi;
        switch (point.getDimension()) {
            case 2: roi = new ROI2DPoint();
                          break;
            case 3: roi = new ROI3DPoint();
                          break;
            default: throw new RuntimeException("Unsupported dimension : " + point.getDimension());
        }
        Point5D position = roi.getPosition5D();
        for(int i = 0; i < point.getDimension(); i++) {
            if(i == 0) {
                position.setX(point.get(i));
            }
            if(i == 1) {
                position.setY(point.get(i));
            }
            if(i == 2) {
                position.setZ(point.get(i));
            }
        }
        roi.setPosition5D(position);
        return roi;
    }
}
