/*
 * $Id: TagLibDoc.java,v 1.1 2008/01/02 09:40:19 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;

import nu.xom.Element;

public class TagLibDoc {

    private String name;

    private String doc;

    private String displayName;

    public TagLibDoc(Element tagXml) {
        name = tagXml.getAttributeValue("id");

        displayName = tagXml.getFirstChildElement("display-name").getValue()
                .trim();

        doc = tagXml.getFirstChildElement("doc").getValue().trim();
    }

    public String getName() {
        return name;
    }

    public String getDoc() {
        return doc;
    }

    public void writeTld(PrintWriter out) {
        if (displayName != null) {
            out.println("\t<display-name>" + displayName + "</display-name>");
        }
        if (doc.length() > 0) {
            out.println("\t<description><![CDATA[");
            out.println(doc);
            out.println("]]></description>");
        }
    }
}
