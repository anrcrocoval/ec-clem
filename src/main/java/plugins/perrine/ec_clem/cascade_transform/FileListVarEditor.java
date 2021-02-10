package plugins.fr.univ_nantes.ec_clem.cascade_transform;

import plugins.adufour.vars.gui.swing.SwingVarEditor;
import plugins.adufour.vars.lang.Var;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class FileListVarEditor extends SwingVarEditor<List<File>> {

    public FileListVarEditor(Var<List<File>> variable) {
        super(variable);
    }

    @Override
    protected JComponent createEditorComponent() {
        return new FileList(getVariable().getValue());
    }

    @Override
    protected void activateListeners() {

    }

    @Override
    protected void deactivateListeners() {

    }

    @Override
    protected void updateInterfaceValue() {

    }
}
