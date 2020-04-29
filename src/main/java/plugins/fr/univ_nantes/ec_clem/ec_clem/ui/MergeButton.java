package plugins.fr.univ_nantes.ec_clem.ec_clem.ui;

import icy.gui.dialog.MessageDialog;
import icy.gui.viewer.Viewer;
import icy.system.thread.ThreadUtil;
import plugins.fr.univ_nantes.ec_clem.ec_clem.sequence.SequenceMerger;
import plugins.fr.univ_nantes.ec_clem.ec_clem.workspace.Workspace;
import javax.inject.Inject;
import javax.swing.JButton;
import java.util.concurrent.CompletableFuture;

public class MergeButton extends JButton {
    private Workspace workspace;

    @Inject
    public MergeButton() {
        super("Merge sequences");
        addActionListener((arg0) -> action());
    }

    private void action() {
        try {
            SequenceMerger sequenceMerger = new SequenceMerger(workspace.getSourceSequence(), workspace.getTargetSequence());
            CompletableFuture
                .supplyAsync(sequenceMerger)
                .thenAccept(sequence -> ThreadUtil.invokeLater(() -> {
                    new Viewer(sequence);
                }));
        } catch(RuntimeException e) {
            MessageDialog.showDialog(e.getMessage(), MessageDialog.ERROR_MESSAGE);
        }
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
