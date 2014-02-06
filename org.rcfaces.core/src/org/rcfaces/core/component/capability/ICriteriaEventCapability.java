/*
 * $Id: ICriteriaEventCapability.java,v 1.1 2013/01/11 15:46:57 jbmeslin Exp $
 */

package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.ICriteriaListener;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:46:57 $
 */
public interface ICriteriaEventCapability {

    /**
     * Adds a listener to the component for the selection event
     * 
     * @param facesListener
     *            the selection listener to add
     */
    void addCriteriaListener(ICriteriaListener facesListener);

    /**
     * Removes a listener from the component for the selection event
     * 
     * @param facesListener
     *            the selection listener to remove
     */
    void removeCriteriaListener(ICriteriaListener facesListener);

    /**
     * Returns a list of selection listener for the component
     * 
     * @return selection listeners' list
     */
    FacesListener[] listCriteriaListeners();
}