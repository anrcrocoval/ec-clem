package plugins.perrine.easyclemv0.storage.xml;

import icy.sequence.DimensionId;
import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.perrine.easyclemv0.model.DimensionSize;
import plugins.perrine.easyclemv0.model.SequenceSize;
import java.util.ArrayList;
import static plugins.perrine.easyclemv0.storage.xml.XmlSequenceSize.*;

public class SequenceSizeXmlReader {

    public SequenceSize readSize(Document document) {
        SequenceSize sequenceSize = new SequenceSize();
        Element root = XMLUtil.getRootElement(document);
        Element targetSizeElement = XMLUtil.getElement(root, imageSizeElement);
        ArrayList<Element> elements = XMLUtil.getElements(targetSizeElement);
        for(Element dimension : elements) {
            sequenceSize.add(new DimensionSize(
                DimensionId.valueOf(dimension.getAttribute(imageDimensionName)),
                Integer.valueOf(dimension.getTextContent()),
                Double.valueOf(dimension.getAttribute(dimensionpixelSize))
            ));
        }
        return sequenceSize;
    }
}
