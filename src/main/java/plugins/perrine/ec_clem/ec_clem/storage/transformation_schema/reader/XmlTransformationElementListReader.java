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
package plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.reader;

import icy.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.XmlTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.storage.transformation_schema.XmlTransformation;
import plugins.fr.univ_nantes.ec_clem.ec_clem.transformation.schema.TransformationSchema;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class XmlTransformationElementListReader {

    private XmlTransformationReader xmlTransformationReader;

    @Inject
    public XmlTransformationElementListReader(XmlTransformationReader xmlTransformationReader) {
        this.xmlTransformationReader = xmlTransformationReader;
    }

    public Element getLastTransformationElement(Document document) {
        List<Element> list = read(document);
        sortTransformationElementsByDate(list);
        return list.get(list.size() - 1);
    }

    public List<TransformationSchema> getTransformationList(Document document) {
        List<TransformationSchema> transformationSchemaList = new ArrayList<>();
        List<Element> elementList = read(document);
        sortTransformationElementsByDate(elementList);
        for(Element element : elementList) {
            transformationSchemaList.add(xmlTransformationReader.read(element));
        }
        return transformationSchemaList;
    }

    private List<Element> read(Document document) {
        return XMLUtil.getElements(document.getDocumentElement(), XmlTransformation.transformationElementName);
    }

    private void sortTransformationElementsByDate(List<Element> list) {
        list.sort(Comparator.comparing(o -> ZonedDateTime.parse(o.getAttribute(XmlTransformation.transformationDateAttributeName))));
    }

    @Inject
    public void setXmlTransformationReader(XmlTransformationReader xmlTransformationReader) {
        this.xmlTransformationReader = xmlTransformationReader;
    }
}
