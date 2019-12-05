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

/**
 * Author: Perrine.Paul-Gilloteaux@curie.fr
 * last set of button: dealing with error prediction. 
 * this will read the checkbox for fiducial localisation error and for predicted FRE,
 * as well as launching the computation of the error map.
 */
package plugins.fr.univ_nantes.ec_clem.ui;

import javax.inject.Inject;
import javax.swing.JPanel;
import plugins.fr.univ_nantes.ec_clem.ui.overlay.ErrorInPositionOverlay;
import plugins.fr.univ_nantes.ec_clem.ui.overlay.PredictedErrorInPositionOverlay;
import plugins.fr.univ_nantes.ec_clem.workspace.Workspace;

public class GuiCLEMButtons2 extends JPanel {

	private ComputeErrorMapButton computeErrorMapButton;
	private MonitorTargetPointButton monitorTargetPointButton;

	private ShowOverlayCheckbox showerror;
	private ShowOverlayCheckbox showpredictederror;
	private ErrorInPositionOverlay errorInPositionOverlay;
	private PredictedErrorInPositionOverlay predictedErrorInPositionOverlay;

	@Inject
	public GuiCLEMButtons2(
			ComputeErrorMapButton computeErrorMapButton,
			MonitorTargetPointButton monitorTargetPointButton,
			ErrorInPositionOverlay errorInPositionOverlay,
			PredictedErrorInPositionOverlay predictedErrorInPositionOverlay
	) {
		this.computeErrorMapButton = computeErrorMapButton;
		this.monitorTargetPointButton = monitorTargetPointButton;
		this.errorInPositionOverlay = errorInPositionOverlay;
		this.predictedErrorInPositionOverlay = predictedErrorInPositionOverlay;
		showerror = new ShowOverlayCheckbox(
			null,
			"Show Difference in Positions",
			"This will draw around each point on source image a red circle which radius is the difference between source point and target point positions (called Fiducial registration error)"
		);
		showpredictederror = new ShowOverlayCheckbox(
			null,
			"Show Predicted Error in Positions ",
			"This will draw around each point on source image an orange circle which radius is the predicted error from the actual point configuration (called Target registration error)"
		);

		add(showerror);
		add(showpredictederror);
		add(computeErrorMapButton);
		add(monitorTargetPointButton);
	}

	public void setWorkspace(Workspace workspace) {
		computeErrorMapButton.setWorkspace(workspace);
		monitorTargetPointButton.setWorkspace(workspace);
		showerror.setWorkspace(workspace);
		showerror.setOverlay(errorInPositionOverlay.setWorkspace(workspace));
		showpredictederror.setWorkspace(workspace);
		showpredictederror.setOverlay(predictedErrorInPositionOverlay.setWorkspace(workspace));
	}

	public void disableButtons() {
		computeErrorMapButton.setEnabled(false);
		monitorTargetPointButton.setEnabled(false);
		showerror.setEnabled(false);
		showpredictederror.setEnabled(false);
	}

	public void enableButtons() {
		computeErrorMapButton.setEnabled(true);
		monitorTargetPointButton.setEnabled(true);
		showerror.setEnabled(true);
		showpredictederror.setEnabled(true);
	}
}