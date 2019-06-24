package plugins.perrine.easyclemv0.storage.xml;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.model.SequenceSize;
import plugins.perrine.easyclemv0.model.Transformation;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.transformationDateAttributeName;
import static plugins.perrine.easyclemv0.storage.xml.XmlTransformation.transformationElementName;

public class XmlTransformationElementListReader {

    private SequenceSizeXmlReader sequenceSizeXmlReader = new SequenceSizeXmlReader();
    private XmlTransformationReader xmlTransformationReader = new XmlTransformationReader();

    public Element getLastTransformationElement(Document document) {
        List<Element> list = read(document);
        sortTransformationElementsByDate(list);
        return list.get(list.size() - 1);
    }

    public List<Transformation> getTransformationList(Document document) {
        List<Transformation> transformationList = new ArrayList<>();
        List<Element> elementList = read(document);
        sortTransformationElementsByDate(elementList);
        SequenceSize sequenceSize = sequenceSizeXmlReader.readSize(document);
        for(Element element : elementList) {
            transformationList.add(xmlTransformationReader.read(element, sequenceSize));
        }
        return transformationList;
    }

    private List<Element> read(Document document) {
        return XMLUtil.getElements(document.getDocumentElement(), transformationElementName);
    }

    private void sortTransformationElementsByDate(List<Element> list) {
        list.sort(Comparator.comparing(o -> ZonedDateTime.parse(o.getAttribute(transformationDateAttributeName))));
    }
}
