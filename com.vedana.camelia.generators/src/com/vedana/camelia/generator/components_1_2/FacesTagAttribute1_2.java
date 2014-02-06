/*
 * $Id: FacesTagAttribute1_2.java,v 1.3 2011/10/12 15:54:00 oeuillot Exp $
 * 
 * $Log: FacesTagAttribute1_2.java,v $
 * Revision 1.3  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/27 14:24:11  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/11/21 13:37:20  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_1_2;

import java.io.PrintWriter;
import java.util.Map;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.FacesTagAttribute;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class FacesTagAttribute1_2 extends FacesTagAttribute {

    public FacesTagAttribute1_2(String id, String type, boolean required,
            boolean generate) {
        super(id, type, required, generate);

    }

    /**
     * @param out
     */
    @Override
    public void writeTagGetterAndSetter(PrintWriter out) {
        if (generate == false) {
            return;
        }

        String methodName = id;
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);

        String varId = id;

        out.print("\tpublic final void set");
        out.println(methodName + "(ValueExpression " + varId + ") {");
        out.println("\t\tthis." + varId + " = " + varId + ";");
        out.println("\t}\n");
    }

    /**
     * @param out
     */
    @Override
    public void writTagVariable(PrintWriter out) {
        if (generate == false) {
            return;
        }

        out.println("\tprivate ValueExpression " + id + ";");
    }

    /**
     * @param out
     */
    @Override
    public void writTagSetProperties(PrintWriter out,
            Map<String, String> properties) {
        if (generate == false) {
            return;
        }

        String methodName = id;
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);
        String clName = type;

        String propertyName = Component1_1.getPropertyName(id);
        properties.put(propertyName, CameliaGenerator.PROPERTY_PREFIX
                + Component1_1.lowerCaseFirstChar(id));

        String varId = id;

        out.println("\n\t\tif (" + varId + " != null) {");
        out.println("\t\t\tif (" + varId + ".isLiteralText()==false) {");
        out.println("\t\t\t\tcomponent.setValueExpression(Properties."
                + propertyName + ", " + varId + ");");
        out.println();
        out.println("\t\t\t} else {");
        out.print("\t\t\t\tcomponent.set" + methodName + "(");

        if (clName.startsWith("java.lang.")) {
            clName = clName.substring(10);
        }

        if (clName.equals("int")) {
            out.print("getInt(" + varId + ".getExpressionString())");

        } else if (clName.equals("long")) {
            out.print("getLong(" + varId + ".getExpressionString())");

        } else if (clName.equals("boolean")) {
            out.print("getBool(" + varId + ".getExpressionString())");

        } else if (clName.equals("Boolean")) {
            out.print("getBoolean(" + varId + ".getExpressionString())");

        } else {
            out.print(varId + ".getExpressionString()");
        }
        out.println(");");
        out.println("\t\t\t}");
        out.println("\t\t}");
    }

    @Override
    protected void writeTldAttribute(PrintWriter out) {
        // super.writeTldAttribute(out);

        String clName = type;
        if ("Number".equals(clName) || "java.lang.Number".equals(clName)) {
            clName = "double";
        }

        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

        if (clName == null) {
            out.println("\t\t\t<deferred-value />");

        } else {
            out.println("\t\t\t<deferred-value>");
            out.println("\t\t\t\t<type>" + clName + "</type>");
            out.println("\t\t\t</deferred-value>");
        }
    }

}
