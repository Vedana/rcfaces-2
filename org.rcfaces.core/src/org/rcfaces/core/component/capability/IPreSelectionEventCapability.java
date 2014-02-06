/*
 * $Id: IPreSelectionEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IPreSelectionListener;

/**
 * @author jbmeslin@vedana.com
 */
public interface IPreSelectionEventCapability {

    /**
     * Adds a listener to the component for the preSelection event
     * 
     * @param facesListener
     *            the preSelection listener to add
     */
    void addPreSelectionListener(IPreSelectionListener facesListener);

    /**
     * Removes a listener from the component for the preSelection event
     * 
     * @param facesListener
     *            the PreSelection listener to remove
     */
    void removePreSelectionListener(IPreSelectionListener facesListener);

    /**
     * Returns a list of PreSelection listener for the component
     * 
     * @return preSelection listeners' list
     */
    FacesListener[] listPreSelectionListeners();
}