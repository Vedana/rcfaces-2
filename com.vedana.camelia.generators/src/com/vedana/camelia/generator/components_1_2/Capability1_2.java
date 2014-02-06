/*
 * $Id: Capability1_2.java,v 1.5 2012/12/07 13:45:23 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_2;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator;
import com.vedana.camelia.generator.components_1_1.Capability1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.Method1_1;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.5 $
 */
public class Capability1_2 extends Capability1_1 {

    public Capability1_2(String id, BeanInfo beanInfo,
            boolean clearCachedValue, boolean forceComponent, boolean required,
            boolean onlyValueBinding, String defaultValue)
            throws IntrospectionException {

        super(id, beanInfo, clearCachedValue, forceComponent, required,
                onlyValueBinding, defaultValue);
    }

    @Override
    public void writeSource(PrintWriter out, Map<String, String> properties,
            Map<String, String> events, Map<String, Method1_1> methods,
            String attributeValueName, Set<String> methodAlreadyDeclared,
            boolean ignoreRequired, Set<String> propertiesAlreadyGenerated,
            boolean inputType) {

        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.add(pd.getName()) == false) {
                continue;
            }

            String clName = pd.getPropertyType().getName();
            if (clName.startsWith("java.lang.")) {
                clName = clName.substring(10);
            }

            String propertyName = Component1_1.getPropertyName(pd.getName());
            if (forceComponent == false) {
                if (propertyName.equals("CONVERTER")
                        || propertyName.equals("IMMEDIATE")) {
                    continue;
                }
            }

            String defaultValue = this.defaultValue;
            if (defaultValue == null) {
                defaultValue = DEFAULT_VALUES.get(propertyName);
            }
            if (defaultValue != null) {
                defaultValue = removePackageFromConstantName(defaultValue);
            }

            if (propertyName.equals("REQUIRED")) {
                if (ignoreRequired) {
                    continue;
                }
            }

            String value;
            if (FACES_CONSTANTS.contains(propertyName.toLowerCase())) {
                value = Component1_1.lowerCaseFirstChar(pd.getName());

            } else {
                value = CameliaGenerator.PROPERTY_PREFIX
                        + Component1_1.lowerCaseFirstChar(pd.getName());
            }

            properties.put(propertyName, value);

            String sig = Method1_1.getSignature(pd.getReadMethod());

            int array = 0;
            Class< ? > pa = pd.getPropertyType();
            for (; pa.isArray(); pa = pa.getComponentType()) {
                array++;
            }

            Method1_1 method = methods.get(sig);
            if (methodAlreadyDeclared.add(sig) == false) {
                // rien

            } else if (method != null) {
                method.writeSource(out);

            } else {
                String facesContext = "null";

                boolean writeBody = true;

                out.print("\tpublic " + pa.getName());
                for (int a = array; a > 0; a--) {
                    out.print("[]");
                }

                out.println(" " + pd.getReadMethod().getName() + "() {");

                if (GENERATE_FACES_CONTEXT) {
                    out.println("\t\treturn " + pd.getReadMethod().getName()
                            + "(null);");
                    out.println("\t}\n");

                    sig = pd.getReadMethod().getName()
                            + "(Ljavax.faces.context.FacesContext;)";
                    if (methodAlreadyDeclared.add(sig) == false) {
                        writeBody = false;
                    }

                    if (writeBody) {

                        out.println("\t/**");
                        out.println("\t * See {@link #"
                                + pd.getReadMethod().getName() + "() "
                                + pd.getReadMethod().getName()
                                + "()} for more details");
                        out.println("\t */");

                        out.print("\tpublic " + pa.getName());

                        for (int a = array; a > 0; a--) {
                            out.print("[]");
                        }

                        out.println(" "
                                + pd.getReadMethod().getName()
                                + "(javax.faces.context.FacesContext facesContext) {");
                        facesContext = "facesContext";
                    }
                }

                if (writeBody) {

                    out.print("\t\treturn ");
                    if (clName.equals("String")) {
                        if (pd.getName().equals(attributeValueName)) {
                            out.println("org.rcfaces.core.internal.tools.ValuesTools.valueToString(this, "
                                    + facesContext + ");");

                        } else {
                            out.println("engine.getStringProperty(Properties."
                                    + propertyName + ", " + facesContext + ");");
                        }
                    } else if (clName.equals("int")) {
                        if (pd.getName().equals(attributeValueName)) {
                            out.println("org.rcfaces.core.internal.tools.ValuesTools.valueToInt(this);");

                        } else {
                            out.print("engine.getIntProperty(Properties."
                                    + propertyName + ",");
                            if (defaultValue == null) {
                                defaultValue = "0";
                            }
                            if (defaultValue != null) {
                                out.print(defaultValue);
                                out.print(", ");
                            }

                            out.println(facesContext + ");");
                        }
                    } else if (clName.equals("boolean")) {
                        if (pd.getName().equals(attributeValueName)) {
                            out.println("org.rcfaces.core.internal.tools.ValuesTools.valueToBool(this);");

                        } else {
                            out.print("engine.getBoolProperty(Properties."
                                    + propertyName + ", ");
                            if (defaultValue == null) {
                                defaultValue = "false";
                            }
                            if (defaultValue != null) {
                                out.print(defaultValue);
                                out.print(", ");
                            }

                            out.println(facesContext + ");");
                        }
                    } else if (clName.equals("Boolean")) {
                        if (pd.getName().equals(attributeValueName)) {
                            out.println("org.rcfaces.core.internal.tools.ValuesTools.valueToBoolean(this);");

                        } else {
                            out.println("engine.getBooleanProperty(Properties."
                                    + propertyName + ", " + facesContext + ");");
                        }
                    } else if (clName.equals("Integer")) {
                        if (pd.getName().equals(attributeValueName)) {
                            out.println("org.rcfaces.core.internal.tools.ValuesTools.valueToInteger(this);");

                        } else {
                            out.println("engine.getIntegerProperty(Properties."
                                    + propertyName + ", " + facesContext + ");");
                        }
                    } else if (isSerialiazable(pa)) {

                        if (Serializable.class != pa) {
                            out.print("(");

                            out.print(pa.getName());

                            for (int a = array; a > 0; a--) {
                                out.print("[]");
                            }
                            out.print(")");
                        }

                        if (pd.getName().equals(attributeValueName)) {
                            out.println("getValue();");

                        } else {

                            out.println("engine.getProperty(Properties."
                                    + propertyName + ", " + facesContext + ");");
                        }
                    } else if (clName.equals("java.util.Comparator")) {
                        out.println("(" + clName
                                + ")engine.getProperty(Properties."
                                + propertyName + ", " + facesContext + ");");

                    } else if (clName.equals("javax.faces.el.ValueBinding")) {
                        out.println("engine.getValueExpressionProperty(Properties."
                                + propertyName + ");");

                    } else {
                        out.println("engine.getProperty(Properties."
                                + propertyName + ", " + facesContext + ");");

                    }

                    out.println("\t}\n");
                }
            }

            /* isXxXXxxSetted() */

            String ptestMethodName = "is"
                    + Character.toUpperCase(pd.getName().charAt(0))
                    + pd.getName().substring(1) + "Setted";

            sig = Method1_1.normalizeMethodId(ptestMethodName + "()");
            method = methods.get(sig);
            if (methodAlreadyDeclared.add(sig) == false) {
                // rien

            } else if (method != null) {
                method.writeSource(out);

            } else {

                out.println("\t/**");
                out.println("\t * Returns <code>true</code> if the attribute \""
                        + pd.getName() + "\" is set.");
                out.println("\t * @return <code>true</code> if the attribute is set.");
                out.println("\t */");

                out.println("\tpublic final boolean " + ptestMethodName
                        + "() {");
                out.println("\t\treturn engine.isPropertySetted(Properties."
                        + propertyName + ");");
                out.println("\t}\n");

            }

            if (pd.getWriteMethod() != null) {

                sig = Method1_1.getSignature(pd.getWriteMethod());

                boolean valueBinding = true;

                method = methods.get(sig);
                if (methodAlreadyDeclared.add(sig) == false) {
                    // rien

                } else if (method != null) {
                    method.writeSource(out);

                } else {
                    String varName = pd.getName();
                    if (varName.equals("for")) {
                        varName = "forValue";
                    }

                    out.print("\tpublic void " + pd.getWriteMethod().getName()
                            + "(");

                    out.print(pa.getName());
                    for (int a = array; a > 0; a--) {
                        out.print("[]");
                    }
                    out.println(" " + varName + ") {");

                    if (pd.getName().equals(attributeValueName) == false) {
                        out.print("\t\tengine.setProperty(Properties."
                                + propertyName + ", ");

                        if (Object.class.isAssignableFrom(pd.getPropertyType())) {
                            if (isSerialiazable(pd.getPropertyType()) == false) {
                                // out.print("(java.io.Serializable)");
                            }
                        }

                        out.println(varName + ");");

                        if (clName.equals("javax.faces.el.MethodBinding")
                                || clName.equals("javax.faces.el.ValueBinding")) {
                            valueBinding = false;
                        }
                    } else {
                        String valueVarName = varName;

                        if (clName.equals("String")) {
                            // out.println("\t\tsetValue(" + varName + ");");

                        } else if (clName.equals("int")) {
                            out.println("\t\tObject value=Integer.valueOf("
                                    + varName + ");");
                            valueVarName = "value";

                        } else if (clName.equals("boolean")) {
                            out.println("\t\tObject value=Boolean.valueOf("
                                    + varName + ");");
                            valueVarName = "value";

                        } else if (clName.equals("Integer")) {
                            // out.println("\t\tsetValue(" + varName + ");");

                        } else { // if (clName.equals("Boolean")) {
                            // out.println("\t\tsetValue(" + varName + ");");
                        }

                        if (inputType) {
                            out.println("\t\tif (org.rcfaces.core.internal.listener.CameliaPhaseListener.isApplyingRequestValues()) {");
                            out.println("\t\t\tsetSubmittedExternalValue("
                                    + valueVarName + ");");
                            out.println("\t\t} else {");
                            out.println("\t\t\tsetValue(" + valueVarName + ");");
                            out.println("\t\t}");
                        } else {
                            out.println("\t\tsetValue(" + valueVarName + ");");
                        }
                    }

                    if (clearCachedValue) {
                        out.println("\t\tclearCachedValue();");
                    }

                    out.println("\t}\n");

                    if (false && valueBinding) {

                        String methodN = pa.getName();
                        if (methodN.startsWith("java.lang.")) {
                            methodN = methodN.substring(10);
                        }
                        for (int a = array; a > 0; a--) {
                            methodN += "[]";
                        }

                        out.println("\t/**");
                        out.println("\t * See {@link #"
                                + pd.getWriteMethod().getName() + "(" + methodN
                                + ") " + pd.getWriteMethod().getName() + "("
                                + methodN + ")} for more details");
                        out.println("\t */");

                        out.println("\tpublic void "
                                + pd.getWriteMethod().getName()
                                + "(ValueExpression " + varName + ") {");
                        if (pd.getName().equals(attributeValueName) == false) {
                            out.print("\t\tengine.setProperty(Properties."
                                    + propertyName + ", ");

                            out.println(varName + ");");
                        } else {
                            out.println("\t\tsetValueExpression(Properties.VALUE, "
                                    + varName + ");");
                        }

                        if (clearCachedValue) {
                            out.println("\t\tclearCachedValue();");
                        }
                        out.println("\t}\n");
                    }
                }
            }

            if (onlyValueBinding) {

                out.println("\t/**");
                out.println("\t * Return the type of the property represented by the {@link ValueExpression}, relative to the specified {@link javax.faces.context.FacesContext}.");
                out.println("\t */");
                out.println("\tpublic Class "
                        + pd.getReadMethod().getName()
                        + "Type(javax.faces.context.FacesContext facesContext) {");

                out.println("\t\tValueExpression valueExpression=engine.getValueExpressionProperty(Properties."
                        + propertyName + ");");

                out.println("\t\tif (valueExpression==null) {");
                out.println("\t\t\treturn null;");
                out.println("\t\t}");
                out.println("\t\tif (facesContext==null) {");
                out.println("\t\t\tfacesContext=javax.faces.context.FacesContext.getCurrentInstance();");
                out.println("\t\t}");
                out.println("\t\treturn valueExpression.getType(facesContext.getELContext());");
                out.println("\t}\n");
            }

        }

        for (Iterator<String> it = this.events.iterator(); it.hasNext();) {
            String name = it.next();

            if (propertiesAlreadyGenerated.add("event$" + name) == false) {
                continue;
            }

            String listenerClass = "I" + name;
            if (listenerClass.equals("IValueChangeListener")) {
                // listenerClass = "ValueChangeListener";
                listenerClass = "javax.faces.event.ValueChangeListener";
            } else {
                listenerClass = Component1_1.EVENT_PACKAGE + "."
                        + listenerClass;
            }

            out.println("\tpublic final void add" + name + "(" + listenerClass
                    + " listener) {");
            out.println("\t\taddFacesListener(listener);");
            out.println("\t}\n");

            out.println("\tpublic final void remove" + name + "("
                    + listenerClass + " listener) {");
            out.println("\t\tremoveFacesListener(listener);");
            out.println("\t}\n");

            out.println("\tpublic final javax.faces.event.FacesListener [] list"
                    + name + "s() {");
            out.println("\t\treturn getFacesListeners(" + listenerClass
                    + ".class);");
            out.println("\t}\n");
        }

        for (Iterator<MethodDescriptor> it = listMethods(); it.hasNext();) {
            MethodDescriptor md = it.next();

            String sig = Method1_1.getSignature(md.getMethod());

            if (methodAlreadyDeclared.add(sig) == false) {
                continue;
            }

            Method1_1 method = methods.get(sig);
            if (method == null) {
                continue;
            }

            method.writeSource(out);
        }
    }

    @Override
    public void writeTagVariable(PrintWriter out,
            Map<String, Method1_1> methods,
            Set<String> propertiesAlreadyGenerated) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.contains(pd.getName())) {
                continue;
            }

            String varName = getTagVarName(pd.getName());

            out.println("\tprivate ValueExpression " + varName + ";");
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            if (propertiesAlreadyGenerated.contains("event$" + eventName)) {
                continue;
            }

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            out.println("\tprivate ValueExpression " + eventName + "s;");
        }
    }

    @Override
    protected void writeTagGetterSetter(PrintWriter out, String methodName,
            String varName) {
        /*
         * out.print("\tpublic final String get"); out.println(methodName + "()
         * {"); out.println("\t\treturn " + varName + ";");
         * out.println("\t}\n");
         */

        out.print("\tpublic void set");
        out.println(methodName + "(ValueExpression " + varName + ") {");
        out.println("\t\tthis." + varName + " = " + varName + ";");
        out.println("\t}\n");
    }

    @Override
    public void writeTagSetProperties(PrintWriter out,
            Map<String, Method1_1> methods, String valueAttributeName,
            Set<String> propertiesAlreadyGenerated) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.contains(pd.getName())) {
                continue;
            }

            String propertyName = Component1_1.getPropertyName(pd.getName());

            Class< ? > propertyType = pd.getPropertyType();

            java.lang.reflect.Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }

            String varName = getTagVarName(pd.getName());

            String methodName = writeMethod.getName();
            boolean value = false;

            if (false && USE_VALUE) {
                if (pd.getName().equals(valueAttributeName)) {
                    methodName = "setValue";
                    value = true;
                }
            }

            boolean onlyValueBinding = this.onlyValueBinding;
            boolean noValueBinding = false;

            if (propertyType.isPrimitive() == false
                    && propertyType.getName().startsWith("java.lang.") == false
                    && ACCEPT_STRING.contains(pd.getName().toUpperCase()) == false) {
                onlyValueBinding = true;
            }

            out.println("\n\t\tif (" + varName + " != null) {");
            if (onlyValueBinding == false && noValueBinding == false) {
                out.println("\t\t\tif (" + varName
                        + ".isLiteralText()==false) {");
            }

            if (noValueBinding == false) {
                out.println("\t\t\t\tcomponent.setValueExpression(Properties."
                        + propertyName + ", " + varName + ");");
            }

            if (onlyValueBinding == false) {
                if (noValueBinding == false) {
                    out.println();
                    out.println("\t\t\t} else {");
                }
                out.print("\t\t\t\tcomponent." + methodName + "(");

                String clName = pd.getPropertyType().getName();
                if (clName.startsWith("java.lang.")) {
                    clName = clName.substring(10);
                }

                if (ENUMERATIONS.contains(varName)) {
                    clName = "String";
                }

                if (clName.equals("int")) {
                    out.print("getInt(" + varName + ".getExpressionString())");

                } else if (clName.equals("long")) {
                    out.print("getLong(" + varName + ".getExpressionString())");

                } else if (clName.equals("boolean")) {
                    if (value) {
                        out.print("getBoolean(" + varName
                                + ".getExpressionString())");
                    } else {
                        out.print("getBool(" + varName
                                + ".getExpressionString())");
                    }

                } else if (clName.equals("Boolean")) {
                    out.print("getBoolean(" + varName
                            + ".getExpressionString())");

                } else if (clName.equals("Number")) {
                    out.print("getNumber(" + varName
                            + ".getExpressionString())");

                } else if (clName.equals("Integer")) {
                    out.print("getInteger(" + varName
                            + ".getExpressionString())");

                } else {
                    out.print(varName + ".getExpressionString()");
                }

                out.println(");");
                if (noValueBinding == false) {
                    out.println("\t\t\t}");
                }
            }
            out.println("\t\t}");
        }
        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            if (propertiesAlreadyGenerated.contains("event$" + eventName)) {
                continue;
            }

            String type = Component1_1.getPropertyName(eventName);
            // String type=eventName.substring(0,
            // eventName.indexOf("Listener")).toUpperCase();

            String ev = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            out.println("\n\t\tif (" + ev + "s != null) {");
            out.println("\t\t\tListenersTools1_2.parseListener(facesContext, component, ListenersTools."
                    + type + "_TYPE, " + ev + "s);");
            out.println("\t\t}");
        }
    }

    @Override
    protected void writeTldEventAttributes(PrintWriter out, String eventName) {
        // super.writeTldEventAttributes(out, eventName);

        out.println("\t\t\t<deferred-value>");
        out.println("\t\t\t\t<type>java.lang.String</type>");
        out.println("\t\t\t</deferred-value>");
    }

    @Override
    protected void writeTldAttributes(PrintWriter out, PropertyDescriptor pd) {
        // super.writeTldAttributes(out, pd);

        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

        String clName = pd.getPropertyType().getName();

        if (ENUMERATIONS.contains(pd.getName())) {
            clName = "java.lang.String";

        } else if (clName.equals("javax.el.ValueExpression")) {
            clName = "java.lang.Object";
        }
        if ("Number".equals(clName) || "java.lang.Number".equals(clName)) {
            clName = "double";
        }

        out.println("\t\t\t<deferred-value>");
        out.println("\t\t\t\t<type>" + clName + "</type>");
        out.println("\t\t\t</deferred-value>");
    }

    @Override
    protected void writeTldValueAttributes(PrintWriter out) {
        out.println("\t\t\t<deferred-value/>");
    }
}