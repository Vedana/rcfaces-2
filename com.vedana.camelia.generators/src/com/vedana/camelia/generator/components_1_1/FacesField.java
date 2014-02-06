/*
 * $Id: FacesField.java,v 1.3 2012/12/07 13:45:23 oeuillot Exp $
 * 
 * $Log: FacesField.java,v $
 * Revision 1.3  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class FacesField {

    private final String id;

    private final String type;

    private final String value;

    private final int modifiers;

    private final List<String> imports = new ArrayList<String>();

    public FacesField(int modifiers, String id, String type, String value) {
        this.id = id;
        this.value = value;
        this.modifiers = modifiers;

        int idx = type.lastIndexOf('.');
        if (idx > 0) {
            imports.add(type);
            type = type.substring(idx + 1);
        }

        this.type = type;
    }

    public Collection<String> listImports() {
        return imports;
    }

    public void writeSource(PrintWriter pw) {
        pw.print("\t");

        if ((modifiers & Modifier.PROTECTED) > 0) {
            pw.print(" protected");

        } else if ((modifiers & Modifier.PUBLIC) > 0) {
            pw.print(" public");

        } else {
            pw.print(" private");

        }

        if ((modifiers & Modifier.STATIC) > 0) {
            pw.print(" static");
        }

        if ((modifiers & Modifier.FINAL) > 0) {
            pw.print(" final");
        }

        if ((modifiers & Modifier.TRANSIENT) > 0) {
            pw.print(" transient");
        }

        String v = value;
        pw.print(" " + type + " " + id);
        if (v != null) {
            if (type.equals("String") || type.equals("java.lang.String")) {
                if (v.equals("null") == false && v.startsWith("\"") == false) {
                    v = "\"" + value + '\"';
                }
            }

            pw.print(" = " + v);
        }

        pw.println(";");
    }

}
