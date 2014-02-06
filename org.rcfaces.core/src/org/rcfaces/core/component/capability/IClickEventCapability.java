/*
 * $Id: IClickEventCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IClickListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface IClickEventCapability {

    /**
     * Adds a listener to the component for the doubleClick event
     * 
     * @param facesListener
     *            the doubleClick listener to add
     */
    void addClickListener(IClickListener facesListener);

    /**
     * Removes a listener from the component for the doubleClick event
     * 
     * @param facesListener
     *            the doubleClick listener to remove
     */
    void removeClickListener(IClickListener facesListener);

    /**
     * Returns a list of doubleClick listener for the component
     * 
     * @return doubleClick listeners' list
     */
    FacesListener[] listClickListeners();
}
