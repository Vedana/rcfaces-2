/*
 * $Id: ICloseEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ICloseListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ICloseEventCapability {

    /**
     * Adds a listener to the component for the close event
     * 
     * @param facesListener
     *            the close listener to add
     */
    void addCloseListener(ICloseListener facesListener);

    /**
     * Removes a listener from the component for the close event
     * 
     * @param facesListener
     *            the close listener to remove
     */
    void removeCloseListener(ICloseListener facesListener);

    /**
     * Returns a list of close listener for the component
     * 
     * @return close listeners' list
     */
    FacesListener[] listCloseListeners();
}
