/*
 * $Id: IFocusBlurEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IBlurListener;
import org.rcfaces.core.event.IFocusListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IFocusBlurEventCapability {

    /**
     * Adds a listener to the component for the focus event
     * 
     * @param facesListener
     *            the focus listener to add
     */
    void addFocusListener(IFocusListener facesListener);

    /**
     * Removes a listener from the component for the focus event
     * 
     * @param facesListener
     *            the focus listener to remove
     */
    void removeFocusListener(IFocusListener facesListener);

    /**
     * Returns a list of focus listener for the component
     * 
     * @return focus listeners' list
     */
    FacesListener[] listFocusListeners();

    /**
     * Adds a listener to the component for the blur event
     * 
     * @param facesListener
     *            the blur listener to add
     */
    void addBlurListener(IBlurListener facesListener);

    /**
     * Removes a listener from the component for the blur event
     * 
     * @param facesListener
     *            the blur listener to remove
     */
    void removeBlurListener(IBlurListener facesListener);

    /**
     * Returns a list of blur listener for the component
     * 
     * @return blur listeners' list
     */
    FacesListener[] listBlurListeners();
}
