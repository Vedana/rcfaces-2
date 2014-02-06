/*
 * $Id: JsConstantField.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Expression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public class JsConstantField extends JsField {

    public JsConstantField(IJsClass jsClass, ASTNode definition,
            Expression value, boolean _private, JsModifier modifier,
            JsComment comment) {
        super(jsClass, definition, value, modifier, comment);
    }
}