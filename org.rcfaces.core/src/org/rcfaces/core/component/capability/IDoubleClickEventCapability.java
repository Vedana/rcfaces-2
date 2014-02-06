/*
 * $Id: IDoubleClickEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IDoubleClickListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IDoubleClickEventCapability {

    /**
     * Adds a listener to the component for the doubleClick event
     * 
     * @param facesListener
     *            the doubleClick listener to add
     */
    void addDoubleClickListener(IDoubleClickListener facesListener);

    /**
     * Removes a listener from the component for the doubleClick event
     * 
     * @param facesListener
     *            the doubleClick listener to remove
     */
    void removeDoubleClickListener(IDoubleClickListener facesListener);

    /**
     * Returns a list of doubleClick listener for the component
     * 
     * @return doubleClick listeners' list
     */
    FacesListener[] listDoubleClickListeners();
}
