/*
 * $Id: IValidationEventCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

import javax.faces.event.FacesListener;

import org.rcfaces.core.event.IValidationListener;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IValidationEventCapability {

    /**
     * Adds a listener to the component for the validation event
     * 
     * @param facesListener
     *            the validation listener to add
     */
    void addValidationListener(IValidationListener facesListener);

    /**
     * Removes a listener from the component for the validation event
     * 
     * @param facesListener
     *            the validation listener to remove
     */
    void removeValidationListener(IValidationListener facesListener);

    /**
     * Returns a list of validation listener for the component
     * 
     * @return validation listeners' list
     */
    FacesListener[] listValidationListeners();

}
