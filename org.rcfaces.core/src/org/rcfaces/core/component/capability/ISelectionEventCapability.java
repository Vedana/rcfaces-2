/*
 * $Id: ISelectionEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ISelectionListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ISelectionEventCapability {

    /**
     * Adds a listener to the component for the selection event
     * 
     * @param facesListener
     *            the selection listener to add
     */
    void addSelectionListener(ISelectionListener facesListener);

    /**
     * Removes a listener from the component for the selection event
     * 
     * @param facesListener
     *            the selection listener to remove
     */
    void removeSelectionListener(ISelectionListener facesListener);

    /**
     * Returns a list of selection listener for the component
     * 
     * @return selection listeners' list
     */
    FacesListener[] listSelectionListeners();
}