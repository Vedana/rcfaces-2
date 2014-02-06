/*
 * $Id: IPageLoadedCapability.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IPageLoadedListener;

public interface IPageLoadedCapability {

    /**
     * Adds a listener to the component for the load event
     * 
     * @param loadListener
     *            the load listener to add
     */
    void addPageLoadedListener(IPageLoadedListener loadListener);

    /**
     * Removes a listener from the component for the load event
     * 
     * @param loadListener
     *            the load listener to remove
     */
    void removePageLoadedListener(IPageLoadedListener loadListener);

    /**
     * Returns a list of load listener for the component
     * 
     * @return load listeners' list
     */
    FacesListener[] listPageLoadedListeners();

}
