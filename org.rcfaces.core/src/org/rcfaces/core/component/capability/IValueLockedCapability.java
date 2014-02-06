/*
 * $Id: IValueLockedCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IValueLockedCapability {

    /**
     * Get the lock state of the main value of the component.
     * 
     * @return The lock state.
     */
    boolean isValueLocked();

    /**
     * Set the lock of the main value of the component.
     * 
     * @param valueLocked
     *            The lock state.
     */
    void setValueLocked(boolean valueLocked);

}
