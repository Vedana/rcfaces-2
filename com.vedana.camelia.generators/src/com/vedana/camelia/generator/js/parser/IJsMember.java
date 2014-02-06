/*
 * $Id: IJsMember.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IJsMember extends IJsMetaProperties {
    IJsClass getJsClass();

    String getName();

    boolean isFinal();

    boolean isMemberOrClassFinal();

    boolean isPublic();

    boolean isProtected();

    boolean isPrivate();

    boolean isStatic();

    String getMetaDoc(String name);

    int getDecorationMask();

    JsComment getComment();
}
