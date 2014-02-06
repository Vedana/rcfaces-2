/*
 * $Id: IImmediateCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IImmediateCapability {

    /**
     * Returns a boolean value indicating that this component's value must be
     * converted and validated immediately (that is, during Apply Request Values
     * phase), rather than waiting until Process Validations phase.
     * 
     * @return boolean
     */
    boolean isImmediate();

    /**
     * Sets a boolean value indicating that this component's value must be
     * converted and validated immediately (that is, during Apply Request Values
     * phase), rather than waiting until Process Validations phase.
     * 
     * @param immediate
     *            boolean
     */
    void setImmediate(boolean immediate);
}
