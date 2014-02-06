/*
 * $Id: Action1_1.java,v 1.1 2011/10/12 15:54:00 oeuillot Exp $
 * 
 * $Log: Action1_1.java,v $
 * Revision 1.1  2011/10/12 15:54:00  oeuillot
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
 * @version $Revision: 1.1 $
 */
public class Action1_1 {

    protected final String id;

    protected final String type;

    protected final boolean main;

    protected final String name;

    public Action1_1(String id, String type, boolean main) {
        this.id = id;

        this.type = type;

        this.main = main;

        if (main == false) {
            name = "on" + Character.toUpperCase(type.charAt(0))
                    + type.substring(1) + "Action";
        } else {
            name = "action";
        }
    }

    public void writeTagVariable(PrintWriter out) {

        out.println("\tprivate String " + name + ";");
        out.println("\tprivate String " + name + "Listeners;");
    }

    public void writeTagGetterAndSetter(PrintWriter out) {

        String propertyName = Character.toUpperCase(name.charAt(0))
                + name.substring(1);
        out.println("\tpublic final void set" + propertyName + "(String "
                + name + ") {");
        out.println("\t\tthis." + name + "=" + name + ";");
        out.println("\t}\n");
        out.println("\tpublic final String get" + propertyName + "() {");
        out.println("\t\treturn " + name + ";");
        out.println("\t}\n");

        out.println("\tpublic final void set" + propertyName
                + "Listener(String listeners) {");
        out.println("\t\tthis." + name + "Listeners = listeners;");
        out.println("\t}\n");
    }

    public void writeTagRelease(PrintWriter out) {
        out.println("\t\t" + name + " = null;");
        out.println("\t\t" + name + "Listeners = null;");
    }

    public void writeTld(PrintWriter out, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        out.println("\t\t<attribute>");

        AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, name);
        if (att != null) {
            att.writeTld(out);
        }

        out.println("\t\t\t<name>" + name + "</name>");
        // out.println("\t\t\t<required>false</required>");

        writeTldAttributes(out, name);

        out.println("\t\t</attribute>");

        out.println("\t\t<attribute>");

        att = ComponentDoc.getAttributeDoc(componentDoc, attributesDoc, name
                + "Listener");
        if (att != null) {
            att.writeTld(out);
        }
        out.println("\t\t\t<name>" + name + "Listener</name>");
        // out.println("\t\t\t<required>false</required>");

        writeTldAttributes(out, name + "Listener");

        out.println("\t\t</attribute>");

        CameliaGenerator.attributesAutodoc.add(name);
        CameliaGenerator.attributesAutodoc.add(name + "Listener");
    }

    protected void writeTldAttributes(PrintWriter out, String name2) {
        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    public void writeTagSetProperties(PrintWriter out) {
        String type = Component1_1.getPropertyName(this.type);

        out.println("\n\t\tif (" + name + " != null) {");
        out.println("\t\t\t" + getListenersToolsClassName()
                + ".parseAction(facesContext, component, ListenersTools."
                + type + "_LISTENER_TYPE, " + name + ");");
        out.println("\t\t}");

        out.println("\n\t\tif (" + name + "Listeners != null) {");
        out.println("\t\t\t" + getListenersToolsClassName()
                + ".parseListener(facesContext, component, ListenersTools."
                + type + "_LISTENER_TYPE, " + name + "Listeners, " + main
                + ");");
        out.println("\t\t}");

        // parseActionListener(application, component, SELECT_LISTENER_TYPE,
        // selectListeners);

    }

    protected String getListenersToolsClassName() {
        return "ListenersTools1_1";
    }

    public void writeTagLogProperties(PrintWriter out) {
        out
                .println("\t\t\tLOG.debug(\"  " + name + "='\"+" + name
                        + "+\"'\");");
        out.println("\t\t\tLOG.debug(\"  " + name + "Listeners='\"+" + name
                + "Listeners+\"'\");");
    }

    public void writeFaceletsHandler(PrintWriter out) {
        String type = Component1_1.getPropertyName(this.type);

        out.println("\tprotected IListenerType getDefaultListenerType() {");
        out.println("\t\treturn ListenersTools." + type + "_LISTENER_TYPE;");
        out.println("\t}");
        out.println();
    }

    public void listCameliaAttributes(Set<String> set) {
        // on ne traite pas les actions ni listeners
    }

}
