/*
 * $Id: JsMember.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Collection;

import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public abstract class JsMember extends JsMetaProperties implements IJsMember {

    protected final IJsClass jsClass;

    public final Expression value;

    public final ASTNode definition;

    public Collection<Expression> access = new ArrayList<Expression>();

    // public boolean frameworkPrivate;

    public JsModifier modifier;

    private JsComment comment;

    public JsMember(IJsClass jsClass, ASTNode definition, Expression value,
            JsModifier modifier, JsComment comment) {
        assert jsClass != null : "JsClass is NULL";

        this.jsClass = jsClass;
        this.definition = definition;
        this.value = value;
        // this.frameworkPrivate = frameworkPrivate;
        this.modifier = modifier;
        this.comment = comment;
    }

    public String getName() {
        return modifier.name;
    }

    public JsComment getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "[Member name=" + modifier.name + " definition=" + definition
                + " value=" + value + " modifier=" + modifier.formatFlags()
                + "]";
    }

    public void setComment(JsComment comment) {
        this.comment = comment;
    }

    public IJsClass getJsClass() {
        return jsClass;
    }

    public boolean isFinal() {
        return modifier.isFinal();
    }

    public boolean isMemberOrClassFinal() {
        if (isFinal()) {
            return true;
        }

        return jsClass.isFinal();
    }

    public boolean isPrivate() {
        return modifier.isPrivate();
    }

    public boolean isProtected() {
        return modifier.isProtected();
    }

    public boolean isPublic() {
        return modifier.isPublic();
    }

    public boolean isStatic() {
        return modifier.isStatic();
    }

    public String getMetaDoc(String name) {
        return modifier.getMeta(name);
    }

    public int getDecorationMask() {
        return modifier.getDecoration();
    }

}