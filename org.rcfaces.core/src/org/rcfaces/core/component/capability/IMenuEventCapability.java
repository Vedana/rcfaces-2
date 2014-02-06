/*
 * $Id: IMenuEventCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IMenuListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IMenuEventCapability {

    /**
     * Adds a listener to the component for the menu event
     * 
     * @param facesListener
     *            the menu listener to add
     */
    void addMenuListener(IMenuListener facesListener);

    /**
     * Removes a listener from the component for the menu event
     * 
     * @param facesListener
     *            the menu listener to remove
     */
    void removeMenuListener(IMenuListener facesListener);

    /**
     * Returns a list of menu listener for the component
     * 
     * @return menu listeners' list
     */
    FacesListener[] listMenuListeners();
}
