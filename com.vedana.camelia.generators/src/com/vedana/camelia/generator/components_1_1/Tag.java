/*
 * $Id: Tag.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 * 
 * $Log: Tag.java,v $
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le générateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/11/27 14:24:11  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Elements;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

public class Tag {

    private String tagClass;

    private String bodyContent;

    private String name;

    private List<Attribute> attributes = new ArrayList<Attribute>();

    private RenderKit renderKit;

    public Tag(Element tagXml, Map<String, RenderKit> renderKits) {
        String renderKitName = tagXml.getAttributeValue("renderKit");
        if (renderKitName == null) {
            renderKitName = "core";
        }

        renderKit = renderKits.get(renderKitName);

        name = tagXml.getFirstChildElement("name").getValue();
        tagClass = tagXml.getFirstChildElement("tag-class").getValue();
        bodyContent = tagXml.getFirstChildElement("body-content").getValue();

        Elements atts = tagXml.getChildElements("attribute");
        for (int i = 0; i < atts.size(); i++) {
            attributes.add(new Attribute(atts.get(i)));
        }
    }

    public String getName() {
        return name;
    }

    public RenderKit getRenderKit() {
        return renderKit;
    }

    public void writeTld(PrintWriter out) {

        CameliaGenerator.componentsAutodoc.add(getName());

        out.println("\t<tag>");
        out.println("\t\t<name>" + getName() + "</name>");
        out.println("\t\t<tag-class>" + getTagClass() + "</tag-class>");
        out
                .println("\t\t<tei-class>org.rcfaces.core.internal.taglib.CameliaTagExtraInfo</tei-class>");
        if (getBodyContent() != null) {
            out.println("\t\t<body-content>" + getBodyContent()
                    + "</body-content>");
        }

        for (Iterator<Attribute> it = attributes.iterator(); it.hasNext();) {
            Attribute attribute = it.next();

            attribute.writeTld(out);
        }

        out.println("\t</tag>");
    }

    private String getBodyContent() {
        return bodyContent;
    }

    private String getTagClass() {
        return tagClass;
    }

    private class Attribute {

        private String name;

        private boolean required;

        private boolean rtexprvalue;

        private boolean deferredValue;

        private String deferredType;

        public Attribute(Element element) {
            name = element.getFirstChildElement("name").getValue();

            Element r = element.getFirstChildElement("required");
            if (r != null) {
                required = "true".equalsIgnoreCase(r.getValue());
            }

            r = element.getFirstChildElement("deferred-value");
            if (r != null) {
                deferredValue = true;

                r = r.getFirstChildElement("type");
                if (r != null) {
                    deferredType = r.getValue();
                    if (deferredType != null) {
                        deferredType = deferredType.trim();
                    }
                }
            }

            r = element.getFirstChildElement("rtexprvalue");
            if (r != null) {
                rtexprvalue = "true".equalsIgnoreCase(r.getValue());
            }
        }

        public void writeTld(PrintWriter out) {
            out.println("\t\t<attribute>");
            out.println("\t\t\t<name>" + name + "</name>");
            if (required) {
                out.println("\t\t\t<required>true</required>");
            }

            writeTldAttributes(out, rtexprvalue, deferredValue, deferredType);
            out.println("\t\t</attribute>");

            CameliaGenerator.attributesAutodoc.add(name);

        }

    }

    public void writeFaceletComponent(PrintWriter out) {

        String upname = Character.toUpperCase(name.charAt(0))
                + name.substring(1);

        out.println("\t<tag>");
        out.println("\t\t<tag-name>" + name + "</tag-name>");
        out.println("\t\t<handler-class>");
        out.println("\t\t\t" + renderKit.faceletsPackage + "." + upname
                + "Handler");
        out.println("\t\t</handler-class>");
        out.println("\t</tag>");
    }

    protected void writeTldAttributes(PrintWriter out, boolean rtexprvalue,
            boolean deferredValue, String deferredType) {
        if (rtexprvalue) {
            out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
        }
    }
}
