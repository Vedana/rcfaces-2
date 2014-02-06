/*
 * $Id: IKeyUpEventCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IKeyUpListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IKeyUpEventCapability {

    /**
     * Adds a listener to the component for the keyUp event
     * 
     * @param facesListener
     *            the keyUp listener to add
     */
    void addKeyUpListener(IKeyUpListener facesListener);

    /**
     * Removes a listener from the component for the keyUp event
     * 
     * @param facesListener
     *            the keyUp listener to remove
     */
    void removeKeyUpListener(IKeyUpListener facesListener);

    /**
     * Returns a list of keyUp listener for the component
     * 
     * @return keyUp listeners' list
     */
    FacesListener[] listKeyUpListeners();
}
