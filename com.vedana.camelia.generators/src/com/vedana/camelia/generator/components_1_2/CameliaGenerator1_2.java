/*
 * $Id: CameliaGenerator1_2.java,v 1.4 2011/10/12 15:54:00 oeuillot Exp $
 * 
 * $Log: CameliaGenerator1_2.java,v $
 * Revision 1.4  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.3  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.5  2006/12/18 15:14:02  oeuillot
 * *** empty log message ***
 *
 * Revision 1.4  2006/11/27 14:24:11  oeuillot
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/21 13:37:20  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/11/14 17:06:38  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_1_2;

import java.io.PrintWriter;
import java.net.MalformedURLException;

import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.4 $
 */
public class CameliaGenerator1_2 extends CameliaGenerator {

    // private static final IFactory FACTORY = new Factory1_2();

    public static void main(String args[]) throws Exception {

        Config config = parseConfig(args);

        CameliaGenerator1_2 generator = new CameliaGenerator1_2(config);

        generator.generate(config);
    }

    protected CameliaGenerator1_2(Config config) throws MalformedURLException {
        super(config);
    }

    @Override
    protected IFactory createFactory() {
        return new Factory1_2();
    }

    public static String convertType(String type) {
        if (type.equals("javax.faces.el.ValueBinding")) {
            return "javax.el.ValueExpression";
        }
        if (type.equals("ValueBinding")) {
            return "ValueExpression";
        }
        return type;
    }

    public static String transformCode(String code) {
        String c = code;

        c = c.replaceAll("engine\\.getValueBindingProperty\\(",
                "engine.getValueExpressionProperty(");

        c = c.replaceAll("ValueBinding", "ValueExpression");
        return c;
    }

    @Override
    protected void writeTldNameSpace(PrintWriter out) {
        out.println("<taglib xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd\" version=\"2.1\">");
    }

    @Override
    protected void writeConverterTldAttribute(PrintWriter out,
            boolean rtexprvalue, String type) {
        if ("Number".equals(type) || "java.lang.Number".equals(type)) {
            type = "double";
        }

        out.println("\t\t\t<deferred-value>");
        out.println("\t\t\t\t<type>" + type + "</type>");
        out.println("\t\t\t</deferred-value>");
    }

    @Override
    protected void writeJspVersion(PrintWriter out) {
    }

    @Override
    protected void writeTlibVersion(PrintWriter out) {
        out.println("\t<tlib-version>1.2</tlib-version>");
    }

    @Override
    protected void writeCoreApplication(PrintWriter out) {
    }

    @Override
    protected void writeOtherFacesComponentComponent(PrintWriter out,
            RenderKit renderKit) {
        if ("core".equals(renderKit.getName())) {
            out.println("\t<component>");
            out.println("\t\t<component-type>javax.faces.HtmlOutputText</component-type>");
            out.println("\t\t<component-class>org.rcfaces.core.internal.component.AsyncHtmlOutputText</component-class>");
            out.println("\t</component>");

        }

    }

    @Override
    protected boolean writeOverride() {
        return true;
    }

}