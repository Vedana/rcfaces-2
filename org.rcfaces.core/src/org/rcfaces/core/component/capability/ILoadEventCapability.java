/*
 * $Id: ILoadEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ILoadListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ILoadEventCapability {

    /**
     * Adds a listener to the component for the load event
     * 
     * @param loadListener
     *            the load listener to add
     */
    void addLoadListener(ILoadListener loadListener);

    /**
     * Removes a listener from the component for the load event
     * 
     * @param loadListener
     *            the load listener to remove
     */
    void removeLoadListener(ILoadListener loadListener);

    /**
     * Returns a list of load listener for the component
     * 
     * @return load listeners' list
     */
    FacesListener[] listLoadListeners();
}
