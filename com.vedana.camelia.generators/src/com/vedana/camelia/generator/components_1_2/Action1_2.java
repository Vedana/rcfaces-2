/*
 * $Id: Action1_2.java,v 1.3 2011/10/12 15:54:00 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.io.PrintWriter;

import com.vedana.camelia.generator.components_1_1.Action1_1;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class Action1_2 extends Action1_1 {

    public Action1_2(String id, String type, boolean main) {
        super(id, type, main);
    }

    @Override
    public void writeTagVariable(PrintWriter out) {

        out.println("\tprivate ValueExpression " + name + "Listeners;");
        out.println("\tprivate ValueExpression " + name + ";");
    }

    @Override
    public void writeTagGetterAndSetter(PrintWriter out) {

        String propertyName = Character.toUpperCase(name.charAt(0))
                + name.substring(1);
        out.println("\tpublic final void set" + propertyName
                + "(ValueExpression " + name + ") {");
        out.println("\t\tthis." + name + "=" + name + ";");
        out.println("\t}\n");

        out.println("\tpublic final void set" + propertyName
                + "Listener(ValueExpression listeners) {");
        out.println("\t\tthis." + name + "Listeners = listeners;");
        out.println("\t}\n");
    }

    @Override
    protected void writeTldAttributes(PrintWriter out, String name) {
        // Pas de super .... pas de rtexprvalue
        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

        out.println("\t\t\t<deferred-value>");
        out.println("\t\t\t\t<type>java.lang.String</type>");
        out.println("\t\t\t</deferred-value>     ");
    }

    @Override
    protected String getListenersToolsClassName() {
        return "ListenersTools1_2";
    }

}
