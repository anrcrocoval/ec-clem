package plugins.perrine.ec_clem.ec_clem.storage.transformation.xml;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import icy.util.XMLUtil;

public class XmlFiletoTransformationReader {
	public double[][] read(Document document) {
	

	
	Element transfo=XMLUtil.getElements(document.getDocumentElement(),"MatrixTransformation").get(0);
	Element transfoinfo=XMLUtil.getElements(document.getDocumentElement(),"transformation").get(0);
	 ArrayList<Element> sequenceSizeElements = XMLUtil.getElements(transfoinfo, "sequenceSize");
        if(sequenceSizeElements.size() != 2) {
            throw new RuntimeException("Element should contain exactly 2 sequenceSize");
        }
        double sourceSequenceSize = readSequenceSize(sequenceSizeElements.get(0));
        double targetSequenceSize = readSequenceSize(sequenceSizeElements.get(1));
       
        if(sequenceSizeElements.get(0).getAttribute("type").equals("target")) {
            double tmp=sourceSequenceSize;
            sourceSequenceSize=targetSequenceSize;
            targetSequenceSize=tmp;
        } 
	double[][] m = new double[4][4];
	for (int i=0;i<4;i++)
	{
		for(int j=0;j<4;j++) {
			m[i][j] = XMLUtil.getAttributeDoubleValue(transfo, "m"+String.valueOf(i)+String.valueOf(j),0);
			
		}
	}
	return m;
	}
	private double readSequenceSize(Element element) {
		double pixelsizeum=1.0;
		 ArrayList<Element> elements = XMLUtil.getElements(element);
	        for(Element dimension : elements) {
	           
	                String dim = (dimension.getAttribute("name"));
	                if (dim.compareTo("X")==0) 
	                	pixelsizeum=Double.parseDouble(dimension.getAttribute("pixelSize"));
	           
	        }
		return pixelsizeum;
	}
	
}
