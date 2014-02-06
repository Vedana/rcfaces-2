/*
 * $Id: AttributeDoc.java,v 1.4 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.components;

import java.io.PrintWriter;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator;

import nu.xom.Element;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.4 $ $Date: 2009/01/15 15:16:52 $
 */
public class AttributeDoc {
    private String name;

    private String doc;

    private String getterDoc;

    private String setterDoc;

    public AttributeDoc(Element tagXml) {
        name = tagXml.getAttributeValue("id");

        doc = tagXml.getFirstChildElement("doc").getValue().trim();

        Element e = tagXml.getFirstChildElement("getter-doc");
        if (e != null) {
            getterDoc = CameliaGenerator.formatComment(e.getValue().trim(), 1);
        }

        e = tagXml.getFirstChildElement("setter-doc");
        if (e != null) {
            setterDoc = CameliaGenerator.formatComment(e.getValue().trim(), 1);
        }
    }

    public String getName() {
        return name;
    }

    public String getDoc() {
        return doc;
    }

    public void writeTld(PrintWriter out) {
        if (doc.length() < 1) {
            return;
        }
        out.println("\t\t\t<description><![CDATA[");
        out.println(doc);
        out.println("]]></description>");
    }

    public String getGetterDoc() {
        return getterDoc;
    }

    public String getSetterDoc() {
        return setterDoc;
    }

    public void writeFacesConfig(PrintWriter out) {
        if (doc.length() < 1) {
            return;
        }
        out.println("\t\t\t<description><![CDATA[");
        out.println(doc);
        out.println("]]></description>");
    }
}
