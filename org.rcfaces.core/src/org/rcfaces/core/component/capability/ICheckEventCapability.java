/*
 * $Id: ICheckEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ICheckListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ICheckEventCapability {

    /**
     * Adds a listener to the component for the checked event
     * 
     * @param checkListener
     *            the listener to add
     */
    void addCheckListener(ICheckListener checkListener);

    /**
     * Removes a listener from the list of listeners
     * 
     * @param checkListener
     *            the listener to remove
     */
    void removeCheckListener(ICheckListener checkListener);

    /**
     * Returns the list of check listener for the component
     * 
     * @return check listeners' list
     */
    FacesListener[] listCheckListeners();
}
