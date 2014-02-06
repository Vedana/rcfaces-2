/*
 * $Id: IKeyDownEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IKeyDownListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IKeyDownEventCapability {

    /**
     * Adds a listener to the component for the keyDown event
     * 
     * @param facesListener
     *            the keyDown listener to add
     */
    void addKeyDownListener(IKeyDownListener facesListener);

    /**
     * Removes a listener from the component for the keyDown event
     * 
     * @param facesListener
     *            the keyDown listener to remove
     */
    void removeKeyDownListener(IKeyDownListener facesListener);

    /**
     * Returns a list of keyDown listener for the component
     * 
     * @return keyDown listeners' list
     */
    FacesListener[] listKeyDownListeners();
}
