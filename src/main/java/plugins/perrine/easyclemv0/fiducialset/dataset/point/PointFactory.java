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
package plugins.perrine.easyclemv0.fiducialset.dataset.point;

import icy.roi.ROI;
import plugins.perrine.easyclemv0.roi.RoiProcessor;

import javax.inject.Inject;

public class PointFactory {

    private RoiProcessor roiProcessor;

    @Inject
    public PointFactory(RoiProcessor roiProcessor) {
        this.roiProcessor = roiProcessor;
    }

    public Point getFrom(ROI roi) {
        return new Point(roiProcessor.getPointFromRoi(roi));
    }

    public Point getFrom(double ... coordinates) {
        return new Point(coordinates);
    }
}
