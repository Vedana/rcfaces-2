/*
 * $Id: Tag1_2.java,v 1.3 2011/10/12 15:54:00 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.io.PrintWriter;
import java.util.Map;

import nu.xom.Element;

import com.vedana.camelia.generator.components_1_1.Tag;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

public class Tag1_2 extends Tag {

    public Tag1_2(Element tagXml, Map<String, RenderKit> renderKits) {
        super(tagXml, renderKits);
    }

    @Override
    protected void writeTldAttributes(PrintWriter out, boolean rtexprvalue,
            boolean deferredValue, String deferredType) {
        if (deferredValue) {
            out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

            if ("Number".equals(deferredType)
                    || "java.lang.Number".equals(deferredType)) {
                deferredType = "double";
            }

            if (deferredType != null) {
                out.println("\t\t\t<deferred-value>");
                out.println("\t\t\t\t<type>" + deferredType + "</type>");
                out.println("\t\t\t</deferred-value>");

            } else {
                out.println("\t\t\t<deferred-value/>");
            }
        } else if (rtexprvalue) {
            out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
        }
    }
}
