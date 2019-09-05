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
package plugins.perrine.easyclemv0.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.frame.progress.AnnounceFrame;
import icy.gui.viewer.Viewer;
import icy.image.colormap.FireColorMap;
import icy.system.thread.ThreadUtil;
import plugins.perrine.easyclemv0.error.fitzpatrick.TargetRegistrationErrorMap;
import plugins.perrine.easyclemv0.error.fitzpatrick.TREComputerFactory;
import plugins.perrine.easyclemv0.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.*;

public class ComputeErrorMapButton extends JButton {

	private static final long serialVersionUID = 1L;
	private Workspace workspace;
    private TREComputerFactory treComputerFactory;
    private TargetRegistrationErrorMap targetRegistrationErrorMap;

    @Inject
    public ComputeErrorMapButton(TREComputerFactory treComputerFactory, TargetRegistrationErrorMap targetRegistrationErrorMap) {
        super("Compute the whole predicted error map ");
        this.treComputerFactory = treComputerFactory;
        this.targetRegistrationErrorMap = targetRegistrationErrorMap;
        setToolTipText(" This will compute a new image were each pixel value stands for the statistical registration error (called Target Registration Error");
        addActionListener((arg0) -> action());
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void action() {
        if (workspace.getSourceSequence() != null) {
            if (workspace.getSourceSequence().getROIs().size() < 3) {
                new AnnounceFrame("Without at least 3 ROI points, the error map does not have any meaning. Please add points.",5);
            } else {
                targetRegistrationErrorMap.run(
                    workspace.getTransformationSchema().getTargetSize(),
                    treComputerFactory.getFrom(workspace)
                ).thenAccept(sequence -> ThreadUtil.invokeLater(() -> {
                    Viewer viewer = new Viewer(sequence);
                    viewer.getLut()
                        .getLutChannel(0)
                        .setColorMap(new FireColorMap(), false);
                }));
            }
        } else {
            MessageDialog.showDialog("Source and target were closed. Please open one of them and try again");
        }
    }
}