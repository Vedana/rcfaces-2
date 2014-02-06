/*
 * $Id: JsModifier.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.vedana.js.dom.ASTNode;

public final class JsModifier {

    public static final int METHOD_TYPE = 0;

    public static final int FIELD_TYPE = 1;

    public static final int CLASS_TYPE = 2;

    public static final int ASPECT_TYPE = 3;

    public static final int OPTIONAL_ABSTRACT = 0x100000;

    public static final int BEFORE = 0x200000;

    public static final int AFTER = 0x400000;

    public static final int THROWING = 0x800000;

    public static final int DECORATION_MASK = 0xE00000;

    public final String className;

    public final String name;

    public final ASTNode node;

    private final int modifier;

    private final int type;

    private Map<String, String> metas = new HashMap<String, String>();

    public JsModifier(String className, String name, ASTNode node, int _type,
            int modifier) { // , boolean _static, boolean _final, boolean
        // _abstract) {
        this.name = name;
        this.className = className;
        this.node = node;

        this.modifier = modifier;
        this.type = _type;
    }

    public boolean isPrivate() {
        return ((modifier & Modifier.PRIVATE) > 0);
    }

    public boolean isAbstract() {
        return ((modifier & Modifier.ABSTRACT) > 0);
    }

    public boolean isOptionalAbstract() {
        return ((modifier & OPTIONAL_ABSTRACT) > 0);
    }

    public boolean isFinal() {
        return ((modifier & Modifier.FINAL) > 0);
    }

    public boolean isPublic() {
        return ((modifier & Modifier.PUBLIC) > 0);
    }

    public boolean isProtected() {
        return ((modifier & Modifier.PROTECTED) > 0);
    }

    public boolean isHidden() {
        return getAccessibleModifier() == 0;
    }

    public boolean isStatic() {
        return ((modifier & Modifier.STATIC) > 0);
    }

    public int getAccessibleModifier() {
        return modifier
                & (Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC);
    }

    public boolean isBefore() {
        return ((modifier & BEFORE) > 0);
    }

    public boolean isAfter() {
        return ((modifier & AFTER) > 0);
    }

    public boolean isThrowing() {
        return ((modifier & THROWING) > 0);
    }

    public String formatFlags() {
        StringBuffer sb = new StringBuffer(256);

        if (isPublic()) {
            sb.append(" public");
        }

        if (isProtected()) {
            sb.append(" protected");
        }

        if (isPrivate()) {
            sb.append(" private");
        }

        if (isHidden()) {
            sb.append(" hidden");
        }

        if (isStatic()) {
            sb.append(" static");
        }

        if (isOptionalAbstract()) {
            sb.append(" optional_abstract");

        } else if (isAbstract()) {
            sb.append(" abstract");
        }

        if (isFinal()) {
            sb.append(" final");
        }

        if (isBefore()) {
            sb.append(" (before)");
        }

        if (isAfter()) {
            sb.append(" (after)");
        }

        if (isThrowing()) {
            sb.append(" (exception)");
        }

        return sb.toString();
    }

    public boolean isFieldType() {
        return type == FIELD_TYPE;
    }

    public boolean isMethodType() {
        return type == METHOD_TYPE;
    }

    public int getDecoration() {
        return modifier & DECORATION_MASK;
    }

    public void setMetas(Map<String, String> metas) {
        this.metas = metas;
    }

    public String getMeta(String name) {
        if (metas == null) {
            return null;
        }
        return metas.get(name.toLowerCase());
    }

    public boolean hasMeta(String name) {
        if (metas == null) {
            return false;
        }
        return metas.containsKey(name.toLowerCase());
    }

    public JsModifier changeFlags(JsModifier modifier) {
        return new JsModifier(className, name, node, type, modifier.modifier);
    }
}