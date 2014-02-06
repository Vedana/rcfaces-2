/*
 * $Id: IDropEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IDropListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IDropEventCapability {

    /**
     * Adds a listener to the component for the drop event
     * 
     * @param facesListener
     *            the drop listener to add
     */
    void addDropListener(IDropListener facesListener);

    /**
     * Removes a listener from the component for the drop event
     * 
     * @param facesListener
     *            the drop listener to remove
     */
    void removeDropListener(IDropListener facesListener);

    /**
     * Returns a list of drop listeners for the component
     * 
     * @return drop listeners' list
     */
    FacesListener[] listDropListeners();
}