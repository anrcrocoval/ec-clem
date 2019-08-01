package plugins.perrine.easyclemv0.test.storage.xml;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import java.io.File;
import java.io.IOException;

public class XmlFileReader {

    public Document loadFile(File XMLFile) {
        checkFileExists(XMLFile);
        return XMLUtil.loadDocument(XMLFile, true);
    }

    private void checkFileExists(File XMLFile) {
        if(!XMLFile.exists()) {
            try {
                XMLFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Document document = XMLUtil.createDocument(true);
            XmlFileWriter xmlFileWriter = new XmlFileWriter(document, XMLFile);
            xmlFileWriter.write();
        }
    }
}
