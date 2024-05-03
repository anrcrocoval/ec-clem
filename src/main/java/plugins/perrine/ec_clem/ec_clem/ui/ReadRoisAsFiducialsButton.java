/**
 * Copyright 2024 Perrine Paul-Gilloteaux <Perrine.Paul-Gilloteaux@univ-nantes.fr>, CNRS.
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
package plugins.perrine.ec_clem.ec_clem.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;

import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.Dataset;
import plugins.perrine.ec_clem.ec_clem.fiducialset.dataset.DatasetFactory;
import plugins.perrine.ec_clem.ec_clem.roi.PointType;
import plugins.perrine.ec_clem.ec_clem.roi.RoiUpdater;
import plugins.perrine.ec_clem.ec_clem.transformation.schema.TransformationSchema;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class ReadRoisAsFiducialsButton extends JButton {

	private Workspace workspace;
	private RoiUpdater roiUpdater;
	private DatasetFactory datasetFactory;
    @Inject
    public ReadRoisAsFiducialsButton() {
        super("Read prexisting ROIs as FIDUCIALS");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        if (workspace.getSourceSequence() == null || workspace.getTargetSequence() == null) {
            MessageDialog.showDialog("Make sure source and target image are openned and selected");
            return;
        }
        int nsource= transformRoisAsLandmarks(workspace.getSourceSequence());
        int ntarget= transformRoisAsLandmarks(workspace.getTargetSequence());
        if (nsource!=ntarget) {
        	System.err.println("The number of added fiducials is different: "+nsource+" for source sequence, and "+ntarget+" for target sequence");
        }
        if (nsource==0) {
        	new AnnounceFrame("No points were added: they needs unique and matching names such as Point1, Point2 etc...", 5);
        } else {
        	new AnnounceFrame("All prexisting ROIs ("+nsource+") have been transformed to fiducials points, \n clear all if it was not what you intend to do. \n Check the Console for any unexpected behavior ", 5);
        } 
        }

    private int transformRoisAsLandmarks(Sequence sequence) {
        saveROI(sequence);
        
        
        Dataset readRois = datasetFactory.getFrom(
                sequence    
            );
            
            
            roiUpdater.updateRoi(readRois, sequence);
        return readRois.getN();
    }

    private void saveROI(Sequence sequence) {
        final List<ROI> rois = sequence.getROIs();
        if (rois.size() > 0) {
            final Document doc = XMLUtil.createDocument(true);
            if (doc != null) {
                ROI.saveROIsToXML(XMLUtil.getRootElement(doc), rois);
                XMLUtil.saveDocument(doc, sequence.getFilename() + "_ROIsavedBeforeClearLandmarks.xml");
            }
        }
    }
    @Inject
    public void setDatasetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    @Inject
    public void setRoiUpdater(RoiUpdater roiUpdater) {
        this.roiUpdater = roiUpdater;
    }
}
