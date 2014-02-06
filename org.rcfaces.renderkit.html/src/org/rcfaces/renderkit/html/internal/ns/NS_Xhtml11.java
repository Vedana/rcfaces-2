/*
 * $Id: NS_Xhtml11.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:02 $
 */
public class NS_Xhtml11 extends AbstractNamespaceSchema {

    protected String rcfacesURN = "org.rcfaces.html";

    protected String xhtml11RcfacesSchemaLocation = "xhtml11-rcfaces.xsd";

    protected String rcfacesSchemaLocation = "rcfaces-html.xsd";

    protected String rcfacesModuleSchemaLocation = "rcfaces-html-module.xsd";

    protected String rcfacesAttributeListName = "rcface.html.attribs";

    protected NamespaceServlet.IBuffer mainSchemaBuffer;

    protected NamespaceServlet.IBuffer rcfacesSchemaBuffer;

    protected NamespaceServlet.IBuffer rcfacesModuleSchemaBuffer;

    public NS_Xhtml11() {
        super("xhtml11");
    }

    public void setNamespaceServlet(NamespaceServlet servlet,
            NamespaceConfigurationImpl namespaceConfiguration) {

        mainSchemaBuffer = recordBuffer(servlet, xhtml11RcfacesSchemaLocation,
                constructMainSchema());
        rcfacesSchemaBuffer = recordBuffer(servlet, rcfacesSchemaLocation,
                constructRCFacesSchema(namespaceConfiguration));
        rcfacesModuleSchemaBuffer = recordBuffer(servlet,
                rcfacesModuleSchemaLocation, constructRCFacesModuleSchema());
    }

    protected String constructMainSchema() {

        StringAppender sa = new StringAppender();

        sa.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sa.append("<xs:schema\n");
        sa.append("  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n");
        sa.append("  targetNamespace=\"http://www.w3.org/1999/xhtml\"\n");
        sa.append("  xmlns:xh11d=\"http://www.w3.org/1999/xhtml/datatypes/\"\n");
        sa.append("  xmlns=\"http://www.w3.org/1999/xhtml\"\n");
        sa.append("  elementFormDefault=\"qualified\" >\n");
        sa.append("  <xs:import\n");
        sa.append("    namespace=\"http://www.w3.org/XML/1998/namespace\"\n");
        sa.append("    schemaLocation=\"http://www.w3.org/2001/xml.xsd\" />\n");
        sa.append("  <xs:include\n");
        sa.append("    schemaLocation=\"xhtml11.xsd\" />\n");
        sa.append("  <xs:import\n");
        sa.append("    namespace=\"" + getNameSpace() + "\"\n");
        sa.append("    schemaLocation=\"" + getSchemaLocation() + "\" />\n");
        sa.append("  <xs:include\n");
        sa.append("    schemaLocation=\"" + getModuleSchemaLocation()
                + "\" />\n");
        sa.append("</xs:schema>\n");

        return sa.toString();
    }

    protected String constructRCFacesSchema(
            NamespaceConfigurationImpl namespaceConfiguration) {
        StringAppender sa = new StringAppender();

        sa.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sa.append("<xs:schema\n");
        sa.append("  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n");
        sa.append("  elementFormDefault=\"qualified\"\n");
        sa.append("  xmlns:xh11d=\"http://www.w3.org/1999/xhtml/datatypes/\"\n");
        sa.append("  targetNamespace=\"" + getNameSpace() + "\">\n");
        sa.append("  <xs:attributeGroup name=\"" + rcfacesAttributeListName
                + "\">\n");

        NamespaceConfigurationImpl.NameSpace ns = namespaceConfiguration
                .getNameSpace(NamespaceConfigurationImpl.DEFAULT_NAMESPACE);

        NamespaceConfigurationImpl.Component cp = ns
                .getComponent(NamespaceConfigurationImpl.DEFAULT_COMPONENT);

        String[] atts = cp.listAttributes();
        for (int i = 0; i < atts.length; i++) {
            String name = atts[i];

            sa.append("     <xs:attribute name=\"" + name
                    + "\" type=\"xs:string\"/>\n");
        }
        sa.append("  </xs:attributeGroup>\n");
        sa.append(" </xs:schema>\n");

        return sa.toString();
    }

    protected String constructRCFacesModuleSchema() {
        StringAppender sa = new StringAppender();

        sa.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sa.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n");
        sa.append("  elementFormDefault=\"qualified\"\n");
        sa.append("  xmlns:xh11d=\"http://www.w3.org/1999/xhtml/datatypes/\">\n");
        sa.append("  <xs:import namespace=\"" + getNameSpace() + "\"\n");
        sa.append("            schemaLocation=\"" + getSchemaLocation()
                + "\" />\n");
        sa.append("  <xs:redefine schemaLocation=\"xhtml-attribs-1.xsd\">\n");
        sa.append("    <xs:attributeGroup name=\"xhtml.Core.attrib\">\n");
        sa.append("      <xs:attributeGroup ref=\"xhtml.Core.attrib\"/>\n");
        sa.append("      <xs:attributeGroup ref=\"" + rcfacesAttributeListName
                + "\"/>\n");
        sa.append("    </xs:attributeGroup>\n");
        sa.append("  </xs:redefine>\n");
        sa.append("</xs:schema>\n");

        return sa.toString();
    }

    public String getNameSpace() {
        return "urn:" + rcfacesURN;
    }

    public String getSchemaLocation() {
        return rcfacesSchemaLocation;
    }

    private String getModuleSchemaLocation() {
        return rcfacesModuleSchemaLocation;
    }

}
