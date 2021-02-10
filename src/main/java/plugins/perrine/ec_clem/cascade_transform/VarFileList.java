package plugins.perrine.ec_clem.cascade_transform;

import plugins.adufour.vars.gui.VarEditor;
import plugins.adufour.vars.lang.Var;
import java.io.File;
import java.util.List;

public class VarFileList extends Var<List<File>> {

    public VarFileList(String name, List<File> defaultValue) throws NullPointerException {
        super(name, defaultValue);
    }

    public VarEditor<List<File>> createVarEditor() {
        return new FileListVarEditor(this);
    }
}
