package plugins.perrine.easyclemv0.storage.xml;

import icy.sequence.Sequence;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.factory.SequenceSizeFactory;
import plugins.perrine.easyclemv0.model.DimensionSize;
import plugins.perrine.easyclemv0.model.SequenceSize;
import static plugins.perrine.easyclemv0.storage.xml.XmlSequenceSize.*;

public class SequenceSizeXmlWriter {

    private Document document;
    private SequenceSizeFactory sequenceSizeFactory = new SequenceSizeFactory();

    public SequenceSizeXmlWriter(Document document) {
        this.document = document;
    }

    public void writeSizeOf(Sequence sequence) {
        Element element = XMLUtil.addElement(document.getDocumentElement(), imageSizeElement);
        SequenceSize dimension = sequenceSizeFactory.getFrom(sequence);
        XMLUtil.setAttributeValue(element, imageDimension, String.valueOf(dimension.getN()));
        for(DimensionSize entry : dimension.getDimensions()) {
            Element value = document.createElement(dimensionSizeElement);
            value.setAttribute(imageDimensionName, entry.getDimensionId().name());
            value.setAttribute(dimensionpixelSize, String.valueOf(entry.getPixelSizeInNanometer()));
            value.setTextContent(String.valueOf(entry.getSize()));
            element.appendChild(value);
        }
    }
}
