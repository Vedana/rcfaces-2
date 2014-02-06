/*
 * $Id: IUserEventCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IUserEventListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IUserEventCapability {

    /**
     * Adds a listener to the component for the user event
     * 
     * @param facesListener
     *            the user listener to add
     */
    void addUserEventListener(IUserEventListener facesListener);

    /**
     * Removes a listener from the component for the user event
     * 
     * @param facesListener
     *            the user listener to remove
     */
    void removeUserEventListener(IUserEventListener facesListener);

    /**
     * Returns a list of user listener for the component
     * 
     * @return user listeners' list
     */
    FacesListener[] listUserEventListeners();

}
