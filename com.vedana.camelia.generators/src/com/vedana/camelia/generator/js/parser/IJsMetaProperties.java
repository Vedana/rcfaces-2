/*
 * $Id: IJsMetaProperties.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IJsMetaProperties {

    void putMetaProperty(String name, Object value);

    Object getMetaProperty(String name);

}
