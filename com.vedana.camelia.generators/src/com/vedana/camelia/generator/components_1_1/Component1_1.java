/*
 * $Id: Component1_1.java,v 1.3 2013/11/14 14:08:48 oeuillot Exp $
 * 
 * $Log: Component1_1.java,v $
 * Revision 1.3  2013/11/14 14:08:48  oeuillot
 * Modification SVG
 *
 * Revision 1.2  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.1  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.4  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
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
 * Revision 1.4  2006/12/18 15:14:02  oeuillot
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
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
import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class Component1_1 {

    // static final String COMPONENT_PACKAGE =
    // CameliaGenerator.CAMELIA_CORE_PACKAGE+ ".component";

    public static final String EVENT_PACKAGE = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".event";

    // static final String TAG_PACKAGE = CameliaGenerator.CAMELIA_CORE_PACKAGE+
    // ".internal.taglib";

    // static final String RENDERER_HTML_PACKAGE =
    // CameliaGenerator.CAMELIA_HTML_PACKAGE+ ".internal.renderer";

    static final String INTERNAL_UTIL_PACKAGE = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".internal.util";

    public static final String FACES_COMPONENT_PACKAGE = CameliaGenerator.CAMELIA_CORE_PACKAGE
            + ".internal.component";

    // private static final String FACELETES_HANDLER_PACKAGE =
    // CameliaGenerator.CAMELIA_CORE_PACKAGE+ ".internal.facelets";

    protected static final Set<String> systemMethods = new HashSet<String>();
    static {
        systemMethods.add("getAsyncRenderer(javax.faces.context.FacesContext)");
    }

    protected final String id;

    protected final Identifier extendsId;

    protected final String componentClassName;

    protected final String componentFileName;

    protected final String rendererClassName;

    protected boolean inputType;

    // private final String rendererFileName;

    protected final String tagClassName;

    protected final String tagFileName;

    protected final List<Capability1_1> capabilities = new ArrayList<Capability1_1>();

    protected final List<Attribute1_1> attributes = new ArrayList<Attribute1_1>();

    protected final List<String> methods = new ArrayList<String>();

    protected final Map<String, Boolean> implementsClass = new HashMap<String, Boolean>();

    protected final Map<String, Method1_1> componentMethods = new HashMap<String, Method1_1>();

    protected boolean abstractComponent = false;

    protected final Set<String> imports = new HashSet<String>();

    protected final Set<String> tagImports = new HashSet<String>();

    protected final String propertyValue;

    protected final Identifier identifier;

    protected final String extendsCapabilitiesId;

    protected String tagExtends;

    protected final boolean render;

    protected boolean ignoreRequired;

    protected boolean converter;

    protected List<Action1_1> actions = new ArrayList<Action1_1>();

    protected Set<String> propertiesAlreadyGenerated = new HashSet<String>();

    protected Set<String> methodsAlreadyGenerated = new HashSet<String>();

    protected List<FacesField> fields = new ArrayList<FacesField>();

    protected List<FacesTagAttribute> tagAttributes = new ArrayList<FacesTagAttribute>();

    protected Set<RenderKit> renderKits = new HashSet<CameliaGenerator.RenderKit>();

    protected RenderKit packageKit;

    protected String tagName;

    protected String template;

    protected String faceletsHandlerClassName;

    protected String faceletsHandlerFileName;

    protected boolean textBodyContent;

    protected Set<String> implementsProperties = new HashSet<String>();

    protected Map<String, String> bindingAliases = new HashMap<String, String>();

    protected Map<String, String> bindingCases = new HashMap<String, String>();

    protected final String componentType;

    public Component1_1(CapabilitiesRepository cr, Element xml,
            Map<String, RenderKit> renderKits, boolean bootStrap,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException {
        id = xml.getAttributeValue("id");
        String extendsIdValue = xml.getAttributeValue("extends");

        String rendererPackage = ((Element) xml.getParent())
                .getAttributeValue("package");
        if (rendererPackage == null) {
            rendererPackage = "core";
        }
        packageKit = renderKits.get(rendererPackage);

        String renderKitName = xml.getAttributeValue("renderKit");
        if (renderKitName == null) {
            renderKitName = "core";
        }
        StringTokenizer st = new StringTokenizer(renderKitName, ", ");
        for (; st.hasMoreTokens();) {
            RenderKit renderKit = renderKits.get(st.nextToken());

            this.renderKits.add(renderKit);
        }

        identifier = new Identifier(packageKit, id);
        componentType = packageKit.componentTypePrefix + "." + id;

        Identifier extendsId = new Identifier(packageKit, extendsIdValue);
        if (extendsIdValue.indexOf(':') < 0) {
            extendsId = searchCore(components, facesComponents, renderKits,
                    extendsId);
        }

        this.extendsId = extendsId;

        extendsCapabilitiesId = xml.getAttributeValue("extendsCapabilities");

        if ("true".equalsIgnoreCase(xml.getAttributeValue("textBodyContent"))) {
            textBodyContent = true;
        }

        if ("true".equalsIgnoreCase(xml.getAttributeValue("abstract"))) {
            abstractComponent = true;
        }

        if ("true".equalsIgnoreCase(xml.getAttributeValue("ignoreRequired"))) {
            ignoreRequired = true;
        }

        if ("true".equalsIgnoreCase(xml.getAttributeValue("converter"))) {
            converter = true;
        }

        if ("false".equalsIgnoreCase(xml.getAttributeValue("render"))) {
            render = false;
        } else {
            render = true;
        }

        tagExtends = xml.getAttributeValue("tagExtends");
        if (tagExtends != null) {
            int idx = tagExtends.lastIndexOf('.');
            if (idx > 0) {
                tagImports.add(tagExtends);
                tagExtends = tagExtends.substring(idx + 1);
            }
        }

        Element propertyValueXml = xml.getFirstChildElement("propertyValue");
        if (propertyValueXml != null) {
            propertyValue = propertyValueXml.getValue();

            // imports.add("javax.faces.component.ValueHolder");
            // imports
            // .add(INTERNAL_UTIL_PACKAGE+".IncompatibleValueException");
            // implementsClass.add("ValueHolder");
        } else {
            propertyValue = null;
        }

        if (bootStrap == false) {
            Elements capabilitiesXml = xml.getChildElements("capability");
            for (int i = 0; i < capabilitiesXml.size(); i++) {
                Element capabilityXml = capabilitiesXml.get(i);

                String capabilityId = capabilityXml.getAttributeValue("id");

                Capability1_1 capability = cr.getCapability(capabilityId,
                        capabilityXml, packageKit);

                capabilities.add(capability);
            }

            Elements attributesXml = xml.getChildElements("attribute");
            for (int i = 0; i < attributesXml.size(); i++) {
                Element attributeXml = attributesXml.get(i);

                attributes.add(cr.getFactory().newAttribute(attributeXml));
            }

            Elements propertiesXml = xml.getChildElements("property");
            for (int i = 0; i < propertiesXml.size(); i++) {
                Element propertyXml = propertiesXml.get(i);

                this.implementsProperties.add(propertyXml.getValue().trim());
            }

            Elements implementsXml = xml.getChildElements("implements");
            for (int i = 0; i < implementsXml.size(); i++) {
                Element implementXml = implementsXml.get(i);

                String rname = implementXml.getAttributeValue("id");
                String r = rname;
                int idx = r.lastIndexOf('.');
                if (idx > 0) {
                    imports.add(r);
                    r = r.substring(idx + 1);
                }

                boolean importMethods = "false".equals(implementXml
                        .getAttributeValue("importMethods")) == false;

                String implementsProperties = implementXml
                        .getAttributeValue("properties");
                if (implementsProperties != null) {
                    StringTokenizer st2 = new StringTokenizer(
                            implementsProperties, ", ");

                    for (; st2.hasMoreTokens();) {
                        this.implementsProperties.add(st2.nextToken());
                    }
                }

                implementsClass.put(r, Boolean.valueOf(importMethods));

                if (importMethods) {
                    importMethods(cr, rname);
                }
            }

            Elements actionsXml = xml.getChildElements("action");
            for (int i = 0; i < actionsXml.size(); i++) {
                Element actionXml = actionsXml.get(i);

                String id = actionXml.getAttributeValue("id");
                String type = actionXml.getAttributeValue("type");
                boolean defaultAction = ("true".equalsIgnoreCase(actionXml
                        .getAttributeValue("default")));

                Action1_1 action = cr.getFactory().newAction(id, type,
                        defaultAction);

                actions.add(action);

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

            Elements methodsXml = xml.getChildElements("method");
            for (int i = 0; i < methodsXml.size(); i++) {
                Element methodXml = methodsXml.get(i);

                if (methodXml.getChildCount() > 0) {
                    Method1_1 method = cr.getFactory().newMethod(methodXml);
                    componentMethods.put(method.getSignature(), method);

                    String sig = method.getSignature();
                    methods.add(sig);
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

            Element constructorXml = xml.getFirstChildElement("template");
            if (constructorXml != null) {
                template = constructorXml.getFirstChildElement("code")
                        .getValue();

                if (template != null) {
                    imports.add("org.rcfaces.core.internal.Constants");
                    imports.add("javax.faces.FacesException");
                }

                Elements importsXML = constructorXml.getChildElements("import");
                for (int i = 0; i < importsXML.size(); i++) {
                    imports.add(importsXML.get(i).getValue());
                }
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

                tagAttributes.add(cr.getFactory().newFacesTagAttribute(attId,
                        type, required, generate));
            }

            Elements fieldsXml = xml.getChildElements("field");
            FacesComponent.parseFields(cr.getFactory(), fields, fieldsXml);
        }

        componentClassName = Character.toUpperCase(id.charAt(0))
                + id.substring(1) + "Component";

        String pkg = packageKit.componentPackage;

        componentFileName = pkg.replace('.', '/') + "/" + componentClassName
                + ".java";

        tagClassName = Character.toUpperCase(id.charAt(0)) + id.substring(1)
                + "Tag";
        tagFileName = packageKit.tagPackage.replace('.', '/') + "/"
                + tagClassName + ".java";

        rendererClassName = Character.toUpperCase(id.charAt(0))
                + id.substring(1) + "Renderer";

        if (actions.isEmpty() == false || textBodyContent) {
            faceletsHandlerClassName = Character.toUpperCase(id.charAt(0))
                    + id.substring(1) + "Handler";

            faceletsHandlerFileName = packageKit.faceletsPackage.replace('.',
                    '/') + "/" + faceletsHandlerClassName + ".java";
        }

        imports.add(packageKit.propertiesPackage + ".Properties");

        tagName = xml.getAttributeValue("tagName");
        if (tagName == null) {
            tagName = getId();
        }

        // imports.add("com.vedana.faces.internal.tools.ComponentTools");
        // imports.add("com.vedana.faces.internal.tools.ValuesTools");
        /*
         * if (implementsClass.contains("ValueHolder")) {
         * methods.add("getConverter()");
         * methods.add("setConverter(javax.faces.convert.Converter)"); }
         */
    }

    private Identifier searchCore(Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents,
            Map<String, RenderKit> renderKits, Identifier original) {

        if (components.containsKey(original)) {
            return original;
        }

        RenderKit renderKit = renderKits.get("core");

        Identifier id = new Identifier(renderKit, original.componentId);
        if (components.containsKey(id)) {
            return id;
        }

        if (facesComponents.containsKey(id)) {
            return id;
        }

        return original;
    }

    public void updateCapabilities(Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents,
            CapabilitiesRepository capabilitiesRepository) {

        if (abstractComponent) {
            Component1_1 parent = components.get(extendsId);

            if (parent == null) {
                FacesComponent facesComponent = facesComponents.get(extendsId);
                if (facesComponent != null) {
                    if (facesComponent.listMethods().contains(
                            "setConverter(String)")) {
                        converter = true;
                    }

                    imports.add(FACES_COMPONENT_PACKAGE + "."
                            + facesComponent.getComponentClassName());

                    if (facesComponent.isInputType()) {
                        inputType = true;
                    }
                }
            }
        }

        if (extendsCapabilitiesId == null) {
            Component1_1 parent = components.get(extendsId);
            if (parent != null) {
                if (parent.abstractComponent) {

                    imports.add(parent.packageKit.componentPackage + "." // INTERNAL
                            + parent.getComponentClassName());
                } else {
                    imports.add(parent.packageKit.componentPackage + "."
                            + parent.getComponentClassName());
                }

                if (parent.isInputType()) {
                    inputType = true;
                }
                // System.out.println(id + " => " + inputType);

            } else {
                FacesComponent facesComponent = facesComponents.get(extendsId);
                if (facesComponent != null) {
                    // C'est forcement un interne
                    imports.add(FACES_COMPONENT_PACKAGE + "."
                            + facesComponent.getComponentClassName());

                    if (facesComponent.isInputType()) {
                        inputType = true;
                    }
                    // System.out.println(id + " => " + inputType);
                }
            }

            return;
        }

        Set<Capability1_1> capabilites = new HashSet<Capability1_1>(
                this.capabilities);

        for (StringTokenizer st = new StringTokenizer(extendsCapabilitiesId,
                ",; "); st.hasMoreTokens();) {
            String token = st.nextToken();

            Component1_1 component;

            Identifier id = new Identifier(identifier.renderKitId, token);
            component = components.get(id);
            if (component == null) {
                id = new Identifier("core", token);
                component = components.get(id);
            }

            if (component == null) {
                throw new NullPointerException("Can not find component '"
                        + token + "'");
            }

            capabilites.addAll(component.listCapabilites());

            if (component.ignoreRequired) {
                this.ignoreRequired = true;
            }
            if (component.isInputType()) {
                inputType = true;
            }
        }
        // System.out.println(id + " => " + inputType);

        for (Iterator<Capability1_1> it = capabilites.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            Capability1_1 parents[] = capability
                    .getParentCapability(capabilitiesRepository);

            if (parents == null || parents.length < 1) {
                continue;
            }

            boolean cont = true;

            for (int i = 0; i < parents.length; i++) {
                if (capabilites.remove(parents[i]) == false) {
                    continue;
                }

                cont = false;
            }

            if (cont == true) {
                continue;
            }

            it = capabilites.iterator();
        }

        this.capabilities.clear();
        this.capabilities.addAll(capabilites);

        Set<String> methods = new HashSet<String>(this.methods);
        Map<String, Method1_1> cmethods = new HashMap<String, Method1_1>(
                this.componentMethods);

        for (StringTokenizer st = new StringTokenizer(extendsCapabilitiesId,
                ",; "); st.hasMoreTokens();) {
            String token = st.nextToken();

            Component1_1 component;

            Identifier id = new Identifier(identifier.renderKitId, token);
            component = components.get(id);
            if (component == null) {
                id = new Identifier("core", token);
                component = components.get(id);
            }

            if (component == null) {
                throw new NullPointerException("Can not find component '"
                        + token + "'");
            }

            methods.addAll(component.listMethods());
            cmethods.putAll(component.listComponentMethods());
        }

        this.methods.clear();
        this.methods.addAll(methods);
        this.componentMethods.clear();
        this.componentMethods.putAll(cmethods);

        Set<Attribute1_1> attributes = new HashSet<Attribute1_1>(
                this.attributes);

        for (StringTokenizer st = new StringTokenizer(extendsCapabilitiesId,
                ",; "); st.hasMoreTokens();) {
            String token = st.nextToken();

            Component1_1 component;

            Identifier id = new Identifier(identifier.renderKitId, token);
            component = components.get(id);
            if (component == null) {
                id = new Identifier("core", token);
                component = components.get(id);
            }

            if (component == null) {
                throw new NullPointerException("Can not find component '"
                        + token + "'");
            }

            attributes.addAll(component.listAttributes());
        }

        this.attributes.clear();
        this.attributes.addAll(attributes);

        Map<String, Boolean> impls = new HashMap<String, Boolean>(
                this.implementsClass);

        for (StringTokenizer st = new StringTokenizer(extendsCapabilitiesId,
                ",; "); st.hasMoreTokens();) {
            String token = st.nextToken();

            Component1_1 component;

            Identifier id = new Identifier(identifier.renderKitId, token);
            component = components.get(id);
            if (component == null) {
                id = new Identifier("core", token);
                component = components.get(id);
            }

            if (component == null) {
                throw new NullPointerException("Can not find component '"
                        + token + "'");
            }

            impls.putAll(component.listImplementsClasses());
        }

        for (Iterator<FacesField> it = fields.iterator(); it.hasNext();) {
            FacesField ff = it.next();

            imports.addAll(ff.listImports());
        }

        this.implementsClass.clear();
        this.implementsClass.putAll(impls);

        Set<String> imports = new HashSet<String>(this.imports);

        for (StringTokenizer st = new StringTokenizer(extendsCapabilitiesId,
                ",; "); st.hasMoreTokens();) {
            String token = st.nextToken();

            Component1_1 component;

            Identifier id = new Identifier(identifier.renderKitId, token);
            component = components.get(id);
            if (component == null) {
                id = new Identifier("core", token);
                component = components.get(id);
            }

            if (component == null) {
                throw new NullPointerException("Can not find component '"
                        + token + "'");
            }

            imports.addAll(component.listImports());
        }

        this.imports.clear();
        this.imports.addAll(imports);
    }

    private Collection<String> listImports() {
        return imports;
    }

    private Map<String, Boolean> listImplementsClasses() {
        return implementsClass;
    }

    private Map<String, Method1_1> listComponentMethods() {
        return componentMethods;
    }

    private Collection<Attribute1_1> listAttributes() {
        return attributes;
    }

    public List<Capability1_1> listCapabilites() {
        return capabilities;
    }

    public List<String> listMethods() {
        return methods;
    }

    public String getId() {
        return id;
    }

    public String getComponentClassName() {
        return componentClassName;
    }

    public String getComponentFileName() {
        return componentFileName;
    }

    public boolean isIgnoreRequired(Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents) {
        if (ignoreRequired) {
            return true;
        }

        if (extendsId == null) {
            return false;
        }

        Component1_1 parent = components.get(extendsId);
        if (parent != null) {
            return parent.isIgnoreRequired(components, facesComponents);
        }

        FacesComponent fc = facesComponents.get(extendsId);

        return fc.isIgnoreRequired();
    }

    public void writeBootStrap(PrintWriter out) {

        String packageId = packageKit.componentPackage;

        out.println("package " + packageId + ";");
        out.println();

        out.println("public class " + getComponentClassName() + " {");
        out.println("}");

    }

    public void writeSource(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<String, String> events, Map<String, Method1_1> methods,
            Map<Identifier, FacesComponent> facesComponents,
            Map<String, ComponentDoc> componentsDoc,
            Map<String, AttributeDoc> attributesDoc) {
        Component1_1 parent = null;
        String parentClassName = null;

        Map<String, String> properties = packageKit.properties;

        if (implementsProperties.isEmpty() == false) {
            for (Iterator<String> it = implementsProperties.iterator(); it
                    .hasNext();) {
                String propertyName = it.next();

                String pn = Component1_1.getPropertyName(propertyName);

                properties.put(pn, CameliaGenerator.PROPERTY_PREFIX
                        + Component1_1.lowerCaseFirstChar(propertyName));
            }
        }

        ComponentDoc componentDoc = componentsDoc.get(tagName);

        if (extendsId != null) {
            parent = components.get(extendsId);
            if (parent != null) {
                parentClassName = parent.getComponentClassName();
                parent.addGeneratedProperties(propertiesAlreadyGenerated,
                        components, facesComponents);
                parent.addGeneratedMethods(methodsAlreadyGenerated, components,
                        facesComponents);

            } else {
                FacesComponent fc = facesComponents.get(extendsId);
                fc.addGeneratedProperties(propertiesAlreadyGenerated);

                parentClassName = fc.getComponentClassName();
            }
        } else {
            System.err.println("*** No extension ! " + id);
            return;
        }

        // System.err.println("Component:" + getId() + " : "+
        // propertiesAlreadyGenerated);

        String packageId = packageKit.componentPackage;

        out.println("package " + packageId + ";");
        out.println();

        Set<String> imports = new HashSet<String>(this.imports);

        imports.add("java.util.Set");
        imports.add("java.util.HashSet");
        imports.add("java.util.Arrays");
        imports.add("org.apache.commons.logging.Log");
        imports.add("org.apache.commons.logging.LogFactory");

        if (capabilities.size() > 0 || attributes.size() > 0
                || bindingAliases.size() > 0) {
            imports.add("javax.faces.el.ValueBinding");
        }

        if (template != null) {
            imports.add("org.rcfaces.core.internal.component.IInitializationState");
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            imports.addAll(capability.listImports(methods));
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attributes = it.next();

            imports.addAll(attributes.listImports());
        }

        for (Iterator<String> it = this.methods.iterator(); it.hasNext();) {
            String methodId = it.next();
            Method1_1 method = componentMethods.get(methodId);
            if (method == null) {
                method = methods.get(methodId);
            }

            if (method == null) {
                System.err.println("Method: " + methodId + " is not found !");
                continue;
            }
            imports.addAll(method.listImports());
        }

        for (Iterator<String> it = imports.iterator(); it.hasNext();) {
            String methodName = it.next();

            out.println("import " + methodName + ";");
        }

        out.println();

        if (componentDoc != null) {
            String doc = CameliaGenerator.formatComment(componentDoc.getDoc(),
                    0);

            if (doc != null && doc.length() > 0) {
                out.println("/**");
                out.println(doc);
                out.println(" */");
            }
        }

        out.print("public");
        if (abstractComponent) {
            out.print(" abstract");
        }
        out.print(" class " + getComponentClassName() + " extends "
                + parentClassName);
        Iterator<Capability1_1> it1 = capabilities.iterator();
        Iterator<Map.Entry<String, Boolean>> it2 = implementsClass.entrySet()
                .iterator();
        if (it1.hasNext() || it2.hasNext()) {
            out.println(" implements ");

            for (; it1.hasNext();) {
                Capability1_1 capability = it1.next();

                out.print("\t" + capability.getClassNameWithoutPackage());
                if (it1.hasNext() || it2.hasNext()) {
                    out.println(",");
                }
            }
            for (; it2.hasNext();) {
                Map.Entry<String, Boolean> entry = it2.next();

                String claz = entry.getKey();

                out.print("\t" + claz);
                if (it2.hasNext()) {
                    out.println(",");
                }
            }
        }
        out.println(" {\n");

        out.println("\tprivate static final Log LOG = LogFactory.getLog("
                + getComponentClassName() + ".class);\n");

        if (abstractComponent == false) {
            out.println("\tpublic static final String COMPONENT_TYPE=\""
                    + componentType + "\";\n");
        }

        writeStaticFieldsSource(out, parentClassName);

        writeFacesFieldSource(out);

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            attribute.writeSourceVariable(out);
        }

        Set<String> cameliaAttributes = listCameliaAttributes();

        if (cameliaAttributes.isEmpty() == false) {
            out.println("\tprotected static final Set CAMELIA_ATTRIBUTES=new HashSet("
                    + parentClassName + ".CAMELIA_ATTRIBUTES);");
            out.println("\tstatic {");
            out.print("\t\tCAMELIA_ATTRIBUTES.addAll(Arrays.asList(new String[] {");
            boolean first = true;
            for (Iterator<String> it = cameliaAttributes.iterator(); it
                    .hasNext();) {
                String att = it.next();

                if (first == false) {
                    out.print(",");
                } else {
                    first = false;
                }
                out.print("\"" + att + "\"");
            }
            out.println("}));");
            out.println("\t}");
        }

        if (propertyValue != null) {
            out.print("\tprotected static final String CAMELIA_VALUE_ALIAS=");
            out.print('\"');
            out.print(propertyValue);
            out.println("\";");

        } else {
            // out.println(parentClassName + ".CAMELIA_VALUE;");
        }

        out.println();

        if (abstractComponent == false) {

            out.println("\tpublic " + getComponentClassName() + "() {");
            // out.println("\t\tsuper();");
            if (abstractComponent == false && render) {
                out.println("\t\tsetRendererType(COMPONENT_TYPE);");

            } else {
                out.println("\t\tsetRendererType(null);");
            }
            if (template != null) {
                out.println("\t\tif (Constants.TEMPLATE_ENGINE_SUPPORT==false) {");
                out.println("\t\t\tthrow new FacesException(\"Template Support must be enabled to use this component !\");");
                out.println("\t\t}");
            }

            out.println("\t}");

            out.println();

            out.println("\tpublic " + getComponentClassName()
                    + "(String componentId) {");
            out.println("\t\tthis();");
            out.println("\t\tsetId(componentId);");
            out.println("\t}");
        }

        out.println();

        if (template != null) {
            out.println("\tprotected boolean isTemplateComponent(IInitializationState state) {");
            out.println("\t\treturn true;");
            out.println("\t}");

            out.println();

            out.println("\tprotected void constructTemplate(IInitializationState state) {");
            out.println("\t\tsuper.constructTemplate(state);");
            out.println(template);
            out.println("\t}");

            out.println();
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.writeComponentFields(out, methods);
        }

        methodsAlreadyGenerated.addAll(systemMethods);

        for (Iterator<String> it = this.methods.iterator(); it.hasNext();) {
            String methodId = it.next();

            Method1_1 method = componentMethods.get(methodId);
            if (method == null) {
                method = methods.get(methodId);
            }

            if (method == null) {
                System.err.println("PROBLEME ! can not find " + methodId
                        + " component=" + getId());
                continue;
            }

            if (methodsAlreadyGenerated.add(method.getSignature()) == false) {
                continue;
            }

            method.writeSource(out);
        }

        boolean ignoreRequired = isIgnoreRequired(components, facesComponents);

        Set<String> props = new HashSet<String>(propertiesAlreadyGenerated);

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.writeSource(out, properties, events, methods,
                    propertyValue, methodsAlreadyGenerated, ignoreRequired,
                    props, inputType);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            attribute
                    .writeSource(out, properties, events,
                            methodsAlreadyGenerated, props, componentDoc,
                            attributesDoc);
        }

        /*
         * out.println("\tpublic void release() {"); for (it =
         * capabilities.iterator(); it.hasNext();) { Capability capability =
         * (Capability) it.next();
         * 
         * capability.writeComponentRelease(out, methods); }
         * out.println("\t\tsuper.release();"); out.println("\t}");
         */

        writeCameliaFields(out);

        Set<String> ps = new HashSet<String>(bindingAliases.keySet());
        ps.addAll(bindingCases.keySet());

        if (ps.isEmpty() == false) {

            out.println();
            out.println("\tpublic void setValueBinding(String name, ValueBinding binding) {");

            boolean first = true;
            for (Iterator<String> it = ps.iterator(); it.hasNext();) {
                String sid = it.next();
                String id = Component1_1.getPropertyName(sid);

                if (first) {
                    out.println("\t\tif (Properties." + id + ".equals(name)) {");
                    first = false;
                } else {
                    out.println();
                    out.println("\t\t} else if (Properties." + id
                            + ".equals(name)) {");
                }

                String alias = bindingAliases.get(sid);
                if (alias != null) {
                    alias = Component1_1.getPropertyName(alias);

                    out.println("\t\t\tname=Properties." + alias + ";");
                }

                String code = bindingCases.get(sid);
                if (code != null) {
                    out.println("\t\t\t" + code);
                }
            }
            if (first == false) {
                out.println("\t\t}");
            }

            out.println("\t\tsuper.setValueBinding(name, binding);");
            out.println("\t}");
        }

        writeSourceFooter(out);

        /*
         * if (propertyValue != null) { String pv = propertyValue;
         * 
         * if (properties.containsKey(pv) == false) { properties.put(pv,
         * "camelia." + pv.toLowerCase()); }
         * 
         * out.println("\tpublic final Object getValue() {");
         * out.println("\t\treturn engine.getValue(Properties." + pv + ",
         * null);"); out.println("\t}\n");
         * 
         * out.println("\tpublic final Object getLocalValue() {");
         * out.println("\t\treturn engine.getLocalValue(Properties." + pv +
         * ");"); out.println("\t}\n");
         * 
         * out.println("\tpublic final void setValue(Object value) {"); out
         * .println("\t\tif (value==null || (value instanceof ValueBinding))
         * {"); out.println("\t\t\tengine.setValue(Properties." + pv + ",
         * value);"); out.println("\t\t\treturn;"); out.println("\t\t}\n");
         * 
         * for (it = capabilities.iterator(); it.hasNext();) { Capability
         * capability = (Capability) it.next();
         * 
         * String type = capability.getPropertyValueType(pv); if (type == null)
         * { continue; }
         * 
         * if ("int".equals(type)) { type = "Integer"; } else if
         * ("long".equals(type)) { type = "Long"; } else if
         * ("double".equals(type)) { type = "Double"; } else if
         * ("boolean".equals(type)) { type = "Boolean"; }
         * 
         * out .println("\t\tif ((value instanceof " + type + ")==false) {");
         * out .println("\t\t\tthrow new IncompatibleValueException(value, \"" +
         * type + "\");"); out.println("\t\t}\n"); }
         * 
         * out.println("\t\tengine.setValue(Properties." + pv + ", value);");
         * out.println("\t}\n"); }
         */
        out.println("}");
    }

    protected void writeStaticFieldsSource(PrintWriter out,
            String parentClassName) {
    }

    protected void writeFacesFieldSource(PrintWriter out) {

        for (Iterator<FacesField> it = fields.iterator(); it.hasNext();) {
            FacesField ff = it.next();

            ff.writeSource(out);
        }
    }

    protected void writeSourceFooter(PrintWriter out) {
    }

    protected void writeCameliaFields(PrintWriter out) {

        out.println("\tprotected Set getCameliaFields() {");
        out.println("\t\treturn CAMELIA_ATTRIBUTES;");
        out.println("\t}");

        if (propertyValue != null) {
            out.println();
            out.println("\tprotected String getCameliaValueAlias() {");
            out.println("\t\treturn CAMELIA_VALUE_ALIAS;");
            out.println("\t}");
        }
    }

    protected void addGeneratedProperties(
            Set<String> propertiesAlreadyGenerated,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents) {
        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.addGeneratedProperties(propertiesAlreadyGenerated);
        }

        if (extendsId != null) {
            Component1_1 parent = components.get(extendsId);
            if (parent != null) {
                parent.addGeneratedProperties(propertiesAlreadyGenerated,
                        components, facesComponents);

            } else {
                FacesComponent fc = facesComponents.get(extendsId);
                fc.addGeneratedProperties(propertiesAlreadyGenerated);
            }
        }
    }

    protected void addGeneratedMethods(Set<String> methodsAlreadyGenerated,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents) {
        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.addGeneratedMethods(methodsAlreadyGenerated);
        }

        if (extendsId != null) {
            Component1_1 parent = components.get(extendsId);
            if (parent != null) {
                parent.addGeneratedMethods(methodsAlreadyGenerated, components,
                        facesComponents);

            } else {
                FacesComponent fc = facesComponents.get(extendsId);
                fc.addGeneratedMethods(methodsAlreadyGenerated);
            }
        }
    }

    public void writeTag(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<String, Method1_1> methods,
            Map<Identifier, FacesComponent> facesComponents) {
        Component1_1 parent = null;
        String parentClassName = null;
        FacesComponent fc = null;

        parent = components.get(extendsId);

        if (tagExtends != null) {
            parentClassName = tagExtends;

        } else if (parent != null) {
            parentClassName = parent.getTagClassName();

            if (parent.packageKit.tagPackage.equals(packageKit.tagPackage) == false) {
                tagImports.add(parent.packageKit.tagPackage + "."
                        + parent.getTagClassName());
            }

        } else {
            fc = facesComponents.get(extendsId);
            parentClassName = "CameliaTag";
        }

        out.println("package " + packageKit.tagPackage + ";");
        out.println();

        if (capabilities.size() > 0 || attributes.size() > 0
                || tagAttributes.size() > 0 || actions.size() > 0) {
            tagImports.add("javax.faces.el.ValueBinding");
            tagImports.add("javax.faces.application.Application");
            tagImports.add("javax.faces.context.FacesContext");
            tagImports.add("javax.faces.component.UIComponent");
        }
        if (textBodyContent) {
            tagImports
                    .add("org.rcfaces.core.component.capability.ITextCapability");
            tagImports.add("javax.servlet.jsp.JspException");
        }

        tagImports.add(packageKit.componentPackage + "." // INTERNAL
                + getComponentClassName());

        tagImports.add("org.apache.commons.logging.Log");
        tagImports.add("org.apache.commons.logging.LogFactory");
        tagImports.add("javax.servlet.jsp.tagext.Tag");
        tagImports.add("javax.faces.component.UIViewRoot");
        tagImports.add("org.rcfaces.core.internal.tools.ListenersTools");
        tagImports.add("org.rcfaces.core.internal.tools.ListenersTools1_1");
        tagImports.add(packageKit.propertiesPackage + ".Properties");

        if (packageKit.tagPackage.equals("org.rcfaces.core.internal.taglib") == false) {
            tagImports.add("org.rcfaces.core.internal.taglib.CameliaTag");
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            tagImports.addAll(capability.listTagImports());
        }

        for (Iterator<String> it = tagImports.iterator(); it.hasNext();) {
            String importName = it.next();

            out.println("import " + importName + ";");
        }

        out.println();
        out.print("public");
        if (abstractComponent) {
            out.print(" abstract");
        }
        out.println(" class " + getTagClassName() + " extends "
                + parentClassName + " implements Tag {\n");

        out.println();

        out.println("\tprivate static final Log LOG=LogFactory.getLog("
                + getTagClassName() + ".class);");
        out.println();

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.writeTagVariable(out, methods,
                    propertiesAlreadyGenerated);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            attribute.writeTagVariable(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_1 action = it.next();

            action.writeTagVariable(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writTagVariable(out);
        }

        if (fc != null) {
            fc.writeTagVariable(out);
        }

        if (converter) {
            out.println("\tprivate String converter;");
            out.println();
        }

        if (abstractComponent == false) {
            out.println("\tpublic String getComponentType() {");
            out.println("\t\treturn " + getComponentClassName()
                    + ".COMPONENT_TYPE;");
            out.println("\t}\n");
        }

        Set<String> props = new HashSet<String>(propertiesAlreadyGenerated);

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.writeTagGetterAndSetter(out, methods, propertyValue,
                    props);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            attribute.writeTagGetterAndSetter(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_1 action = it.next();

            action.writeTagGetterAndSetter(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writeTagGetterAndSetter(out);
        }

        if (fc != null) {
            fc.writeTagGetterAndSetter(out);
        }

        if (converter) {
            out.println("\tpublic final void setConverter(String converter) {");
            out.println("\t\tthis.converter=converter;");
            out.println("\t}\n");
            out.println("\tpublic final String getConverter() {");
            out.println("\t\treturn converter;");
            out.println("\t}\n");
        }

        Iterator<Capability1_1> it1 = capabilities.iterator();
        Iterator<Attribute1_1> it2 = attributes.iterator();
        Iterator<Action1_1> it3 = actions.iterator();
        Iterator<FacesTagAttribute> it4 = tagAttributes.iterator();

        if (it1.hasNext() || it2.hasNext() || it3.hasNext() || it4.hasNext()
                || converter) {
            out.println("\tprotected void setProperties(UIComponent uiComponent) {");

            out.println("\t\tif (LOG.isDebugEnabled()) {");
            if (abstractComponent == false) {
                out.println("\t\t\tif (" + getComponentClassName()
                        + ".COMPONENT_TYPE==getComponentType()) {");
                out.println("\t\t\t\tLOG.debug(\"Component id='\"+getId()+\"' type='\"+getComponentType()+\"'.\");");
                out.println("\t\t\t}");
            }

            if (it1.hasNext()) {
                for (Iterator<Capability1_1> itl = capabilities.iterator(); itl
                        .hasNext();) {
                    Capability1_1 capability = itl.next();

                    capability.writeTagLogProperties(out, methods,
                            propertyValue, propertiesAlreadyGenerated);
                }
            }

            if (it2.hasNext()) {
                for (Iterator<Attribute1_1> itl = attributes.iterator(); itl
                        .hasNext();) {
                    Attribute1_1 attribute = itl.next();

                    attribute.writeTagLogProperties(out);
                }
            }

            if (it3.hasNext()) {
                for (Iterator<Action1_1> itl = actions.iterator(); itl
                        .hasNext();) {
                    Action1_1 attribute = itl.next();

                    attribute.writeTagLogProperties(out);
                }
            }

            if (it4.hasNext()) {
                for (Iterator<FacesTagAttribute> itl = tagAttributes.iterator(); itl
                        .hasNext();) {
                    FacesTagAttribute fta = itl.next();

                    fta.writeTagLogProperties(out);
                }
            }

            out.println("\t\t}");

            out.println("\t\tif ((uiComponent instanceof "
                    + getComponentClassName() + ")==false) {");
            out.println("\t\t\tif (uiComponent instanceof UIViewRoot) {");
            out.println("\t\t\t\tthrow new IllegalStateException(\"The first component of the page must be a UIViewRoot component !\");");
            out.println("\t\t\t}");

            out.println("\t\t\tthrow new IllegalStateException(\"Component specified by tag is not instanceof of '"
                    + getComponentClassName() + "'.\");");
            out.println("\t\t}\n");

            out.println("\t\tsuper.setProperties(uiComponent);\n");

            out.println("\t\t" + getComponentClassName() + " component = ("
                    + getComponentClassName() + ") uiComponent;");
            out.println("\t\tFacesContext facesContext = getFacesContext();");
            out.println("\t\tApplication application = facesContext.getApplication();");

            // out.println();
            // out.println("\t\tcomponent.clearListeners();");

            for (; it1.hasNext();) {
                Capability1_1 capability = it1.next();

                capability.writeTagSetProperties(out, methods, propertyValue,
                        propertiesAlreadyGenerated);
            }

            for (; it2.hasNext();) {
                Attribute1_1 attribute = it2.next();

                attribute.writeTagSetProperties(out, propertyValue);
            }

            for (; it3.hasNext();) {
                Action1_1 attribute = it3.next();

                attribute.writeTagSetProperties(out);
            }

            for (; it4.hasNext();) {
                FacesTagAttribute fta = it4.next();

                fta.writTagSetProperties(out, packageKit.properties);
            }

            if (fc != null) {
                fc.writeTagSetProperties(out, packageKit.properties);
            }

            if (converter) {
                out.println("\tif (converter != null) {");
                out.println("\t\tif (isValueReference(converter)) {");
                out.println("\t\t\tValueBinding vb = application.createValueBinding(converter);");
                out.println("\t\t\tcomponent.setValueBinding(Properties.CONVERTER, vb);");
                out.println("\t\t} else {");
                out.println("\t\t\tcomponent.setConverter(application.createConverter(converter));");
                out.println("\t\t}");
                out.println("\t}");
            }

            out.println("\t}\n");
        }

        out.println("\tpublic void release() {");

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability
                    .writeTagRelease(out, methods, propertiesAlreadyGenerated);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            /*
             * if (attribute.getType().equals("Object[]")) { continue; }
             */

            attribute.writeTagRelease(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_1 action = it.next();

            action.writeTagRelease(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writTagRelease(out);
        }

        if (fc != null) {
            fc.writeTagRelease(out);
        }

        if (converter) {
            out.println("\t\tconverter = null;");
        }

        out.println("\n\t\tsuper.release();");
        out.println("\t}\n");

        if (textBodyContent) {

            out.println("\tprotected int getDoStartValue() {");
            out.println("\t\treturn EVAL_BODY_BUFFERED;");
            out.println("\t}");
            out.println();
            out.println("\tpublic int doEndTag() throws JspException {");
            out.println("\t\tif (text == null && getBodyContent() != null) {");
            out.println("\t\t\tString content = getBodyContent().getString();");
            out.println("\t\t\tif (content != null && content.length() > 0) {");
            out.println("\t\t\t\tcontent = content.trim();");
            out.println("\t\t\t\tif (content.length() > 0) {");
            out.println("\t\t\t\t\tif (LOG.isDebugEnabled()) {");
            out.println("\t\t\t\t\t\tLOG.debug(\"  [body of tag] text='\"+content+\"'\");");
            out.println("\t\t\t\t\t}");

            out.println("\t\t\t\t\t((ITextCapability)getComponentInstance()).setText(content);");
            out.println("\t\t\t\t}");
            out.println("\t\t\t}");
            out.println("\t\t}");
            out.println("\t\treturn super.doEndTag();");
            out.println("\t}");

        }

        out.println("}");

    }

    public String getTagClassName() {
        return tagClassName;
    }

    public static String lowerCaseFirstChar(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static String getPropertyName(String name) {
        StringBuffer sb = new StringBuffer();

        char chs[] = name.toCharArray();
        boolean upper = true;
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (Character.isLetter(c) == false) {
                upper = false;
                sb.append(chs);
                continue;
            }

            if (Character.isLowerCase(c)) {
                upper = false;
                sb.append(Character.toUpperCase(c));
                continue;
            }

            if (upper) {
                sb.append(c);
                continue;
            }
            upper = true;
            sb.append("_" + c);
        }

        return sb.toString();
    }

    public String getTagFileName() {
        return tagFileName;
    }

    protected Set<String> listCameliaAttributes() {
        Set<String> set = new HashSet<String>();

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability.listCameliaAttributes(set);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            attribute.listCameliaAttributes(set);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_1 action = it.next();

            action.listCameliaAttributes(set);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.listCameliaAttributes(set);
        }

        return set;
    }

    public void writeTld(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponent,
            Map<String, ComponentDoc> componentsDoc,
            Map<String, AttributeDoc> attributesDoc) {

        if (abstractComponent) {
            return;
        }

        CameliaGenerator.componentsAutodoc.add(tagName);

        out.println("\t<tag>");

        ComponentDoc componentDoc = componentsDoc.get(tagName);
        if (componentDoc != null) {
            writeDocBeforeName(componentDoc, out);
        }

        out.println("\t\t<name>" + tagName + "</name>");
        out.println("\t\t<tag-class>" + packageKit.tagPackage + "."
                + getTagClassName() + "</tag-class>");
        out.println("\t\t<tei-class>org.rcfaces.core.internal.taglib.CameliaTagExtraInfo</tei-class>");
        out.println("\t\t<body-content>JSP</body-content>");

        if (componentDoc != null) {
            writeDocAfterName(componentDoc, out);
        }

        out.println("\t\t<attribute>");

        AttributeDoc attDoc = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, "id");
        if (attDoc != null) {
            writeDocBeforeName(attDoc, out);
        }

        out.println("\t\t\t<name>id</name>");
        // out.println("\t\t\t<required>false</required>");
        // out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
        writeTldAttributes(out, "id");

        if (attDoc != null) {
            writeDocAfterName(attDoc, out);
        }

        out.println("\t\t</attribute>");
        CameliaGenerator.attributesAutodoc.add("id");

        out.println("\t\t<attribute>");

        attDoc = ComponentDoc.getAttributeDoc(componentDoc, attributesDoc,
                "binding");
        if (attDoc != null) {
            writeDocBeforeName(attDoc, out);
        }

        out.println("\t\t\t<name>binding</name>");
        // out.println("\t\t\t<required>false</required>");
        // out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
        writeTldAttributes(out, "binding");

        if (attDoc != null) {
            writeDocAfterName(attDoc, out);
        }

        out.println("\t\t</attribute>");
        CameliaGenerator.attributesAutodoc.add("binding");

        writeTldAttributes(out, components, facesComponent, componentDoc,
                attributesDoc);

        out.println("\t</tag>");
    }

    protected void writeDocBeforeName(AttributeDoc att, PrintWriter out) {
    }

    protected void writeDocAfterName(AttributeDoc att, PrintWriter out) {
        att.writeTld(out);
    }

    protected void writeDocBeforeName(ComponentDoc componentDoc, PrintWriter out) {
    }

    protected void writeDocAfterName(ComponentDoc componentDoc, PrintWriter out) {
        componentDoc.writeTld(out);
    }

    protected void writeTldAttributes(PrintWriter out, String attributeName) {
        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");
    }

    public void writeTldAttributes(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents,
            ComponentDoc componentDoc, Map<String, AttributeDoc> attributesDoc) {

        boolean converter = this.methods
                .contains("setConverter([java.lang.String;)");
        FacesComponent fc = null;

        if (extendsId != null) {
            Component1_1 parent = components.get(extendsId);

            if (parent != null) {
                parent.writeTldAttributes(out, components, facesComponents,
                        componentDoc, attributesDoc);

            } else {
                fc = facesComponents.get(extendsId);
                if (fc.listMethods().contains(
                        "setConverter([java.lang.String;)")) {
                    converter = true;
                }
            }
        }

        if (fc != null) {
            fc.writeTld(out, componentDoc, attributesDoc);
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_1 capability = it.next();

            capability
                    .writeTld(out, propertyValue, componentDoc, attributesDoc);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_1 attribute = it.next();

            /*
             * if (attribute.getType().equals("Object[]")) { continue; }
             */

            attribute.writeTld(out, componentDoc, attributesDoc);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_1 action = it.next();

            action.writeTld(out, componentDoc, attributesDoc);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute fta = it.next();

            fta.writeTldAttribute(out, componentDoc, attributesDoc);
        }

        if (converter) {
            out.println("\t\t<attribute>");

            AttributeDoc attDoc = ComponentDoc.getAttributeDoc(componentDoc,
                    attributesDoc, "converter");
            if (attDoc != null) {
                writeDocBeforeName(attDoc, out);
            }

            out.println("\t\t\t<name>converter</name>");
            out.println("\t\t\t<required>false</required>");

            writeTldAttributes(out, "converter");

            if (attDoc != null) {
                writeDocAfterName(attDoc, out);
            }

            out.println("\t\t</attribute>");
            CameliaGenerator.attributesAutodoc.add("converter");
        }
    }

    public void writeFacesConfig(PrintWriter out,
            Map<String, ComponentDoc> componentsDoc,
            Map<String, AttributeDoc> attributesDoc) {
        if (abstractComponent) {
            return;
        }

        out.println("\t<component>");

        ComponentDoc componentDoc = componentsDoc.get(tagName);
        if (componentDoc != null) {
            componentDoc.writeFacesConfig(out);
        }

        out.println("\t\t<component-type>" + componentType
                + "</component-type>");
        out.println("\t\t<component-class>" + packageKit.componentPackage + "."
                + getComponentClassName() + "</component-class>");

        if (componentDoc != null) {
            for (Attribute1_1 attribute : attributes) {
                attribute.writeFacesConfig(out, componentDoc, attributesDoc);
            }
        }

        out.println("\t</component>");
    }

    public void writeFacesConfigRenderer(PrintWriter out, RenderKit renderKit) {
        if (abstractComponent || render == false) {
            return;
        }

        out.println("\t\t<renderer>");
        out.println("\t\t\t<component-family>rcfaces</component-family>");
        out.println("\t\t\t<renderer-type>" + componentType
                + "</renderer-type>");

        out.println("\t\t\t<renderer-class>" + renderKit.renderKitPackage + "."
                + rendererClassName + "</renderer-class>");
        out.println("\t\t</renderer>");
    }

    private void importMethods(CapabilitiesRepository cr, String interfaceName) {
        System.err.println("Import methods of " + interfaceName);
        try {
            Class< ? > cl = cr.loadClass(interfaceName);

            BeanInfo beanInfo = Introspector.getBeanInfo(cl);

            MethodDescriptor mds[] = beanInfo.getMethodDescriptors();

            for (int i = 0; i < mds.length; i++) {
                String sig = Method1_1.getSignature(mds[i].getMethod());

                methods.add(sig);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeFaceletComponent(PrintWriter out,
            Map<Identifier, Component1_1> components) {

        if (abstractComponent) {
            return;
        }

        out.println("\t<tag>");
        out.println("\t\t<tag-name>" + tagName + "</tag-name>");
        out.println("\t\t<component>");
        out.println("\t\t\t<component-type>" + componentType
                + "</component-type>");

        if (render) {
            out.println("\t\t\t<renderer-type>" + componentType
                    + "</renderer-type>");
        }

        out.println("\t\t\t<handler-class>"
                + getFaceletsHandlerClassName(components) + "</handler-class>");

        out.println("\t\t</component>");
        out.println("\t</tag>");
    }

    public void writeFaceletsHandler(File cFile, Component1_1 component,
            Map<Identifier, Component1_1> components) throws IOException {
        FileWriter fout = new FileWriter(cFile);
        PrintWriter out = new PrintWriter(fout);

        String parentClassName = null;

        if (extendsId != null) {
            Component1_1 parent = components.get(extendsId);
            if (parent != null) {
                parentClassName = parent.getFaceletsHandlerFileName();
            }
        }

        if (parentClassName == null) {
            if (packageKit.getName().equals("core")) {
                parentClassName = "CameliaComponentHandler";
            } else {
                parentClassName = "org.rcfaces.core.internal.facelets.CameliaComponentHandler";
            }
        }

        out.println("package " + packageKit.faceletsPackage + ";");
        out.println();

        out.println("import org.rcfaces.core.internal.tools."
                + getListenersToolsClassName() + ";");
        out.println("import org.rcfaces.core.internal.tools.ListenersTools.IListenerType;\n");

        writeFaceletsHandlerImports(out);

        if (textBodyContent) {
            writeFaceletsTextHandlerImports(out);
            out.println("import javax.faces.component.UIComponent;");
        }
        out.println();

        out.println("public class " + faceletsHandlerClassName + " extends "
                + parentClassName + " {\n");

        out.println("\tpublic " + faceletsHandlerClassName
                + "(ComponentConfig config) {");
        out.println("\t\tsuper(config);");
        out.println("\t}");
        out.println();

        if (actions.isEmpty() == false) {
            Action1_1 action = actions.get(0);
            action.writeFaceletsHandler(out);
        }

        if (textBodyContent) {
            writeFaceletsTextHandlerMethods(out);
        }

        /*
         * out.println("\tprotected MetaRuleset createMetaRuleset(Class type)
         * {"); out .println("\t\tMetaRuleset metaRuleset =
         * super.createMetaRuleset(type);"); // metaRuleset.addRule(new
         * ComponentMetaRule(getDefaultListenerType()));
         * 
         * Iterator it; for (it = capabilities.iterator(); it.hasNext();) {
         * Capability capability = (Capability) it.next();
         * 
         * capability.writeFaceletsHandler(out); }
         * 
         * out.println("\t\treturn metaRuleset;"); out.println("\t}");
         */

        out.println("}");

        out.close();
        fout.close();

    }

    protected void writeFaceletsTextHandlerMethods(PrintWriter out) {
        out.println("\tprotected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {");
        out.println("\t\tsetTextBody(ctx, c);");
        out.println("\t}");
        out.println();

        if (false) {
            out.println("\tprotected void applyNextHandler(FaceletContext ctx, UIComponent c) {");
            out.println("\t}");

            out.println();
        }
    }

    protected void writeFaceletsTextHandlerImports(PrintWriter out) {
        out.println("import com.sun.facelets.FaceletContext;");
    }

    protected void writeFaceletsHandlerImports(PrintWriter out) {
        out.println("import com.sun.facelets.tag.jsf.ComponentConfig;");
    }

    public String getFaceletsHandlerFileName() {
        return faceletsHandlerFileName;

    }

    public String getFaceletsHandlerClassName(
            Map<Identifier, Component1_1> components) {
        if (faceletsHandlerClassName != null) {
            return packageKit.faceletsPackage + "." + faceletsHandlerClassName;
        }

        if (extendsId != null) {
            Component1_1 parent = components.get(extendsId);
            if (parent != null) {
                return parent.getFaceletsHandlerClassName(components);
            }
        }

        return "org.rcfaces.core.internal.facelets.CameliaComponentHandler";
    }

    public RenderKit getPackageRenderKit() {
        return packageKit;
    }

    public final boolean isInputType() {
        return inputType;
    }

    protected String getListenersToolsClassName() {
        return "ListenersTools";
    }

    public Identifier getIdentifier() {
        return identifier;
    }

}