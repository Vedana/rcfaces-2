/*
 * $Id: IBundleVarCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A string value specifying the name of a request scope attribute under which
 * the resource bundle will be exposed as a Map.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IBundleVarCapability {

    /**
     * Returns a string value specifying the name of a request scope attribute
     * under which the resource bundle will be exposed as a Map.
     * 
     * @return bundle var name
     */
    String getBundleVar();

    /**
     * Sets a string value specifying the name of a request scope attribute
     * under which the resource bundle will be exposed as a Map.
     * 
     * @param bundleVar
     *            bundle var name
     */
    void setBundleVar(String bundleVar);
}
