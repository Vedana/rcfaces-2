/*
 * $Id: IJsField.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.Expression;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IJsField extends IJsMember {

    IJsType getType(JsStats stats);

    Expression getExpression();
}
