package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IExpandListener;

/**
 * 
 * @author jb.meslin@vedana.com
 */
public interface IExpandEventCapability {

    /**
     * Adds a listener to the component for the expand event
     * 
     * @param expandListener
     *            the expand listener to add
     */
    void addExpandListener(IExpandListener expandListener);

    /**
     * Removes a listener from the component for the expand event
     * 
     * @param expandListener
     *            the expand listener to remove
     */
    void removeExpandListener(IExpandListener expandListener);

    /**
     * Returns a list of expand listener for the component
     * 
     * @return expand listeners' list
     */
    FacesListener[] listExpandListeners();
}
