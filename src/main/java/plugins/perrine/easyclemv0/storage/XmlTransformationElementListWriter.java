package plugins.perrine.easyclemv0.storage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlTransformationElementListWriter {

    private XmlTransformationElementListReader xmlTransformationElementListReader = new XmlTransformationElementListReader();

    public void removeLastTransformationElement(Document document) {
        Element lastTransformationElement = xmlTransformationElementListReader.getLastTransformationElement(document);
        lastTransformationElement.getParentNode().removeChild(lastTransformationElement);
    }
}
