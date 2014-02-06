/*
 * $Id: IJsType.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IJsType {
    IJsClass getJsClass();

    int getArrayDepth();

    IJsType[] listTemplates();

    String toString(IJsClass relativeClass);
}
