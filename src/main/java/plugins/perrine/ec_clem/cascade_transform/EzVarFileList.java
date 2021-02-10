package plugins.perrine.ec_clem.cascade_transform;

import plugins.adufour.ezplug.EzVar;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class EzVarFileList extends EzVar<List<File>> {

    public EzVarFileList(String name) {
        super(new VarFileList(name, new LinkedList<>()), null);
    }
}
