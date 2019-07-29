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
package plugins.perrine.easyclemv0.storage;

import icy.util.XMLUtil;
import org.w3c.dom.Document;

import java.io.File;

public class XmlFileWriter {

    private File file;
    private Document document;

    public XmlFileWriter(Document document, File file) {
        this.document = document;
        this.file = file;
    }

    public void write() {
        XMLUtil.saveDocument(document, file);
    }

    public void clear() {

    }

    public Document getDocument() {
        return document;
    }
}
