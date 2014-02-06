/*
 * $Id: Method1_1.java,v 1.2 2012/12/07 13:45:23 oeuillot Exp $
 * 
 * $Log: Method1_1.java,v $
 * Revision 1.2  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.1  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/12/18 15:14:02  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import nu.xom.Element;
import nu.xom.Elements;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.2 $
 */
public class Method1_1 {
    private final String id;

    private final String returnType;

    private final String javadoc;

    private final String code;

    private final Set<String> imports = new HashSet<String>();

    private final List<Parameter> parameters = new ArrayList<Parameter>();

    private final List<String> exceptions = new ArrayList<String>();

    private final List<Field> fields = new ArrayList<Field>();

    private final List<String> properties = new ArrayList<String>();

    private final boolean finalModifier;

    private String signature;

    private String modifier;

    private boolean overrideAnnotation;

    public Method1_1(Element xml) {
        this.id = xml.getAttributeValue("id");

        this.modifier = xml.getAttributeValue("modifier");
        if (modifier == null) {
            modifier = "public";
        }

        String r = xml.getAttributeValue("returnType");
        if (r != null) {
            int idx = r.lastIndexOf('.');
            if (idx > 0) {
                String cl = r;
                int idxDim = cl.indexOf('[');
                if (idxDim > 0) {
                    cl = cl.substring(0, idxDim);
                }
                int idxGene = cl.indexOf('<');
                if (idxGene > 0) {
                    cl = cl.substring(0, idxGene);
                }

                imports.add(cl);
                r = r.substring(idx + 1);
            }
        } else {
            r = "void";
        }
        returnType = r;

        Element javadocXml = xml.getFirstChildElement("javadoc");
        if (javadocXml != null) {
            this.javadoc = CameliaGenerator.formatComment(
                    javadocXml.getValue(), 1);

        } else {
            this.javadoc = null;
        }
        Element codeXml = xml.getFirstChildElement("code");
        if (codeXml != null) {
            this.code = codeXml.getValue();

        } else {
            this.code = null;
        }

        Elements importsXml = xml.getChildElements("import");
        for (int i = 0; i < importsXml.size(); i++) {
            Element importXml = importsXml.get(i);

            imports.add(importXml.getValue());
        }

        Elements propertiesXml = xml.getChildElements("property");
        for (int i = 0; i < propertiesXml.size(); i++) {
            Element propertyXml = propertiesXml.get(i);

            properties.add(propertyXml.getValue());
        }

        Elements throwsXml = xml.getChildElements("throws");
        for (int i = 0; i < throwsXml.size(); i++) {
            Element throwXml = throwsXml.get(i);

            r = throwXml.getValue();
            int idx = r.lastIndexOf('.');
            if (idx > 0) {
                imports.add(r);
                r = r.substring(idx + 1);
            }
            exceptions.add(r);
        }

        Elements parametersXml = xml.getChildElements("parameter");
        for (int i = 0; i < parametersXml.size(); i++) {
            Element parameterXml = parametersXml.get(i);

            String name = parameterXml.getAttributeValue("name");
            String type = parameterXml.getAttributeValue("type");

            type = convertType(type);

            String cl = type;
            int dimension = 0;
            int idx = type.lastIndexOf('.');
            if (idx > 0) {
                for (;;) {
                    int idxDim = cl.lastIndexOf('[');
                    if (idxDim < 0) {
                        break;
                    }
                    int idxGene = cl.indexOf('<');
                    if (idxGene > 0) {
                        cl = cl.substring(0, idxGene);
                    }

                    cl = cl.substring(0, idxDim);
                    dimension++;
                }

                imports.add(cl);

            } else if (Character.isUpperCase(type.charAt(0))) {
                cl = "java.lang." + type;
            }

            parameters.add(new Parameter(cl, name, dimension));
        }

        Elements fieldsXml = xml.getChildElements("field");
        for (int i = 0; i < fieldsXml.size(); i++) {
            Element fieldXml = fieldsXml.get(i);

            String name = fieldXml.getAttributeValue("name");
            String type = fieldXml.getAttributeValue("type");

            int idx = type.lastIndexOf('.');
            if (idx > 0) {
                String cl = type;
                int idxDim = cl.indexOf('[');
                if (idxDim > 0) {
                    cl = cl.substring(0, idxDim);
                }
                int idxGene = cl.indexOf('<');
                if (idxGene > 0) {
                    cl = cl.substring(0, idxGene);
                }

                imports.add(cl);
            } else if (Character.isUpperCase(type.charAt(0))) {
                type = "java.lang." + type;
                // type = type.substring(idx + 1);
            }

            fields.add(new Field(type, name));
        }

        this.finalModifier = ("true".equalsIgnoreCase(xml
                .getAttributeValue("final")));

        this.overrideAnnotation = ("true".equalsIgnoreCase(xml
                .getAttributeValue("override")));

    }

    protected String convertType(String type) {

        return type;
    }

    public String getId() {
        return id;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getCode() {
        return code;
    }

    public Set<String> listImports() {
        return imports;
    }

    public void writeSource(PrintWriter out) {
        if (javadoc != null) {
            out.println("\t/**");
            out.println(javadoc);
            out.println("\t */");
        }

        if (overrideAnnotation) {
            out.println("\t@Override");
        }

        out.print("\t" + modifier);

        if (finalModifier) {
            out.print(" final");
        }

        out.print(" " + getReturnType() + " " + getId() + "(");

        for (Iterator<Parameter> it = parameters.iterator(); it.hasNext();) {
            Parameter parameter = it.next();

            String type = parameter.getType();
            int idx = type.lastIndexOf('.');
            if (idx > 0) {
                imports.add(type);
                type = type.substring(idx + 1);
            }

            for (int dimension = parameter.dimension; dimension > 0; dimension--) {
                type += "[]";
            }

            out.print(type + " " + parameter.getName());

            if (it.hasNext()) {
                out.print(", ");
            }
        }
        out.print(")");

        Iterator<String> it = exceptions.iterator();
        if (it.hasNext()) {
            out.print(" throws ");
            for (; it.hasNext();) {
                String exception = it.next();

                out.print(exception);

                if (it.hasNext()) {
                    out.println(", ");
                }
            }
        }

        out.println(" {\n");

        if (code != null) {

            String c = transformCode(code);

            out.println(c);
        }

        out.println("\t}\n");
    }

    protected String transformCode(String code) {
        return code;
    }

    private static class Parameter {

        private final String type;

        private final String name;

        private final int dimension;

        public Parameter(String type, String name, int dimension) {
            this.type = type;
            this.name = name;
            this.dimension = dimension;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public final int getDimension() {
            return dimension;
        }

    }

    private static class Field {

        private final String type;

        private final String name;

        public Field(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * @param out
     */
    public void writeTagVariable(PrintWriter out) {
    }

    /**
     * @param out
     */
    public void writeTagRelease(PrintWriter out) {
    }

    public void writeComponentFields(PrintWriter out) {
        Iterator<Field> it = fields.iterator();
        if (it.hasNext() == false) {
            return;
        }

        for (; it.hasNext();) {
            Field field = it.next();

            out.println("\tprivate " + field.getType() + " " + field.getName()
                    + ";");
        }

        out.println();
    }

    public void writeComponentRelease(PrintWriter out) {
        for (Iterator<Field> it = fields.iterator(); it.hasNext();) {
            Field field = it.next();

            out.println("\t\t" + field.getName() + " = null;");
        }
    }

    public String getSignature() {
        if (signature != null) {
            return signature;
        }

        StringBuffer sb = new StringBuffer(256);
        sb.append(getId());
        sb.append('(');

        for (Iterator<Parameter> it = parameters.iterator(); it.hasNext();) {
            Parameter p = it.next();
            String type = p.getType();

            for (int dimension = p.dimension; dimension > 0; dimension--) {
                sb.append('[');
            }

            if (type.indexOf('.') > 0) {
                type = "L" + type + ";";
            }

            sb.append(type);

            if (it.hasNext()) {
                sb.append(',');
            }
        }

        sb.append(')');

        signature = sb.toString();

        return signature;
    }

    public static String getSignature(java.lang.reflect.Method method) {
        StringBuffer sb = new StringBuffer(256);
        sb.append(method.getName());
        sb.append('(');

        Class< ? > cs[] = method.getParameterTypes();
        for (int i = 0; i < cs.length; i++) {
            if (i > 0) {
                sb.append(',');
            }

            String r = cs[i].getName();

            if (r.indexOf("[") < 0 && r.indexOf('.') > 0) {
                r = "L" + r + ";";
            }

            sb.append(r);
        }

        sb.append(')');

        return sb.toString();

    }

    @Override
    public boolean equals(Object object) {
        if (object == null || (object instanceof Method1_1) == false) {
            return false;
        }

        return ((Method1_1) object).getSignature().equals(getSignature());
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

    public static String normalizeMethodId(String s) {
        int idx = s.indexOf('(');

        String n = s.substring(0, idx + 1);

        StringTokenizer st = new StringTokenizer(s.substring(idx + 1,
                s.length() - 1), ", ", true);

        Boolean nextParam = null;
        for (; st.hasMoreTokens();) {
            String param = st.nextToken();

            if (param.equals(",")) {
                if (nextParam == Boolean.FALSE) {
                    throw new IllegalStateException("Invalid method id '" + s
                            + "'.");
                }
                n += ",";
                nextParam = Boolean.FALSE;
                continue;
            }

            if (param.equals(" ")) {
                continue;
            }

            if (nextParam == Boolean.TRUE) {
                throw new IllegalStateException("Invalid method id '" + s
                        + "'.");
            }

            if (Character.isUpperCase(param.charAt(0))
                    && param.indexOf('.') < 0) {
                param = "java.lang." + param;
            }

            int dimension = 0;
            for (;;) {
                int idx2 = param.lastIndexOf('[');
                if (idx2 < 0) {
                    break;
                }

                param = param.substring(0, idx2);
                dimension++;
            }

            if (param.indexOf('.') > 0) {
                param = "L" + param + ";";
            }

            for (; dimension > 0; dimension--) {
                param = "[" + param;
            }

            n += param;
            nextParam = Boolean.TRUE;

        }

        if (nextParam == Boolean.FALSE) {
            throw new IllegalStateException("Invalid method name '" + s + "'.");
        }

        return n + ")";
    }

    public String[] listProperties() {
        return properties.toArray(new String[properties.size()]);
    }
}
