/*
 * $Id: IJsMethod.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.FunctionDeclaration;

public interface IJsMethod extends IJsMember {
    boolean isAbstract();

    FunctionDeclaration getDeclaration();

    // Si indirection, elle resoud l'indirection ...
    IJsMethod resolveDeclaration(JsStats stats);

    IJsParam[] listParams(JsStats stats);

    IJsParam[] resolveParams(JsStats stats);

    IJsReturn getReturn(JsStats stats);

    IJsReturn resolveReturn(JsStats stats);

    boolean resolvePublic(JsStats stats);

    boolean isBefore();

    boolean isAfter();

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
     * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
     */
    public interface IJsParam {

        String getName();

        boolean isHidden();

        boolean isOptional();

        IJsType getJsType();

        String getComment();
    }

    public interface IJsReturn {
        IJsType getJsType();

        String getComment();
    }
}
