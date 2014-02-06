/*
 * $Id: ISortEventCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ISortListener;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ISortEventCapability {

    String SORT_INTEGER = "integer";

    String SORT_NUMBER = "number";

    String SORT_ALPHA = "alpha";

    String SORT_ALPHA_IGNORE_CASE = "alphaIgnoreCase";

    String SORT_TIME = "time";

    String SORT_DATE = "date";

    String SORT_SERVER = "server";

    /**
     * Adds a listener to the component for the sort event
     * 
     * @param facesListener
     *            the sort listener to add
     */
    void addSortListener(ISortListener facesListener);

    /**
     * Removes a listener from the component for the sort event
     * 
     * @param facesListener
     *            the sort listener to remove
     */
    void removeSortListener(ISortListener facesListener);

    /**
     * Returns a list of sort listener for the component
     * 
     * @return sort listeners' list
     */
    FacesListener[] listSortListeners();
}
