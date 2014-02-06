/*
 * $Id: ComponentDoc.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Elements;

import com.vedana.camelia.generator.components.AttributeDoc;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public class ComponentDoc {
    private String name;

    private String doc;

    private Map<String, AttributeDoc> mainAttributesDoc = new HashMap<String, AttributeDoc>();

    private Map<String, AttributeDoc> attributeDocs = new HashMap<String, AttributeDoc>();

    public ComponentDoc(Element tagXml) {
        name = tagXml.getAttributeValue("id");

        doc = tagXml.getFirstChildElement("doc").getValue().trim();

        Elements elements = tagXml.getChildElements("attribute");

        for (int i = 0; i < elements.size(); i++) {
            AttributeDoc att = new AttributeDoc(elements.get(i));

            attributeDocs.put(att.getName(), att);
        }
    }

    public String getName() {
        return name;
    }

    public String getDoc() {
        return doc;
    }

    public AttributeDoc getAttribute(String name) {
        AttributeDoc att = attributeDocs.get(name);
        if (att != null) {
            return att;
        }

        return mainAttributesDoc.get(name);
    }

    public void writeTld(PrintWriter out) {
        if (doc.length() < 1) {
            return;
        }
        out.println("\t\t<description><![CDATA[");
        out.println(doc);
        out.println("]]></description>");
    }

    public static AttributeDoc getAttributeDoc(ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc, String attributeName) {
        if (componentDoc != null) {
            AttributeDoc att = componentDoc.getAttribute(attributeName);
            if (att != null) {
                return att;
            }
        }

        return attributesDoc.get(attributeName);
    }

    public void writeFacesConfig(PrintWriter out) {
        writeTld(out);
    }
}
