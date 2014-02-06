/*
 * $Id: Attribute1_2.java,v 1.6 2012/12/07 13:45:23 oeuillot Exp $
 * 
 * $Log: Attribute1_2.java,v $
 * Revision 1.6  2012/12/07 13:45:23  oeuillot
 * Pour JB
 *
 * Revision 1.4  2011/10/12 15:54:00  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.3  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le g�n�rateur JS->JAVA
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
package com.vedana.camelia.generator.components_1_2;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components_1_1.Attribute1_1;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.Method1_1;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.6 $
 */
public class Attribute1_2 extends Attribute1_1 {

    public Attribute1_2(Element xml) {
        super(xml);
    }

    @Override
    public void writeSource(PrintWriter out, Map<String, String> properties,
            Map<String, String> events, Set<String> methodAlreadyDeclared,
            Set<String> propertiesAlreadyGenerated, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        if (writeComponent == false) {
            return;
        }

        // On garde

        String propertyName = Component1_1.getPropertyName(getId());
        properties.put(propertyName, CameliaGenerator1_2.PROPERTY_PREFIX
                + Component1_1.lowerCaseFirstChar(getId()));

        AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, getId());

        String clName = getType();
        if (clName.startsWith("java.lang.")) {
            clName = clName.substring(10);
        }

        String methodName = getId();
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);

        String prefix = "get";
        if (clName.equals("boolean")) {
            prefix = "is";
        }

        String varId = getVarId();
        String facesContext = "null";

        String sig = prefix + methodName + "()";
        if (methodAlreadyDeclared.add(sig)) {
            if (att != null) {
                String getterDoc = att.getGetterDoc();
                if (getterDoc != null) {
                    out.println("\t/**");
                    out.println(getterDoc);
                    out.println("\t */");
                }
            }

            out.print("\tpublic " + clName + " ");
            out.println(prefix + methodName + "() {");

            out.println("\t\treturn " + prefix + methodName + "(null);");
            out.println("\t}\n");
        } else {
            System.out.println();

        }

        sig = prefix + methodName + "(Ljavax.faces.context.FacesContext;)";
        if (methodAlreadyDeclared.add(sig)) {
            if (att != null) {
                String getterDoc = att.getGetterDoc();
                if (getterDoc != null) {
                    out.println("\t/**");
                    out.println(getterDoc);
                    out.println("\t */");
                }
            }

            out.print("\tpublic " + clName + " ");

            out.println(prefix + methodName
                    + "(javax.faces.context.FacesContext facesContext) {");
            facesContext = "facesContext";

            if (value) {
                out.println("\t\tif (" + varId + " != null) {");
                out.println("\t\t\treturn " + varId + ";");
                out.println("\t\t}");

                out.println("\t\treturn getValueExpression(Properties."
                        + propertyName + ");");

            } else if (clName.equals("String")) {
                out.print("\t\tString s = ");
                out.println("engine.getStringProperty(Properties."
                        + propertyName + ", " + facesContext + ");");
                if (defaultValue != null) {
                    out.println("\t\tif (s == null) {");
                    out.println("\t\t\ts = \"" + defaultValue + "\";");
                    out.println("\t\t}");

                    System.out.println("USE STRING DEFAULT (" + defaultValue
                            + ")");
                }

                out.println("\t\treturn s;");

            } else if (clName.equals("int")) {
                out.print("\t\treturn ");
                out.print("engine.getIntProperty(Properties." + propertyName);
                if (defaultValue == null) {
                    defaultValue = "0";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(", " + facesContext + ");");

            } else if (clName.equals("double")) {
                out.print("\t\treturn ");
                out.print("engine.getDoubleProperty(Properties." + propertyName);
                if (defaultValue == null) {
                    defaultValue = "0.0";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(", " + facesContext + ");");

            } else if (clName.equals("boolean")) {
                out.print("\t\treturn ");
                out.print("engine.getBoolProperty(Properties." + propertyName);
                if (defaultValue == null) {
                    defaultValue = "false";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(", " + facesContext + ");");

            } else if (clName.equals("Boolean")) {
                out.print("\t\treturn ");
                out.println("engine.getBooleanProperty(Properties."
                        + propertyName + ", " + facesContext + ");");

            } else if (clName.equals("Object")) {
                out.print("\t\treturn ");
                out.println("engine.getValue(Properties." + propertyName + ", "
                        + facesContext + ");");

            } else { // if (clName.equals("Object[]")) {
                out.print("\t\treturn ");
                out.println("(" + clName + ")engine.getValue(Properties."
                        + propertyName + ", " + facesContext + ");");

            }
            out.println("\t}\n");
        }

        sig = "set" + methodName + "(L" + getType() + ";)";
        if (methodAlreadyDeclared.add(sig)) {
            if (att != null) {
                String setterDoc = att.getSetterDoc();
                if (setterDoc != null) {
                    out.println("\t/**");
                    out.println(setterDoc);
                    out.println("\t */");
                }
            }

            out.println("\tpublic void set" + methodName + "(" + clName + " "
                    + varId + ") {");
            if (value) {
                out.println("\t\tthis." + varId + " = " + varId + ";");
                if (model != null) {
                    out.println("\t\tthis." + varId + "IndexesModel = null;");
                }
            } else if (clName.equals("Object")) {
                out.println("\t\tengine.setValue(Properties." + propertyName
                        + ", " + varId + ");");
            } else {
                out.println("\t\tengine.setProperty(Properties." + propertyName
                        + ", " + varId + ");");
            }

            if (clearVariable != null) {
                out.println("\t\tthis." + clearVariable + "=" + varId + ";");
            }
            if (clearCachedValue) {
                out.println("\t\tclearCachedValue();");
            }
            out.println("\t}\n");

            if (att != null) {
                String setterDoc = att.getSetterDoc();
                if (setterDoc != null) {
                    out.println("\t/**");
                    out.println(setterDoc);
                    out.println("\t */");
                }
            }
        }

        if (isSetted) {

            sig = Method1_1.normalizeMethodId("is" + methodName + "Setted()");
            if (methodAlreadyDeclared.add(sig)) {
                out.println("\t/**");
                out.println("\t * Returns <code>true</code> if the attribute \""
                        + getId() + "\" is set.");
                out.println("\t * @return <code>true</code> if the attribute is set.");
                out.println("\t */");

                out.println("\tpublic boolean is" + methodName + "Setted() {");
                out.println("\t\treturn engine.isPropertySetted(Properties."
                        + propertyName + ");");
                out.println("\t}\n");
            }
        }

    }

    @Override
    public void writeTagVariable(PrintWriter out) {
        if (writeTag == false) {
            return;
        }
        // On garde
        String varId = getVarId();

        out.println("\tprivate ValueExpression " + varId + ";");
    }

    @Override
    public void writeTagGetterAndSetter(PrintWriter out) {
        if (writeTag == false) {
            return;
        }
        // On garde
        String methodName = getId();
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);

        String varId = getVarId();

        out.print("\tpublic void set");
        out.println(methodName + "(ValueExpression " + varId + ") {");
        out.println("\t\tthis." + varId + " = " + varId + ";");
        out.println("\t}\n");
    }

    @Override
    public void writeTagSetProperties(PrintWriter out, String propertyValue) {
        if (writeTag == false) {
            return;
        }
        // On garde
        String methodName = getId();
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);
        String clName = getType();

        if (ENUMERATIONS.contains(getId())) {
            clName = "String";
        }

        String varId = getVarId();
        String propertyName = Component1_2.getPropertyName(getId());
        if (getId().equals(propertyValue)) {
            propertyName = "VALUE";
        }

        out.println("\n\t\tif (" + varId + " != null) {");
        out.println("\t\t\tif (" + varId + ".isLiteralText()==false) {");
        if (noValueBinding == false) {
            out.println("\t\t\t\tcomponent.setValueExpression(Properties."
                    + propertyName + ", " + varId + ");");
            out.println();
            out.println("\t\t\t} else {");
        } else {
            out.println("\t\t\t\tthrow new javax.faces.FacesException(\"Attribute '"
                    + id + "' does not accept binding !\");");
            out.println("\t\t\t}");
        }

        if (onlyValueBinding) {
            out.println("\t\t\t\tthrow new javax.faces.FacesException(\"Attribute '"
                    + id + "' accept only a binding expression !\");");
            out.println("\t\t\t}");
            out.println("\t\t}");

            return;
        }

        out.print("\t\t\t\tcomponent.set" + methodName + "(");

        if (clName.startsWith("java.lang.")) {
            clName = clName.substring(10);
        }

        if (tagForceString != null) {
            clName = tagForceString;
        }

        if (clName.equals("int")) {
            out.print("getInt(" + varId + ".getExpressionString())");

        } else if (clName.equals("long")) {
            out.print("getLong(" + varId + ".getExpressionString())");

        } else if (clName.equals("boolean")) {
            out.print("getBool(" + varId + ".getExpressionString())");

        } else if (clName.equals("Boolean")) {
            out.print("getBoolean(" + varId + ".getExpressionString())");

        } else if (clName.equals("Number")) {
            out.print("getNumber(" + varId + ".getExpressionString())");

        } else if (clName.equals("double")) {
            out.print("getDouble(" + varId + ".getExpressionString())");

        } else {
            out.print(varId + ".getExpressionString()");
        }
        out.println(");");
        if (noValueBinding == false) {
            out.println("\t\t\t}");
        }
        out.println("\t\t}");
    }

    @Override
    protected void writeTldAttributes(PrintWriter out) {
        // super.writeTldAttributes(out, required2); // Non on fait pas de
        // rtexprvalue

        out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");

        String clName = getType();

        if (clName == null) {
            out.println("\t\t\t<deferred-value />");

        } else {
            if (ENUMERATIONS.contains(getId())) {
                clName = "java.lang.String";
            }
            if ("Number".equals(clName) || "java.lang.Number".equals(clName)) {
                clName = "double";
            }

            out.println("\t\t\t<deferred-value>");
            out.println("\t\t\t\t<type>" + clName + "</type>");
            out.println("\t\t\t</deferred-value>");
        }
    }
    /*
     * <deferred-value> <type>java.lang.Stringw
     */

}