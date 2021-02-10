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
package plugins.perrine.ec_clem.ec_clem.storage.transformation_schema.reader;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import javax.inject.Inject;
import java.io.File;

public class XmlFileReader {

    @Inject
    public XmlFileReader() {}

    public Document loadFile(File XMLFile) {
        return XMLUtil.loadDocument(XMLFile, true);
    }
}
