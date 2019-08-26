package plugins.perrine.easyclemv0.storage.xml;

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
