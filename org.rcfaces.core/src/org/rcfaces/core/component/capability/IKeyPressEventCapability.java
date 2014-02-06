/*
 * $Id: IKeyPressEventCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IKeyPressListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IKeyPressEventCapability {

    /**
     * Adds a listener to the component for the keyPress event
     * 
     * @param facesListener
     *            the keyPress listener to add
     */
    void addKeyPressListener(IKeyPressListener facesListener);

    /**
     * Removes a listener from the component for the keyPress event
     * 
     * @param facesListener
     *            the keyPress listener to remove
     */
    void removeKeyPressListener(IKeyPressListener facesListener);

    /**
     * Returns a list of keyPress listener for the component
     * 
     * @return keyPress listeners' list
     */
    FacesListener[] listKeyPressListeners();
}
