/*
 * $Id: IClientSelectionFullStateCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IClientSelectionFullStateCapability extends
        IClientFullStateCapability {

    /**
     * Returns a boolean value indicating whether the client should know about
     * the component's full state even if only a part of the data is present
     * (AJAX).
     * 
     * @return boolean
     */
    int getClientSelectionFullState();

    /**
     * Sets a boolean value indicating whether the client should know about the
     * component's full state even if only a part of the data is present (AJAX).
     * 
     * @param clientSelectionFullState
     *            boolean
     */
    void setClientSelectionFullState(int clientSelectionFullState);
}
