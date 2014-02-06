/*
 * $Id: Attribute1_1.java,v 1.3 2012/12/07 13:45:23 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_1;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.Element;

import com.vedana.camelia.generator.components.AttributeDoc;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $
 */
public class Attribute1_1 {
    protected static final Set<String> ENUMERATIONS = Capability1_1.ENUMERATIONS;

    protected final String id;

    protected final String type;

    protected boolean writeComponent;

    protected boolean writeTag;

    protected boolean value;

    protected boolean isSetted;

    protected String defaultValue = null;

    protected String model;

    protected boolean clearCachedValue;

    protected String tagForceString;

    protected String clearVariable;

    protected boolean noValueBinding;

    protected boolean onlyValueBinding;

    protected boolean required;

    public Attribute1_1(Element xml) {
        this.id = xml.getAttributeValue("id");
        this.type = xml.getAttributeValue("type");

        this.writeComponent = ("false".equalsIgnoreCase(xml
                .getAttributeValue("writeComponent"))) == false;
        this.writeTag = ("false".equalsIgnoreCase(xml
                .getAttributeValue("writeTag"))) == false;
        this.isSetted = ("false".equalsIgnoreCase(xml
                .getAttributeValue("isSetted"))) == false;
        this.clearCachedValue = ("true".equalsIgnoreCase(xml
                .getAttributeValue("clearCachedValue")));
        this.defaultValue = xml.getAttributeValue("default");
        this.tagForceString = xml.getAttributeValue("tagForceType");

        this.noValueBinding = "true".equalsIgnoreCase(xml
                .getAttributeValue("noValueBinding"));
        this.onlyValueBinding = "true".equalsIgnoreCase(xml
                .getAttributeValue("onlyValueBinding"));
        this.required = "required".equalsIgnoreCase(xml
                .getAttributeValue("required"));

        this.value = ("true".equalsIgnoreCase(xml.getAttributeValue("value")));
        this.model = xml.getAttributeValue("model");

        this.clearVariable = xml.getAttributeValue("clearVariable");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Set<String> listImports() {
        Set<String> imports = new HashSet<String>();

        return imports;
    }

    public void writeSourceVariable(PrintWriter out) {
        if (writeComponent == false) {
            return;
        }

        if (value) {
            String varId = getVarId();

            out.println("\tprivate Object " + varId + " = null;");
            if (model != null) {
                out.println("\tprivate transient " + model + " " + varId
                        + "IndexesModel = null;");
            }
        }
    }

    public void writeSource(PrintWriter out, Map<String, String> properties,
            Map<String, String> events, Set<String> methodAlreadyDeclared,
            Set<String> propertiesAlreadyGenerated, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        if (writeComponent == false) {
            return;
        }

        String propertyName = Component1_1.getPropertyName(getId());
        properties.put(propertyName, CameliaGenerator.PROPERTY_PREFIX
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

                out.println("\t\treturn getValueBinding(Properties."
                        + propertyName + ");");

            } else if (clName.equals("String")) {
                out.print("\t\tString s = ");
                out.println("engine.getStringProperty(Properties."
                        + propertyName + ", " + facesContext + ");");
                if (defaultValue != null) {
                    out.print("\t\tif (s!=null) {");
                    out.print("\t\t\ts=\"" + defaultValue + "\";");
                    out.print("\t\t}");
                }

                out.println("\n\nreturn s;");

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

            } else if (clName.equals("float")) {
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

    public void writeTagVariable(PrintWriter out) {
        if (writeTag == false) {
            return;
        }

        String varId = getVarId();

        out.println("\tprivate String " + varId + ";");
    }

    public void writeTagGetterAndSetter(PrintWriter out) {
        if (writeTag == false) {
            return;
        }

        String methodName = getId();
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);

        String varId = getVarId();

        out.print("\tpublic void set");
        out.println(methodName + "(String " + varId + ") {");
        out.println("\t\tthis." + varId + " = " + varId + ";");
        out.println("\t}\n");
    }

    protected String getVarId() {
        String id = getId();

        if (id.equals("for")) {
            return "forVal";
        }

        return id;
    }

    public void writeTagRelease(PrintWriter out) {
        if (writeTag == false) {
            return;
        }

        String varId = getVarId();

        out.print("\t\t" + varId + " = ");
        /*
         * String clName = getType();
         * 
         * if (clName.equals("boolean")) { out.println("false;"); return; } if
         * (clName.equals("int")) { out.println("0;"); return; } if
         * (clName.equals("long")) { out.println("0l;"); return; } if
         * (clName.equals("double")) { out.println("0.0;"); return; } if
         * (clName.equals("float")) { out.println("0.0f;"); return; } if
         * (clName.equals("char")) { out.println("0;"); return; }
         */

        out.println("null;");

    }

    public void writeTagSetProperties(PrintWriter out, String propertyValue) {
        if (writeTag == false) {
            return;
        }

        String methodName = getId();
        methodName = Character.toUpperCase(methodName.charAt(0))
                + methodName.substring(1);
        String clName = getType();

        if (ENUMERATIONS.contains(getId())) {
            clName = "String";
        }

        String varId = getVarId();
        String propertyName = Component1_1.getPropertyName(getId());
        if (getId().equals(propertyValue)) {
            propertyName = "VALUE";
        }

        out.println("\n\t\tif (" + varId + " != null) {");
        out.println("\t\t\tif (isValueReference(" + varId + ")) {");
        if (noValueBinding == false) {
            out.println("\t\t\t\tValueBinding vb = application.createValueBinding("
                    + varId + ");");

            out.println("\t\t\t\tcomponent.setValueBinding(Properties."
                    + propertyName + ", vb);");
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
            out.print("getInt(" + varId + ")");

        } else if (clName.equals("long")) {
            out.print("getLong(" + varId + ")");

        } else if (clName.equals("boolean")) {
            out.print("getBool(" + varId + ")");

        } else if (clName.equals("Boolean")) {
            out.print("getBoolean(" + varId + ")");

        } else if (clName.equals("Number")) {
            out.print("getNumber(" + varId + ")");

        } else if (clName.equals("double")) {
            out.print("getDouble(" + varId + ")");

        } else {
            out.print(varId);
        }
        out.println(");");
        if (noValueBinding == false) {
            out.println("\t\t\t}");
        }
        out.println("\t\t}");

    }

    public void writeTld(PrintWriter out, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        out.println("\t\t<attribute>");

        AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, getId());
        if (att != null) {
            att.writeTld(out);
        }

        out.println("\t\t\t<name>" + getId() + "</name>");
        if (required) {
            out.println("\t\t\t<required>true</required>");
        }

        writeTldAttributes(out);

        out.println("\t\t</attribute>");

        CameliaGenerator.attributesAutodoc.add(getId());
    }

    protected void writeTldAttributes(PrintWriter out) {
        out.println("\t\t\t<rtexprvalue>true</rtexprvalue>");
    }

    public void writeTagLogProperties(PrintWriter out) {
        out.println("\t\t\tLOG.debug(\"  " + getVarId() + "='\"+" + getVarId()
                + "+\"'\");");
    }

    public void listCameliaAttributes(Set<String> set) {
        set.add(getId());
    }

    public void writeFacesConfig(PrintWriter out, ComponentDoc componentDoc,
            Map<String, AttributeDoc> attributesDoc) {
        out.println("\t\t<attribute>");

        AttributeDoc att = ComponentDoc.getAttributeDoc(componentDoc,
                attributesDoc, getId());
        if (att != null) {
            att.writeFacesConfig(out);
        }

        out.println("\t\t\t<attribute-name>" + getId() + "</attribute-name>");
        out.println("\t\t\t<attribute-class>" + type + "</attribute-class>");

        out.println("\t\t</attribute>");
    }
}