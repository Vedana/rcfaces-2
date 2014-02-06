/*
 * $Id: IWheelSelectionCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Fred (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IWheelSelectionCapability {

    /**
     * Returns a boolean value indicating wether the mouse wheel change the selection
     * 
     * @return wheelSelection boolean property
     */
    boolean isWheelSelection();

    /**
     * Sets a boolean value indicating wether the component mouse wheel change the selection
     * 
     * @param wheelSelection
     *            wheelSelection boolean property
     */
    void setWheelSelection(boolean wheelSelection);
}
