/*
 * $Id: JsPrivateStaticMember.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;

public class JsPrivateStaticMember extends JsField {

    public JsPrivateStaticMember(IJsClass jsClass, ASTNode definition,
            Expression value, boolean _private, JsModifier modifier,
            JsComment comment) {
        super(jsClass, definition, value, modifier, comment);
    }
}