/*
 * $Id: FacesTagAttribute.java,v 1.3 2011/10/12 15:54:00 oeuillot Exp $
 * 
 * $Log: FacesTagAttribute.java,v $
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
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import com.vedana.camelia.generator.components.AttributeDoc;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class FacesTagAttribute {

    protected String id;

    protected boolean required;

    protected boolean generate;

    protected String type;

    public FacesTagAttribute(String id, String type, boolean required,
            boolean generate) {
        this.id = id;
        this.type = type;
        this.required = required;
        this.generate = generate;

    }

    public void writeTldAttribute(PrintWriter out, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {

        out.println("\t\t<attribute>");

        AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, id);
        if (att != null) {
            att.writeTld(out);
        }

        out.println("\t\t\t<name>" + id + "</name>");
        if (required) {
            out.println("\t\t\t<required>true</required>");
        }

        writeTldAttribute(out);

        out.println("\t\t</attribute>");
        CameliaGenerator.attributesAutodoc.add(id);
    }

    protected void writeTldAttribute(PrintWriter out) {
        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    /**
     * @param out
     */
    public void writeTagGetterAndSetter(PrintWriter out) {
        if (generate == false) {
            return;
        }

        String methodName = id;
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);

        String varId = id;

        out.print("\tpublic final String get");
        out.println(methodName + "() {");
        out.println("\t\treturn " + varId + ";");
        out.println("\t}\n");

        out.print("\tpublic final void set");
        out.println(methodName + "(String " + varId + ") {");
        out.println("\t\tthis." + varId + " = " + varId + ";");
        out.println("\t}\n");
    }

    /**
     * @param out
     */
    public void writTagVariable(PrintWriter out) {
        if (generate == false) {
            return;
        }

        out.println("\tprivate String " + id + ";");
    }

    /**
     * @param out
     */
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
        out.println("\t\t\tif (isValueReference(" + varId + ")) {");
        out.println("\t\t\t\tValueBinding vb = application.createValueBinding("
                + varId + ");");
        out.println("\t\t\t\tcomponent.setValueBinding(Properties."
                + propertyName + ", vb);");
        out.println();
        out.println("\t\t\t} else {");
        out.print("\t\t\t\tcomponent.set" + methodName + "(");

        if (clName.startsWith("java.lang.")) {
            clName = clName.substring(10);
        }

        if (clName.equals("int")) {
            out.print("getInt(" + varId + ")");

        } else if (clName.equals("long")) {
            out.print("getLong(" + varId + ")");

        } else if (clName.equals("boolean")) {
            out.print("getBool(" + varId + ")");

        } else if (clName.equals("Boolean")) {
            out.print("getBoolean(" + varId + ")");

        } else {
            out.print(varId);
        }
        out.println(");");
        out.println("\t\t\t}");
        out.println("\t\t}");
    }

    /**
     * @param out
     */
    public void writTagRelease(PrintWriter out) {
        if (generate == false) {
            return;
        }

        out.println("\t\t" + id + " = null;");
    }

    public String getId() {
        return id;
    }

    public void writeTagLogProperties(PrintWriter out) {

        out.println("\t\t\tLOG.debug(\"  " + id + "='\"+" + id + "+\"'\");");

    }

    public void listCameliaAttributes(Set<String> set) {
        if (generate == false) {
            return;
        }
        set.add(id);
    }
}
