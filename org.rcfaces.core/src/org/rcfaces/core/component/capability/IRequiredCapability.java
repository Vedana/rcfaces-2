/*
 * $Id: IRequiredCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IRequiredCapability {

    /**
     * Returns a boolean value indicating that the user is required to provide a
     * submitted value for this input component.
     * 
     * @return required boolean property
     */
    boolean isRequired();

    /**
     * Sets a boolean value indicating that the user is required to provide a
     * submitted value for this input component.
     * 
     * @param required
     *            required boolean property
     */
    void setRequired(boolean required);
}
