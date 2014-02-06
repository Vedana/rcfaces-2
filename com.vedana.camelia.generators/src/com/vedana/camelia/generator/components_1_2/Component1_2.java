/*
 * $Id: Component1_2.java,v 1.6 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.beans.IntrospectionException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.Identifier;
import com.vedana.camelia.generator.components_1_1.Action1_1;
import com.vedana.camelia.generator.components_1_1.Attribute1_1;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator;
import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;
import com.vedana.camelia.generator.components_1_1.Capability1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.FacesComponent;
import com.vedana.camelia.generator.components_1_1.FacesTagAttribute;
import com.vedana.camelia.generator.components_1_1.Method1_1;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.6 $
 */
public class Component1_2 extends Component1_1 {

    public Component1_2(CapabilitiesRepository cr, Element xml,
            Map<String, RenderKit> renderKits, boolean bootStrap,
            Map<Identifier, Component1_1> components,
            Map<Identifier, FacesComponent> facesComponents)
            throws IntrospectionException {
        super(cr, xml, renderKits, bootStrap, components, facesComponents);
    }

    @Override
    public void writeSource(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<String, String> events, Map<String, Method1_1> methods,
            Map<Identifier, FacesComponent> facesComponents,
            Map<String, ComponentDoc> componentsDoc,
            Map<String, AttributeDoc> attributesDoc) {
        Component1_2 parent = null;
        String parentClassName = null;

        if (implementsProperties.isEmpty() == false) {
            for (Iterator<String> it = implementsProperties.iterator(); it
                    .hasNext();) {
                String propertyName = it.next();

                String pn = Component1_1.getPropertyName(propertyName);

                String cpropertyName = convertImplementsPropertyName(propertyName);

                packageKit.properties.put(pn, cpropertyName);
            }
        }

        ComponentDoc componentDoc = componentsDoc.get(tagName);

        if (extendsId != null) {
            parent = (Component1_2) components.get(extendsId);
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
            imports.add("javax.el.ValueExpression");
        }

        if (template != null) {
            imports.add("org.rcfaces.core.internal.component.IInitializationState");
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_2 capability = (Capability1_2) it.next();

            imports.addAll(capability.listImports(methods));
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_2 attributes = (Attribute1_2) it.next();

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
                Capability1_2 capability = (Capability1_2) it1.next();

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
            Attribute1_2 attribute = (Attribute1_2) it.next();

            attribute.writeSourceVariable(out);
        }

        writeCameliaAttributs(out, listCameliaAttributes(), parentClassName,
                propertyValue);

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
            Capability1_2 capability = (Capability1_2) it.next();

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
            Capability1_2 capability = (Capability1_2) it.next();

            capability.writeSource(out, packageKit.properties, events, methods,
                    propertyValue, methodsAlreadyGenerated, ignoreRequired,
                    props, inputType);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_2 attribute = (Attribute1_2) it.next();

            attribute
                    .writeSource(out, packageKit.properties, events,
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
            out.println("\tpublic void setValueExpression(String name, ValueExpression binding) {");

            boolean first = true;
            for (Iterator<String> it = ps.iterator(); it.hasNext();) {
                String sid = it.next();

                translateProperty(out, sid, first);
                first = false;
            }

            if (first == false) {
                out.println("\t\t}");
            }

            out.println("\t\tsuper.setValueExpression(name, binding);");
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

    protected void translateProperty(PrintWriter out, String sid, boolean first) {

        String id = Component1_1.getPropertyName(sid);

        if (first) {
            out.println("\t\tif (Properties." + id + ".equals(name)) {");

        } else {
            out.println();
            out.println("\t\t} else if (Properties." + id + ".equals(name)) {");
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

    protected String convertImplementsPropertyName(String propertyName) {
        return CameliaGenerator.PROPERTY_PREFIX
                + Component1_1.lowerCaseFirstChar(propertyName);
    }

    protected void writeCameliaAttributs(PrintWriter out,
            Set<String> cameliaAttributes, String parentClassName,
            String propertyValue) {

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
    }

    @Override
    public void writeTag(PrintWriter out,
            Map<Identifier, Component1_1> components,
            Map<String, Method1_1> methods,
            Map<Identifier, FacesComponent> facesComponents) {
        Component1_2 parent = null;
        String parentClassName = null;
        FacesComponent fc = null;

        parent = (Component1_2) components.get(extendsId);

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
            tagImports.add("javax.el.ValueExpression");
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
        tagImports.add("org.rcfaces.core.internal.tools.ListenersTools1_2");
        tagImports.add(packageKit.propertiesPackage + ".Properties");

        if (packageKit.tagPackage.equals("org.rcfaces.core.internal.taglib") == false) {
            tagImports.add("org.rcfaces.core.internal.taglib.CameliaTag");
        }

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_2 capability = (Capability1_2) it.next();

            tagImports.addAll(capability.listTagImports());
        }

        for (Iterator<String> it = tagImports.iterator(); it.hasNext();) {
            String methodName = it.next();

            out.println("import " + methodName + ";");
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
            Capability1_2 capability = (Capability1_2) it.next();

            capability.writeTagVariable(out, methods,
                    propertiesAlreadyGenerated);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_2 attribute = (Attribute1_2) it.next();

            attribute.writeTagVariable(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_2 action = (Action1_2) it.next();

            action.writeTagVariable(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute1_2 fta = (FacesTagAttribute1_2) it.next();

            fta.writTagVariable(out);
        }

        if (fc != null) {
            fc.writeTagVariable(out);
        }

        if (converter) {
            out.println("\tprivate ValueExpression converter;");
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
            Capability1_2 capability = (Capability1_2) it.next();

            capability.writeTagGetterAndSetter(out, methods, propertyValue,
                    props);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_2 attribute = (Attribute1_2) it.next();

            attribute.writeTagGetterAndSetter(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_2 action = (Action1_2) it.next();

            action.writeTagGetterAndSetter(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute1_2 fta = (FacesTagAttribute1_2) it.next();

            fta.writeTagGetterAndSetter(out);
        }

        if (fc != null) {
            fc.writeTagGetterAndSetter(out);
        }

        if (converter) {
            out.println("\tpublic final void setConverter(ValueExpression converter) {");
            out.println("\t\tthis.converter=converter;");
            out.println("\t}\n");
            /*
             * out.println("\tpublic final String getConverter() {");
             * out.println("\t\treturn converter;"); out.println("\t}\n");
             */
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
                    Capability1_2 capability = (Capability1_2) itl.next();

                    capability.writeTagLogProperties(out, methods,
                            propertyValue, propertiesAlreadyGenerated);
                }
            }

            if (it2.hasNext()) {
                for (Iterator<Attribute1_1> itl = attributes.iterator(); itl
                        .hasNext();) {
                    Attribute1_2 attribute = (Attribute1_2) itl.next();

                    attribute.writeTagLogProperties(out);
                }
            }

            if (it3.hasNext()) {
                for (Iterator<Action1_1> itl = actions.iterator(); itl
                        .hasNext();) {
                    Action1_2 attribute = (Action1_2) itl.next();

                    attribute.writeTagLogProperties(out);
                }
            }

            if (it4.hasNext()) {
                for (Iterator<FacesTagAttribute> itl = tagAttributes.iterator(); itl
                        .hasNext();) {
                    FacesTagAttribute1_2 fta = (FacesTagAttribute1_2) itl
                            .next();

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

            if (converter) {
                out.println("\t\tApplication application = facesContext.getApplication();");
            }

            // out.println();
            // out.println("\t\tcomponent.clearListeners();");

            for (; it1.hasNext();) {
                Capability1_2 capability = (Capability1_2) it1.next();

                capability.writeTagSetProperties(out, methods, propertyValue,
                        propertiesAlreadyGenerated);
            }

            for (; it2.hasNext();) {
                Attribute1_2 attribute = (Attribute1_2) it2.next();

                attribute.writeTagSetProperties(out, propertyValue);
            }

            for (; it3.hasNext();) {
                Action1_2 attribute = (Action1_2) it3.next();

                attribute.writeTagSetProperties(out);
            }

            for (; it4.hasNext();) {
                FacesTagAttribute1_2 fta = (FacesTagAttribute1_2) it4.next();

                fta.writTagSetProperties(out, packageKit.properties);
            }

            if (fc != null) {
                fc.writeTagSetProperties(out, packageKit.properties);
            }

            if (converter) {
                out.println("\tif (converter != null) {");
                out.println("\t\tif (converter.isLiteralText()==false) {");
                out.println("\t\t\tcomponent.setValueExpression(Properties.CONVERTER, converter);");
                out.println("\t\t} else {");
                out.println("\t\t\tcomponent.setConverter(application.createConverter(converter.getExpressionString()));");
                out.println("\t\t}");
                out.println("\t}");
            }

            out.println("\t}\n");
        }

        out.println("\tpublic void release() {");

        for (Iterator<Capability1_1> it = capabilities.iterator(); it.hasNext();) {
            Capability1_2 capability = (Capability1_2) it.next();

            capability
                    .writeTagRelease(out, methods, propertiesAlreadyGenerated);
        }

        for (Iterator<Attribute1_1> it = attributes.iterator(); it.hasNext();) {
            Attribute1_2 attribute = (Attribute1_2) it.next();

            /*
             * if (attribute.getType().equals("Object[]")) { continue; }
             */

            attribute.writeTagRelease(out);
        }

        for (Iterator<Action1_1> it = actions.iterator(); it.hasNext();) {
            Action1_2 action = (Action1_2) it.next();

            action.writeTagRelease(out);
        }

        for (Iterator<FacesTagAttribute> it = tagAttributes.iterator(); it
                .hasNext();) {
            FacesTagAttribute1_2 fta = (FacesTagAttribute1_2) it.next();

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

    @Override
    protected void writeTldAttributes(PrintWriter out, String attributeName) {
        String type = null;

        if ("id".equals(attributeName)) {
            out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
            return;

        } else if ("binding".equals(attributeName)) {
            type = "javax.faces.component.UIComponent";

        } else if ("converter".equals(attributeName)) {
            type = "javax.faces.convert.Converter";
        }

        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

        if (type == null) {
            out.println("\t\t\t<deferred-value />");

        } else {
            out.println("\t\t\t<deferred-value>");
            out.println("\t\t\t\t<type>" + type + "</type>");
            out.println("\t\t\t</deferred-value>");
        }
    }

    @Override
    protected void writeDocBeforeName(ComponentDoc componentDoc, PrintWriter out) {
        componentDoc.writeTld(out);
    }

    @Override
    protected void writeDocAfterName(ComponentDoc componentDoc, PrintWriter out) {
    }

    @Override
    protected void writeDocBeforeName(AttributeDoc att, PrintWriter out) {
        att.writeTld(out);
    }

    @Override
    protected void writeDocAfterName(AttributeDoc att, PrintWriter out) {

    }

}