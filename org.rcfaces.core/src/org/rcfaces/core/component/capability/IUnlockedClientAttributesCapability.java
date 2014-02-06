/*
 * $Id: IUnlockedClientAttributesCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IUnlockedClientAttributesCapability {

    /**
     * Returns a string value holding a comma separated list of the client
     * modifiable properties.
     * 
     * @return list of modifiable attributes
     */
    String getUnlockedClientAttributeNames();

    /**
     * Sets a string value holding a comma separated list of the client
     * modifiable properties.
     * 
     * @param lock
     *            list of modifiable attributes
     */
    void setUnlockedClientAttributeNames(String lock);
}
