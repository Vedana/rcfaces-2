/*
 * $Id: IDropCompleteEventCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IDropCompleteListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IDropCompleteEventCapability {

    /**
     * Adds a listener to the component for the drop event
     * 
     * @param facesListener
     *            the drop listener to add
     */
    void addDropCompleteListener(IDropCompleteListener facesListener);

    /**
     * Removes a listener from the component for the drop event
     * 
     * @param facesListener
     *            the drop listener to remove
     */
    void removeDropCompleteListener(IDropCompleteListener facesListener);

    /**
     * Returns a list of drop listeners for the component
     * 
     * @return drop listeners' list
     */
    FacesListener[] listDropCompleteListeners();
}