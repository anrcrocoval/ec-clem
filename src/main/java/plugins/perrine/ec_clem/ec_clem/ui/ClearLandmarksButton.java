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
package plugins.perrine.ec_clem.ec_clem.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.roi.ROI;
import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import plugins.perrine.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;
import java.util.List;

public class ClearLandmarksButton extends JButton {

	private Workspace workspace;

    @Inject
    public ClearLandmarksButton() {
        super("Clear all landmarks points");
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
        clearLandmarks(workspace.getSourceSequence());
        clearLandmarks(workspace.getTargetSequence());
        new AnnounceFrame("All fiducial points have been cleared", 5);
    }

    private void clearLandmarks(Sequence sequence) {
        saveROI(sequence);
        sequence.removeAllROI(true);
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
}
