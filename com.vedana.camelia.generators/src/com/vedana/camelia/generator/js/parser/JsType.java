/*
 * $Id: JsType.java,v 1.2 2013/11/14 14:08:48 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsType implements IJsType {

    private final int arrayDepth;

    private final IJsClass jsClass;

    private final IJsType[] templates;

    private final boolean multiple;

    private final boolean canBeTemplateType;

    public JsType(IJsClass jsClass, IJsType templates[], int arrayDepth,
            boolean multiple, boolean canBeTemplateType) {

        assert jsClass != null : "JsClass is null";

        this.jsClass = jsClass;
        this.templates = templates;
        this.arrayDepth = arrayDepth;

        this.multiple = multiple;
        this.canBeTemplateType = canBeTemplateType;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public IJsClass getJsClass() {
        return jsClass;
    }

    public IJsType[] listTemplates() {
        return templates;
    }

    public static IJsType parse(JsStats stats, String className,
            IJsClass mainClass, boolean canBeParameterType) {
        boolean multiple = false;
        if (className.endsWith("...")) {
            className = className.substring(0, className.length() - 3).trim();

            multiple = true;
        }

        int arrayDepth = 0;

        int idx = className.indexOf('<');
        int idx2 = className.lastIndexOf('>');

        List<IJsType> templates = new ArrayList<IJsType>();

        if (idx >= 0 && idx2 > idx) {
            String templatesNames = className.substring(idx + 1, idx2);
            className = className.substring(0, idx)
                    + className.substring(idx2 + 1);

            TypeTokenizer st = new TypeTokenizer(templatesNames, ",");
            for (; st.hasMoreTokens();) {
                templates.add(parse(stats, st.nextToken(), mainClass, true));
            }
        }

        for (;;) {
            idx = className.lastIndexOf('[');
            if (idx < 0) {
                break;
            }
            className = className.substring(0, idx);
            arrayDepth++;
        }

        if (mainClass != null) {
            for (IJsType jsType : mainClass.listParameters()) {
                if (jsType.getJsClass().getName().equals(className)) {
                    return jsType;
                }
            }
        }

        if (canBeParameterType && stats.containsClass(className) == false) {
            return new JsType(new JsClass(className, false), new IJsType[0], 0,
                    multiple, canBeParameterType);
        }

        IJsClass jsClass = stats.getJsClass(className);
        assert jsClass != null : "Invalid className='" + className + "'";

        return new JsType(jsClass, templates.toArray(new IJsType[templates
                .size()]), arrayDepth, multiple, canBeParameterType);
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(IJsClass relativeClass) {
        StringBuilder sb = new StringBuilder(128);

        sb.append(jsClass.toString(relativeClass, false));

        if (templates.length > 0) {

            sb.append('<');

            for (int i = 0; i < templates.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(templates[i].toString(relativeClass));
            }
            sb.append('>');
        }

        for (int i = 0; i < arrayDepth; i++) {
            sb.append("[]");
        }

        if (multiple) {
            sb.append("...");
        }

        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + arrayDepth;
        result = prime * result + ((jsClass == null) ? 0 : jsClass.hashCode());
        result = prime * result + (multiple ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(templates);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JsType other = (JsType) obj;
        if (arrayDepth != other.arrayDepth)
            return false;
        if (jsClass == null) {
            if (other.jsClass != null)
                return false;
        } else if (!jsClass.equals(other.jsClass))
            return false;
        if (multiple != other.multiple)
            return false;
        if (!Arrays.equals(templates, other.templates))
            return false;
        return true;
    }

}
