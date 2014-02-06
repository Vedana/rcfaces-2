/*
 * $Id: FacesComponent.java,v 1.4 2011/10/12 15:54:00 oeuillot Exp $
 * 
 * $Log: FacesComponent.java,v $
 * Revision 1.4  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.3  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.2  2008/06/05 09:42:24  oeuillot
 * Les fichiers de configuration sont dans le core
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

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import nu.xom.Element;
import nu.xom.Elements;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.4 $
 */
public class FacesComponent {

    private final String id;

    private final String extendsFaces;

    private String implementsFaces;

    private final String componentClassName;

    private final String componentFileName;

    private final List<String> methods = new ArrayList<String>();

    private final List<FacesTagAttribute> tagAttributes = new ArrayList<FacesTagAttribute>();

    private final List<FacesField> fields = new ArrayList<FacesField>();

    private final Set<String> imports = new HashSet<String>();

    private boolean ignoreRequired;

    private boolean inputType;

    private Map<String, Method1_1> localMethods = new HashMap<String, Method1_1>();

    private Map<String, String> bindingAliases = new HashMap<String, String>();

    private Map<String, String> bindingCases = new HashMap<String, String>();

    private Identifier componentType;

    public FacesComponent(IFactory factory, Element xml,
            Map<String, RenderKit> renderKits) {
        id = xml.getAttributeValue("id");
        extendsFaces = xml.getAttributeValue("extends");

        if (extendsFaces.equals("javax.faces.component.UIInput")) {
            inputType = true;
        }

        String renderKitName = xml.getAttributeValue("renderKit");
        if (renderKitName == null) {
            renderKitName = "core";
        }
        RenderKit renderKit = renderKits.get(renderKitName);

        componentType = new Identifier(renderKit, id);

        String impl = xml.getAttributeValue("implements");
        if (impl != null) {
            String s = "";
            StringTokenizer st = new StringTokenizer(impl, ", ");
            for (; st.hasMoreTokens();) {
                String token = st.nextToken();
                int pos = token.lastIndexOf('.');
                if (pos < 0) {
                    s += ", " + token;
                    continue;
                }

                imports.add(token);
                s += ", " + token.substring(pos + 1);
            }

            if (s.length() > 0) {
                implementsFaces = s;
            }
        }

        componentClassName = Character.toUpperCase(id.charAt(0))
                + id.substring(1) + "Component";
        componentFileName = Component1_1.FACES_COMPONENT_PACKAGE.replace('.', '/')
                + "/" + componentClassName + ".java";

        Elements methodsXml = xml.getChildElements("method");
        for (int i = 0; i < methodsXml.size(); i++) {
            Element methodXml = methodsXml.get(i);

            if (methodXml.getChildCount() > 0) {
                Method1_1 method = factory.newMethod(methodXml);
                localMethods.put(method.getSignature(), method);

                methods.add(method.getSignature());
                continue;
            }

            String s = methodXml.getAttributeValue("id");
            if (s.indexOf('(') < 0) {
                s += "()";
            } else {
                s = Method1_1.normalizeMethodId(s);
            }
            methods.add(s);
        }

        Elements tagAttributesXml = xml.getChildElements("tagAttribute");
        for (int i = 0; i < tagAttributesXml.size(); i++) {
            Element tagAttributeXml = tagAttributesXml.get(i);

            String attId = tagAttributeXml.getAttributeValue("id");
            String type = tagAttributeXml.getAttributeValue("type");
            boolean required = "true".equalsIgnoreCase(tagAttributeXml
                    .getAttributeValue("required"));
            boolean generate = "true".equalsIgnoreCase(tagAttributeXml
                    .getAttributeValue("generate"));

            tagAttributes.add(factory.newFacesTagAttribute(attId, type,
                    required, generate));
        }

        if ("true".equalsIgnoreCase(xml.getAttributeValue("ignoreRequired"))) {
            ignoreRequired = true;
        }

        Elements bindingAliasesXml = xml.getChildElements("binding-alias");
        for (int i = 0; i < bindingAliasesXml.size(); i++) {
            Element bindingAliasXml = bindingAliasesXml.get(i);

            bindingAliases.put(bindingAliasXml.getAttributeValue("id"),
                    bindingAliasXml.getAttributeValue("target"));
        }

        Elements bindingCasesXml = xml.getChildElements("binding-case");
        for (int i = 0; i < bindingCasesXml.size(); i++) {
            Element bindingCaseXml = bindingCasesXml.get(i);

            String code = bindingCaseXml.getFirstChildElement("code")
                    .getValue();

            bindingCases.put(bindingCaseXml.getAttributeValue("id"), code);
        }

        Elements fieldsXml = xml.getChildElements("field");
        parseFields(factory, fields, fieldsXml);
    }

    static void parseFields(IFactory factory, List<FacesField> fields,
            Elements fieldsXml) {
        for (int i = 0; i < fieldsXml.size(); i++) {
            Element fieldXml = fieldsXml.get(i);

            String attId = fieldXml.getAttributeValue("id");
            String type = fieldXml.getAttributeValue("type");
            String value = fieldXml.getAttributeValue("value");
            int modifiers = 0;
            String modif = fieldXml.getAttributeValue("modifiers");
            if (modif != null) {
                StringTokenizer st = new StringTokenizer(modif);
                for (; st.hasMoreTokens();) {
                    String tok = st.nextToken();

                    if (tok.equalsIgnoreCase("transient")) {
                        modifiers |= Modifier.TRANSIENT;
                        continue;
                    }

                    if (tok.equalsIgnoreCase("private")) {
                        modifiers |= Modifier.PRIVATE;
                        continue;
                    }

                    if (tok.equalsIgnoreCase("protected")) {
                        modifiers |= Modifier.PROTECTED;
                        continue;
                    }

                    if (tok.equalsIgnoreCase("public")) {
                        modifiers |= Modifier.PUBLIC;
                        continue;
                    }

                    if (tok.equalsIgnoreCase("final")) {
                        modifiers |= Modifier.FINAL;
                        continue;
                    }

                    if (tok.equalsIgnoreCase("static")) {
                        modifiers |= Modifier.STATIC;
                        continue;
                    }
                }
            }

            fields.add(factory.newFacesField(modifiers, attId, type, value));
        }

    }

    public String getComponentClassName() {
        return componentClassName;
    }

    public String getComponentFileName() {
        return componentFileName;
    }

    public String getId() {
        return id;
    }

    public void writeSource(PrintWriter out, Map<String, Method1_1> globalMethods,
            File templateBase) throws IOException {

        Map<String, Method1_1> methods = new HashMap<String, Method1_1>(globalMethods);
        methods.putAll(localMethods);

        File url = new File(templateBase, getCameliaBaseComponentTemplate());

        Map<String, String> properties = new HashMap<String, String>();

        properties.put("CLASS", getComponentClassName());
        properties.put("PARENT.CLASS", extendsFaces);
        properties.put("IMPLEMENTS", implementsFaces);

        Set<String> cameliaAttributes = new HashSet<String>();

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.listCameliaAttributes(cameliaAttributes);
        }

        Set<String> imports = new HashSet<String>(this.imports);

        StringBuffer sb = new StringBuffer();
        if (cameliaAttributes.isEmpty()) {
            sb.append("Collections.EMPTY_SET");
        } else {
            boolean first = true;

            imports.add("java.util.HashSet");
            imports.add("java.util.Arrays");

            sb.append("new HashSet(Arrays.asList(new String[] {");
            for (Iterator<String> it = cameliaAttributes.iterator(); it
                    .hasNext();) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append('\"').append(it.next()).append('\"');
            }
            sb.append("}))");
        }
        properties.put("CAMELIA_ATTRIBUTES", sb.toString());

        CharArrayWriter caw = new CharArrayWriter();
        PrintWriter cout = new PrintWriter(caw);
        for (Iterator<FacesField> it = this.fields.iterator(); it.hasNext();) {
            FacesField ff = it.next();

            imports.addAll(ff.listImports());

            ff.writeSource(cout);
        }
        cout.flush();
        if (caw.size() > 0) {
            caw.write("\n");
        }
        properties.put("FIELDS", caw.toString());

        caw = new CharArrayWriter();
        cout = new PrintWriter(caw);
        for (Iterator<String> it = this.methods.iterator(); it.hasNext();) {
            String methodId = it.next();

            Method1_1 method = methods.get(methodId);

            imports.addAll(method.listImports());

            method.writeSource(cout);
        }
        cout.flush();
        properties.put("METHODS", caw.toString());

        imports.remove("javax.faces.context.FacesContext");
        removeImports(imports);
        imports.remove("javax.faces.render.Renderer");
        imports.remove("javax.faces.event.ActionEvent");
        imports.remove("javax.faces.event.FacesEvent");
        imports.remove("javax.faces.event.PhaseId");

        caw = new CharArrayWriter();
        cout = new PrintWriter(caw);
        for (Iterator<String> it = imports.iterator(); it.hasNext();) {
            String importClass = it.next();

            cout.println("import " + importClass + ";");
        }
        cout.flush();

        properties.put("IMPORTS", caw.toString());

        Set<String> ps = new HashSet<String>(bindingAliases.keySet());
        ps.addAll(bindingCases.keySet());

        StringBuffer sbb = new StringBuffer();
        if (ps.isEmpty() == false) {

            boolean first = true;
            for (Iterator<String> it = ps.iterator(); it.hasNext();) {
                String sid = it.next();
                String id = Component1_1.getPropertyName(sid);

                if (first) {
                    sbb.append("if (Properties." + id + ".equals(name)) {\n"); // Pas
                    // de
                    // tabs
                    // !
                    first = false;
                } else {
                    sbb.append("\n\t\t} else if (Properties." + id
                            + ".equals(name)) {\n");
                }

                String alias = bindingAliases.get(sid);
                if (alias != null) {
                    alias = Component1_1.getPropertyName(alias);

                    sbb.append("\t\t\tname=Properties." + alias + ";");
                }

                String code = bindingCases.get(sid);
                if (code != null) {
                    sbb.append("\t\t\t\t" + code.trim() + "\n");
                }
            }
            if (first == false) {
                sbb.append("\t\t}"); // Pas de CR
            }
        }
        properties.put("BINDING_CASES", sbb.toString());

        String buffer = TemplateTools.getContent(url, properties, false);

        out.print(buffer);
    }

    protected String getCameliaBaseComponentTemplate() {
        return "jsf_1_1/CameliaBaseComponent.template";
    }

    protected void removeImports(Set<String> imports) {
        imports.remove("javax.faces.el.ValueBinding");
    }

    public void writeTld(PrintWriter out, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writeTldAttribute(out, componentDoc, attributesDoc);
        }
    }

    public boolean isIgnoreRequired() {
        return ignoreRequired;
    }

    public List<String> listMethods() {
        return methods;
    }

    /**
     * @param out
     */
    public void writeTagGetterAndSetter(PrintWriter out) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writeTagGetterAndSetter(out);
        }
    }

    /**
     * @param out
     */
    public void writeTagVariable(PrintWriter out) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writTagVariable(out);
        }
    }

    /**
     * @param out
     */
    public void writeTagSetProperties(PrintWriter out,
            Map<String, String> properties) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writTagSetProperties(out, properties);
        }
    }

    /**
     * @param out
     */
    public void writeTagRelease(PrintWriter out) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writTagRelease(out);
        }
    }

    public void addGeneratedProperties(Set<String> propertiesAlreadyGenerated) {
        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            propertiesAlreadyGenerated.add(fta.getId());
        }
    }

    public void addGeneratedMethods(Set<String> methodsAlreadyGenerated) {
    }

    public boolean isInputType() {
        return inputType;
    }

    public Identifier getComponentType() {
        return componentType;
    }
}