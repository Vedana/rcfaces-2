/*
 * $Id: Attribute2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 * 
 * $Log: Attribute2_2.java,v $
 * Revision 1.1  2014/02/05 16:03:40  jbmeslin
 * passage en versio, 2_2 + maven
 *
 * Revision 1.3  2013/12/19 15:48:18  jbmeslin
 * Correction des fonctions IsXxxSetted
 *
 * Revision 1.2  2013/01/28 16:39:46  jbmeslin
 * Ajout du constructeur dans l'énumération des porperties
 *
 * Revision 1.1  2011/10/12 15:54:01  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.3  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le gï¿½nï¿½rateur JS->JAVA
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
package com.vedana.camelia.generator.components_2_2;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components_1_1.Component1_1;
import com.vedana.camelia.generator.components_1_1.ComponentDoc;
import com.vedana.camelia.generator.components_1_1.Method1_1;
import com.vedana.camelia.generator.components_1_2.Attribute1_2;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.1 $
 */
public class Attribute2_2 extends Attribute1_2 {

    public Attribute2_2(Element xml) {
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
        properties.put(propertyName, getId());

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

            if (value) {
                out.println("\t\tif (" + varId + " != null) {");
                out.println("\t\t\treturn " + varId + ";");
                out.println("\t\t}");

                out.println("\t\treturn getValueExpression(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ");");

            } else if (clName.equals("String")) {
                out.print("\t\treturn ");
                out.println("(String)getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ");");

            } else if (clName.equals("int")) {
                out.print("\t\treturn ");
                out.print("(Integer)getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName));
                if (defaultValue == null) {
                    defaultValue = "0";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(");");

            } else if (clName.equals("double")) {
                out.print("\t\treturn ");
                out.print("(Double)getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName));
                if (defaultValue == null) {
                    defaultValue = "0.0";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(");");

            } else if (clName.equals("boolean")) {
                out.print("\t\treturn ");
                out.print("(Boolean)getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName));
                if (defaultValue == null) {
                    defaultValue = "false";
                }
                if (defaultValue != null) {
                    out.print(", " + defaultValue);
                }
                out.println(");");

            } else if (clName.equals("Boolean")) {
                out.print("\t\treturn ");
                out.println("(Boolean)getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ");");

            } else if (clName.equals("Object")) {
                out.print("\t\treturn ");
                out.println("getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ");");

            } else { // if (clName.equals("Object[]")) {
                out.print("\t\treturn ");
                out.println("("
                        + clName
                        + ")getStateHelper().eval(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ");");

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
                out.println("\t\t getStateHelper().put(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ", "
                        + varId + ");");
            } else {
                out.println("\t\t getStateHelper().put(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName) + ", "
                        + varId + ");");
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
                out.println("\t\treturn this.getValueExpression(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName)
                        + ")!=null");
                
                out.println(" \t\t\t || getStateHelper().get(Properties."
                        + CameliaGenerator2_2
                                .convertAttributeName(propertyName)
                        + ")!=null;");
                
                out.println("\t}\n");
            }
        }

    }
}