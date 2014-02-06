/*
 * $Id: Capability1_1.java,v 1.2 2012/12/07 13:45:23 oeuillot Exp $
 * 
 * $Log: Capability1_1.java,v $
 * Revision 1.2  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.1  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.5  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.4  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.3  2008/06/05 09:42:24  oeuillot
 * Les fichiers de configuration sont dans le core
 *
 * Revision 1.2  2008/01/14 14:14:57  oeuillot
 * I2495
 * Ajout de logs pour le debuggage au niveau des components et tags
 * Correction de bug sur le f_grid  (check et selection)
 * Ajout de la configuration du layout du calendar popup.
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.6  2006/12/21 17:03:34  oeuillot
 * *** empty log message ***
 *
 * Revision 1.5  2006/12/18 15:14:02  oeuillot
 * *** empty log message ***
 *
 * Revision 1.4  2006/12/08 17:37:09  oeuillot
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
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components.CapabilitiesRepository;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.2 $
 */
public class Capability1_1 {

    protected static final boolean USE_VALUE = true;

    protected static final boolean GENERATE_FACES_CONTEXT = true;

    protected static final Set<String> ENUMERATIONS = new HashSet<String>();
    static {
        ENUMERATIONS.add("hiddenMode");
        ENUMERATIONS.add("itemHiddenMode");
        ENUMERATIONS.add("asyncRenderMode");
        ENUMERATIONS.add("asyncDecodeMode");
        ENUMERATIONS.add("checkCardinality");
        ENUMERATIONS.add("selectionCardinality");
        ENUMERATIONS.add("textPosition");
        ENUMERATIONS.add("order");
        ENUMERATIONS.add("mode");
        ENUMERATIONS.add("attributesLocale");
        ENUMERATIONS.add("clientDatesStrategy");
        ENUMERATIONS.add("numberFormatType");
        ENUMERATIONS.add("inputType");
        ENUMERATIONS.add("calendarLayout");
        ENUMERATIONS.add("clientSelectionFullState");
        ENUMERATIONS.add("clientCheckFullState");
        ENUMERATIONS.add("clientAdditionalInformationFullState");
        ENUMERATIONS.add("dragEffects");
        ENUMERATIONS.add("dropEffects");
        ENUMERATIONS.add("dragTypes");
        ENUMERATIONS.add("dropTypes");
        ENUMERATIONS.add("rowDragEffects");
        ENUMERATIONS.add("rowDropEffects");
        ENUMERATIONS.add("rowDragTypes");
        ENUMERATIONS.add("rowDropTypes");

        // attributs
        ENUMERATIONS.add("disabledWeekDays");
        ENUMERATIONS.add("date");
        ENUMERATIONS.add("itemHiddenMode");
        ENUMERATIONS.add("time");
        ENUMERATIONS.add("minTime");
        ENUMERATIONS.add("maxTime");
        ENUMERATIONS.add("defaultTime");
        ENUMERATIONS.add("defaultItemInputType");
        ENUMERATIONS.add("layoutType");
    }

    protected static final Map<String, String> DEFAULT_VALUES = new HashMap<String, String>();
    static {
        DEFAULT_VALUES.put("WHEEL_SELECTION", "true");
        DEFAULT_VALUES.put("EDITABLE", "true");
        DEFAULT_VALUES.put("ENABLED", "true");
        DEFAULT_VALUES.put("EXPANDABLE", "true");
        DEFAULT_VALUES.put("BORDER", "true");
        DEFAULT_VALUES.put("VISIBLE", "true");
        DEFAULT_VALUES.put("PRELOADED_LEVEL_DEPTH", "-1");
        DEFAULT_VALUES.put("HEADER_VISIBLE", "true");
        DEFAULT_VALUES.put("PREPEND_ID", "true");
        DEFAULT_VALUES
                .put("CALENDAR_LAYOUT",
                        "org.rcfaces.core.component.capability.ICalendarLayoutCapability.DEFAULT_LAYOUT");
        DEFAULT_VALUES
                .put("TEXT_POSITION",
                        "org.rcfaces.core.component.capability.IHorizontalTextPositionCapability.DEFAULT_POSITION");
        DEFAULT_VALUES
                .put("HIDDEN_MODE",
                        "org.rcfaces.core.component.capability.IHiddenModeCapability.DEFAULT_HIDDEN_MODE");

        DEFAULT_VALUES
                .put("DRAG_EFFECTS",
                        "org.rcfaces.core.component.capability.IDragAndDropEffects.UNKNOWN_DND_EFFECT");
        DEFAULT_VALUES
                .put("DROP_EFFECTS",
                        "org.rcfaces.core.component.capability.IDragAndDropEffects.UNKNOWN_DND_EFFECT");
    }

    protected static final Set<String> FACES_CONSTANTS = new HashSet<String>();
    static {
        FACES_CONSTANTS.add("required");
    }

    protected static final Set<String> ACCEPT_STRING = new HashSet<String>();
    static {
        ACCEPT_STRING.add("LITERALLOCALE");
        ACCEPT_STRING.add("LITERALTIMEZONE");
        ACCEPT_STRING.add("COMPONENTLOCALE");
        ACCEPT_STRING.add("COMPONENTTIMEZONE");
        ACCEPT_STRING.add("FILTERPROPERTIES");
        ACCEPT_STRING.add("DRAGTYPES");
        ACCEPT_STRING.add("DROPTYPES");
        // ACCEPT_STRING.add("DATE");
        // ACCEPT_STRING.add("PERIOD");
    }

    protected final BeanInfo beanInfo;

    protected List<BeanInfo> beanInfos;

    protected List<PropertyDescriptor> properties;

    protected List<MethodDescriptor> methods;

    protected Set<String> events = new HashSet<String>();

    protected boolean clearCachedValue;

    protected boolean forceComponent;

    protected final String id;

    protected boolean required;

    protected boolean onlyValueBinding;

    protected Class< ? > clazz;

    protected Capability1_1 parentCapability[];

    protected String defaultValue;

    public Capability1_1(String id, BeanInfo beanInfo,
            boolean clearCachedValue, boolean forceComponent, boolean required,
            boolean onlyValueBinding, String defaultValue)
            throws IntrospectionException {
        this.id = id;

        this.beanInfo = beanInfo;
        this.clearCachedValue = clearCachedValue;
        this.forceComponent = forceComponent;
        this.required = required;
        this.onlyValueBinding = onlyValueBinding;
        this.defaultValue = defaultValue;

        clazz = beanInfo.getBeanDescriptor().getBeanClass();
        if (clazz.isInterface()) {
            beanInfos = new ArrayList<BeanInfo>();

            scanInterfaces(beanInfos, new HashSet<Class< ? >>(), clazz);
        }

    }

    public Capability1_1[] getParentCapability(CapabilitiesRepository repository) {
        if (parentCapability != null) {
            return parentCapability;
        }

        Class< ? > ps[] = clazz.getInterfaces();

        List<Capability1_1> l = new ArrayList<Capability1_1>();

        for (int i = 0; i < ps.length; i++) {
            Class< ? > p = ps[i];

            if (p.getName()
                    .startsWith(CapabilitiesRepository.CAPABILITY_PREFIX) == false) {
                continue;
            }

            Capability1_1 parent = repository.getCapability(p.getName()
                    .substring(
                            CapabilitiesRepository.CAPABILITY_PREFIX.length()));
            if (parent == null) {
                continue;
            }

            l.add(parent);
        }

        parentCapability = l.toArray(new Capability1_1[l.size()]);

        return parentCapability;
    }

    private void scanInterfaces(List<BeanInfo> beanInfos,
            Set<Class< ? >> interfaces, Class< ? > clz)
            throws IntrospectionException {
        interfaces.add(clz);
        BeanInfo beanInfo = Introspector.getBeanInfo(clz);
        beanInfos.add(beanInfo);

        Class< ? > ints[] = clz.getInterfaces();
        if (ints.length == 0) {
            return;
        }

        for (int i = 0; i < ints.length; i++) {
            Class< ? > in = ints[i];

            if (interfaces.contains(in)) {
                continue;
            }

            scanInterfaces(beanInfos, interfaces, in);
        }

    }

    public BeanInfo getBeanInfo() {
        return beanInfo;
    }

    public String getClassName() {
        return beanInfo.getBeanDescriptor().getBeanClass().getName();
    }

    public Iterator<MethodDescriptor> listMethods() {
        if (methods != null) {
            return methods.iterator();
        }

        List<MethodDescriptor> methods = new ArrayList<MethodDescriptor>();
        if (beanInfos == null) {
            methods.addAll(Arrays.asList(beanInfo.getMethodDescriptors()));

        } else {
            for (Iterator<BeanInfo> it = beanInfos.iterator(); it.hasNext();) {
                BeanInfo bi = it.next();

                methods.addAll(Arrays.asList(bi.getMethodDescriptors()));
            }
        }

        Set<java.lang.reflect.Method> ms = new HashSet<java.lang.reflect.Method>();
        for (Iterator<MethodDescriptor> it = methods.iterator(); it.hasNext();) {
            MethodDescriptor md = it.next();

            ms.add(md.getMethod());
        }

        List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();
        if (beanInfos == null) {
            properties.addAll(Arrays.asList(beanInfo.getPropertyDescriptors()));

        } else {
            for (Iterator<BeanInfo> it = beanInfos.iterator(); it.hasNext();) {
                BeanInfo bi = it.next();

                properties.addAll(Arrays.asList(bi.getPropertyDescriptors()));
            }
        }

        for (Iterator<PropertyDescriptor> it = properties.iterator(); it
                .hasNext();) {
            PropertyDescriptor pd = it.next();

            if (pd.getWriteMethod() != null && pd.getReadMethod() != null) {
                ms.remove(pd.getWriteMethod());
                ms.remove(pd.getReadMethod());
                continue;
            }
        }

        methods.clear();
        List<java.lang.reflect.Method> eventRemove = new ArrayList<java.lang.reflect.Method>();
        for (Iterator<java.lang.reflect.Method> it = ms.iterator(); it
                .hasNext();) {
            java.lang.reflect.Method listMethod = it.next();

            String mName = listMethod.getName();
            if (mName.startsWith("list") == false) {
                continue;
            }

            mName = mName.substring(4, mName.length() - 1);

            java.lang.reflect.Method addMethod = null;
            java.lang.reflect.Method removeMethod = null;
            for (Iterator<java.lang.reflect.Method> it2 = ms.iterator(); it2
                    .hasNext();) {
                java.lang.reflect.Method m2 = it2.next();

                if (m2.getName().equals("add" + mName)) {
                    addMethod = m2;
                    continue;
                }

                if (m2.getName().equals("remove" + mName)) {
                    removeMethod = m2;
                    continue;
                }
            }

            if (addMethod == null || removeMethod == null) {
                continue;
            }

            events.add(mName);
            eventRemove.add(removeMethod);
            eventRemove.add(addMethod);
            eventRemove.add(listMethod);
        }
        methods.removeAll(eventRemove);

        methods.clear();
        for (Iterator<java.lang.reflect.Method> it = ms.iterator(); it
                .hasNext();) {
            methods.add(new MethodDescriptor(it.next()));
        }

        return methods.iterator();
    }

    public Iterator<PropertyDescriptor> listProperties() {
        if (properties != null) {
            return properties.iterator();

        }

        properties = new ArrayList<PropertyDescriptor>();
        if (beanInfos == null) {
            properties.addAll(Arrays.asList(beanInfo.getPropertyDescriptors()));

        } else {
            for (Iterator<BeanInfo> it = beanInfos.iterator(); it.hasNext();) {
                BeanInfo bi = it.next();

                properties.addAll(Arrays.asList(bi.getPropertyDescriptors()));
            }
        }

        for (Iterator<PropertyDescriptor> it = properties.iterator(); it
                .hasNext();) {
            PropertyDescriptor pd = it.next();

            if (pd.getWriteMethod() != null && pd.getReadMethod() != null) {
                continue;
            }

            it.remove();
        }

        return properties.iterator();
    }

    public String getClassNameWithoutPackage() {
        String cn = getClassName();
        return cn.substring(cn.lastIndexOf('.') + 1);
    }

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

            String defaultValue = DEFAULT_VALUES.get(propertyName);
            if (defaultValue != null && defaultValue.indexOf('.') >= 0) {
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
                        out.println("engine.getValueBindingProperty(Properties."
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
                                + "(ValueBinding " + varName + ") {");
                        if (pd.getName().equals(attributeValueName) == false) {
                            out.print("\t\tengine.setProperty(Properties."
                                    + propertyName + ", ");

                            out.println(varName + ");");
                        } else {
                            out.println("\t\tsetValueBinding(Properties.VALUE, "
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
                out.println("\t * Return the type of the property represented by the {@link ValueBinding}, relative to the specified {@link javax.faces.context.FacesContext}.");
                out.println("\t */");
                out.println("\tpublic Class "
                        + pd.getReadMethod().getName()
                        + "Type(javax.faces.context.FacesContext facesContext) {");

                out.println("\t\tValueBinding valueBinding=engine.getValueBindingProperty(Properties."
                        + propertyName + ");");

                out.println("\t\tif (valueBinding==null) {");
                out.println("\t\t\treturn null;");
                out.println("\t\t}");
                out.println("\t\tif (facesContext==null) {");
                out.println("\t\t\tfacesContext=javax.faces.context.FacesContext.getCurrentInstance();");
                out.println("\t\t}");
                out.println("\t\treturn valueBinding.getType(facesContext);");
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

    protected String removePackageFromConstantName(String defaultValue) {
        int idx = defaultValue.lastIndexOf('.');
        if (idx < 0) {
            return defaultValue;
        }

        int idx2 = defaultValue.lastIndexOf('.', idx - 1);
        if (idx2 < 0) {
            return defaultValue;
        }

        return defaultValue.substring(idx2 + 1);
    }

    protected String getClassFromConstantName(String defaultValue) {
        int idx = defaultValue.lastIndexOf('.');
        if (idx < 0) {
            return defaultValue;
        }

        return defaultValue.substring(0, idx);
    }

    protected boolean isSerialiazable(Class< ? > type) {
        return Serializable.class.isAssignableFrom(type);
    }

    public Set<String> listImports(Map<String, Method1_1> methods) {
        Set<String> imports = new HashSet<String>();
        imports.add(getClassName());

        if (GENERATE_FACES_CONTEXT) {
            // imports.add("javax.faces.context.FacesContext");
        }

        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            Method1_1 method = methods.get(Method1_1.getSignature(pd
                    .getReadMethod()));
            if (method != null) {
                imports.addAll(method.listImports());
            }
            java.lang.reflect.Method writeMethod = pd.getWriteMethod();
            if (writeMethod != null) {
                method = methods.get(Method1_1.getSignature(writeMethod));
                if (method != null) {
                    imports.addAll(method.listImports());
                }
            }

            String propertyName = Component1_1.getPropertyName(pd.getName());

            String defaultValue = DEFAULT_VALUES.get(propertyName);
            if (defaultValue != null && defaultValue.indexOf('.') >= 0) {
                String cls = getClassFromConstantName(defaultValue);
                if (cls != null) {
                    imports.add(cls);
                }
            }
        }

        for (Iterator<MethodDescriptor> it = listMethods(); it.hasNext();) {
            MethodDescriptor md = it.next();

            if (md.getName().startsWith("event")) {
                continue;
            }

            Method1_1 method = methods.get(Method1_1.getSignature(md
                    .getMethod()));
            if (method != null) {
                imports.addAll(method.listImports());
            }
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String event = it.next();

            String listenerClass = "I" + event;
            if (listenerClass.equals("IValueChangeListener")) {
                listenerClass = "javax.faces.event.ValueChangeListener";

            } else {
                listenerClass = Component1_1.EVENT_PACKAGE + "."
                        + listenerClass;
            }

            // imports.add(listenerClass);

            // imports.add("javax.faces.event.FacesListener");
        }

        return imports;
    }

    public void writeTagVariable(PrintWriter out,
            Map<String, Method1_1> methods,
            Set<String> propertiesAlreadyGenerated) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.contains(pd.getName())) {
                continue;
            }

            String varName = getTagVarName(pd.getName());

            out.println("\tprivate String " + varName + ";");
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            if (propertiesAlreadyGenerated.contains("event$" + eventName)) {
                continue;
            }

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            out.println("\tprivate String " + eventName + "s;");
        }
    }

    protected String getTagVarName(String name) {
        if (name.equals("for")) {
            return "forValue";
        }
        if (name.startsWith("UI") == false) {
            return name;
        }

        return "ui" + Character.toUpperCase(name.charAt(2)) + name.substring(3);
    }

    public void writeTagGetterAndSetter(PrintWriter out,
            Map<String, Method1_1> methods, String valueAttributeName,
            Set<String> propertiesAlreadyGenerated) {
        valueAttributeName = null;

        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.add(pd.getName()) == false) {
                continue;
            }

            String methodName = pd.getName();
            String varName = getTagVarName(pd.getName());

            if (methodName.startsWith("UI")) {
                methodName = "Ui" + methodName.substring(2);
            }
            methodName = Character.toUpperCase(methodName.charAt(0))
                    + methodName.substring(1);

            writeTagGetterSetter(out, methodName, varName);

            if (USE_VALUE) {
                if (pd.getName().equals(valueAttributeName)) {
                    writeTagGetterSetter(out, "Value", pd.getName());
                }
            }
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            if (propertiesAlreadyGenerated.add("event$" + eventName) == false) {
                continue;
            }

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            String eventAttributeName = convertEventName(eventName);
            eventAttributeName = Character.toUpperCase(eventAttributeName
                    .charAt(0)) + eventAttributeName.substring(1);

            writeTagGetterSetter(out, eventAttributeName, eventName + "s");
        }
    }

    private String convertEventName(String eventName) {
        return eventName;
        /*
         * if (eventName.equals("blurListener")) { return "blur"; } if
         * (eventName.equals("focusListener")) { return "onFocus"; } if
         * (eventName.equals("selectionListener")) { return "onSelection"; } if
         * (eventName.equals("doubleClickListener")) { return "onDoubleClick"; }
         * if (eventName.equals("checkListener")) { return "onCheck"; } if
         * (eventName.equals("closeListener")) { return "onClose"; } if
         * (eventName.equals("valueChangeListener")) { return "onChange"; } if
         * (eventName.equals("keyUpListener")) { return "onKeyUp"; } if
         * (eventName.equals("keyDownListener")) { return "onKeyDown"; } if
         * (eventName.equals("keyPressListener")) { return "onKeyPress"; } if
         * (eventName.equals("mouseOutListener")) { return "onMouseOut"; } if
         * (eventName.equals("mouseOverListener")) { return "onMouseOver"; } if
         * (eventName.equals("propertyChangeListener")) { return
         * "onPropertyChange"; } if (eventName.equals("sortListener")) { return
         * "onSort"; } if (eventName.equals("resetListener")) { return
         * "onReset"; } if (eventName.equals("resetListener")) { return
         * "onReset"; } if (eventName.equals("menuListener")) { return "onMenu";
         * }
         * 
         * return "*** " + eventName;
         */
    }

    protected void writeTagGetterSetter(PrintWriter out, String methodName,
            String varName) {
        out.print("\tpublic String get");
        out.println(methodName + "() {");
        out.println("\t\treturn " + varName + ";");
        out.println("\t}\n");

        out.print("\tpublic void set");
        out.println(methodName + "(String " + varName + ") {");
        out.println("\t\tthis." + varName + " = " + varName + ";");
        out.println("\t}\n");
    }

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
                out.println("\t\t\tif (isValueReference(" + varName + ")) {");
            }

            if (noValueBinding == false) {
                out.println("\t\t\t\tValueBinding vb = application.createValueBinding("
                        + varName + ");");
                out.println("\t\t\t\tcomponent.setValueBinding(Properties."
                        + propertyName + ", vb);");
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
                    out.print("getInt(" + varName + ")");

                } else if (clName.equals("long")) {
                    out.print("getLong(" + varName + ")");

                } else if (clName.equals("boolean")) {
                    if (value) {
                        out.print("getBoolean(" + varName + ")");
                    } else {
                        out.print("getBool(" + varName + ")");
                    }

                } else if (clName.equals("Boolean")) {
                    out.print("getBoolean(" + varName + ")");

                } else if (clName.equals("Number")) {
                    out.print("getNumber(" + varName + ")");

                } else if (clName.equals("Integer")) {
                    out.print("getInteger(" + varName + ")");

                } else {
                    out.print(varName);
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
            out.println("\t\t\tListenersTools.parseListener(facesContext, component, ListenersTools."
                    + type + "_TYPE, " + ev + "s);");
            out.println("\t\t}");
        }
    }

    public void writeTagRelease(PrintWriter out,
            Map<String, Method1_1> methods,
            Set<String> propertiesAlreadyGenerated) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.contains(pd.getName())) {
                continue;
            }

            Method1_1 method = methods.get(Method1_1.getSignature(pd
                    .getReadMethod()));
            if (method != null) {
                method.writeTagRelease(out);
                continue;
            }

            String varName = getTagVarName(pd.getName());

            out.println("\t\t" + varName + " = null;");
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            if (propertiesAlreadyGenerated.contains("event$" + eventName)) {
                continue;
            }

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            out.println("\t\t" + eventName + "s = null;");
        }
    }

    public void writeComponentFields(PrintWriter out,
            Map<String, Method1_1> methods) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            Method1_1 method = methods.get(Method1_1.getSignature(pd
                    .getReadMethod()));
            if (method != null) {
                method.writeComponentFields(out);
            }

            java.lang.reflect.Method writeMethod = pd.getWriteMethod();
            if (writeMethod != null) {
                method = methods.get(Method1_1.getSignature(writeMethod));
                if (method != null) {
                    method.writeComponentFields(out);
                }
            }
        }
    }

    public void writeComponentRelease(PrintWriter out,
            Map<String, Method1_1> methods) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            Method1_1 method = methods.get(Method1_1.getSignature(pd
                    .getReadMethod()));
            if (method != null) {
                method.writeComponentRelease(out);
            }

            java.lang.reflect.Method writeMethod = pd.getWriteMethod();
            if (writeMethod != null) {
                method = methods.get(Method1_1.getSignature(writeMethod));
                if (method != null) {
                    method.writeComponentRelease(out);
                }
            }
        }
    }

    public void writeTld(PrintWriter out, String valueAttributeName,
            ComponentDoc componentDoc, Map<String, AttributeDoc> attributesDoc) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (USE_VALUE) {
                // String propertyName =
                // Component.getPropertyName(pd.getName());
                if (pd.getName().equals(valueAttributeName)) {
                    out.println("\t\t<attribute>");
                    AttributeDoc att = ComponentDoc.getAttributeDoc(
                            componentDoc, attributesDoc, "value");
                    if (att != null) {
                        att.writeTld(out);
                    }

                    out.println("\t\t\t<name>value</name>");

                    writeTldValueAttributes(out);

                    out.println("\t\t</attribute>");

                    CameliaGenerator.attributesAutodoc.add("value");
                }
            }

            String name = pd.getName();
            if (name.startsWith("UI")) {
                name = "ui" + name.substring(2);
            }

            out.println("\t\t<attribute>");

            AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                    attributesDoc, name);
            if (att != null) {
                att.writeTld(out);
            }

            out.println("\t\t\t<name>" + name + "</name>");

            if (required) {
                out.println("\t\t\t<required>true</required>");
            }

            writeTldAttributes(out, pd);

            out.println("\t\t</attribute>");

            CameliaGenerator.attributesAutodoc.add(name);

        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            String eventAttributeName = convertEventName(eventName);

            out.println("\t\t<attribute>");

            AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                    attributesDoc, eventAttributeName);
            if (att != null) {
                att.writeTld(out);
            }

            out.println("\t\t\t<name>" + eventAttributeName + "</name>");

            writeTldEventAttributes(out, eventName);

            out.println("\t\t</attribute>");

            CameliaGenerator.attributesAutodoc.add(eventAttributeName);

        }
    }

    protected void writeTldValueAttributes(PrintWriter out) {
        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    protected void writeTldEventAttributes(PrintWriter out, String eventName) {

        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    protected void writeTldAttributes(PrintWriter out, PropertyDescriptor pd) {
        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    public String getPropertyValueType(String valueAttributeName) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            // String propertyName = Component.getPropertyName(pd.getName());
            if (pd.getName().equals(valueAttributeName)) {

                String clName = pd.getPropertyType().getName();
                if (clName.startsWith("java.lang.")) {
                    clName = clName.substring(10);
                }

                return clName;
            }
        }

        return null;
    }

    public Collection<String> listTagImports() {
        Set<String> set = new HashSet<String>();

        /*
         * for (Iterator it = events.iterator(); it.hasNext();) { // String
         * eventName=(String) it.next(); //
         * set.add(Component.COMPONENT_PACKAGE+".listener.I"+eventName); }
         */
        return set;
    }

    public void addGeneratedProperties(Set<String> propertiesAlreadyGenerated) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            propertiesAlreadyGenerated.add(pd.getName());
        }

        // Il faut scanner les methodes pour obtenir les events !
        listMethods();

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            propertiesAlreadyGenerated.add("event$" + eventName);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj instanceof Capability1_1) == false) {
            return false;
        }

        return ((Capability1_1) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void writeTagLogProperties(PrintWriter out,
            Map<String, Method1_1> methods2, String propertyValue,
            Set<String> propertiesAlreadyGenerated) {

        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            if (propertiesAlreadyGenerated.contains(pd.getName())) {
                continue;
            }

            java.lang.reflect.Method writeMethod = pd.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }

            String varName = getTagVarName(pd.getName());

            out.println("\t\t\tLOG.debug(\"  " + varName + "='\"+" + varName
                    + "+\"'\");");
        }
    }

    public void addGeneratedMethods(Set<String> methodsAlreadyGenerated) {
        Iterator<MethodDescriptor> it = listMethods();

        for (; it.hasNext();) {
            MethodDescriptor md = it.next();

            String sig = Method1_1.getSignature(md.getMethod());

            methodsAlreadyGenerated.add(sig);
        }
    }

    public void listCameliaAttributes(Set<String> set) {
        for (Iterator<PropertyDescriptor> it = listProperties(); it.hasNext();) {
            PropertyDescriptor pd = it.next();

            String name = pd.getName();
            if (name.startsWith("UI")) {
                name = "ui" + name.substring(2);
            }

            set.add(name);
        }

        for (Iterator<String> it = events.iterator(); it.hasNext();) {
            String eventName = it.next();

            eventName = Character.toLowerCase(eventName.charAt(0))
                    + eventName.substring(1);

            set.add(eventName);
        }
    }
}