/*
 * $Id: Component2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 */
package com.vedana.camelia.generator.components_2_2;

import java.beans.IntrospectionException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.Method1_1;
import com.vedana.camelia.generator.components_1_2.Component1_2;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.1 $
 */
public class Component2_2 extends Component1_2 {

    protected Set<String> behaviorEvents = new HashSet<String>();

    public Component2_2(CapabilitiesRepository cr, Element xml,
            Map<String, RenderKit> renderKits, boolean bootStrap,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException {
        super(cr, xml, renderKits, bootStrap, components, facesComponents);

        imports.add("java.util.Collection");

        Elements bes = xml.getChildElements("behaviorEvent");
        for (int i = 0; i < bes.size(); i++) {
            behaviorEvents.add(bes.get(i).getAttributeValue("id"));
        }
    }

    @Override
    protected void writeCameliaFields(PrintWriter out) {

    }

    @Override
    protected void writeSourceFooter(PrintWriter out) {
        super.writeSourceFooter(out);

        if (behaviorEvents.size() > 0) {
            out.println("\t");
            out.println("\tprotected static Collection<String> staticGetEventNames() {");
            out.println("\t\treturn BEHAVIOR_EVENT_NAMES;");
            out.println("\t}");
            out.println("\t");
            out.println("\t@Override");
            out.println("\tpublic Collection<String> getEventNames() {");
            out.println("\t\treturn staticGetEventNames();   ");
            out.println("\t}");
        }
    }

    @Override
    protected void writeStaticFieldsSource(PrintWriter out,
            String parentClassName) {
        super.writeStaticFieldsSource(out, parentClassName);

        if (behaviorEvents.size() > 0) {

            out.println("\tprotected static final Collection<String> BEHAVIOR_EVENT_NAMES=new HashSet<String>("
                    + parentClassName + ".BEHAVIOR_EVENT_NAMES);");
            out.println("\tstatic {");

            if (behaviorEvents.size() == 1) {
                out.println("\t\tBEHAVIOR_EVENT_NAMES.add(\""
                        + behaviorEvents.iterator().next() + "\");");

            } else {
                out.print("\t\tBEHAVIOR_EVENT_NAMES.addAll(Arrays.asList(");
                boolean first = true;
                for (String att : behaviorEvents) {
                    if (first == false) {
                        out.print(",");
                    } else {
                        first = false;
                    }
                    out.print("\"" + att + "\"");
                }
                out.println("));");
            }
            out.println("\t}");
        } else {

            out.println("\tprotected static final Collection<String> BEHAVIOR_EVENT_NAMES="
                    + parentClassName + ".BEHAVIOR_EVENT_NAMES;");

        }
    }

    @Override
    protected void writeCameliaAttributs(PrintWriter out,
            Set<String> cameliaAttributes, String parentClassName,
            String propertyValue) {

    }

    @Override
    protected String convertImplementsPropertyName(String propertyName) {
        return CameliaGenerator.PROPERTY_PREFIX
                + propertyName;
    }

    @Override
    protected void translateProperty(PrintWriter out, String sid, boolean first) {

        String id = Component1_1.getPropertyName(sid);

        if (first) {
            out.println("\t\tif (Properties." + id
                    + ".toString().equals(name)) {");
        } else {
            out.println();
            out.println("\t\t} else if (Properties." + id
                    + ".toString().equals(name)) {");
        }

        String alias = bindingAliases.get(sid);
        if (alias != null) {
            alias = Component1_1.getPropertyName(alias);

            out.println("\t\t\tname=Properties." + alias + ".toString();");
        }

        String code = bindingCases.get(sid);
        if (code != null) {
            out.println("\t\t\t" + code);
        }
    }

    @Override
    protected void writeFaceletsHandlerImports(PrintWriter out) {
        out.println("import javax.faces.view.facelets.ComponentConfig;");
    }

    @Override
    protected void writeFaceletsTextHandlerImports(PrintWriter out) {
        out.println("import javax.faces.view.facelets.FaceletContext;");
    }

    @Override
    protected void writeFaceletsTextHandlerMethods(PrintWriter out) {
        out.println("\t@Override");
        out.println("\tpublic void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {");
        out.println("\t\tsetTextBody(ctx, c);");
        out.println("\t}");
        out.println();
    }
}