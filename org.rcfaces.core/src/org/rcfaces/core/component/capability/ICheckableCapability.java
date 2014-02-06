/*
 * $Id: ICheckableCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating whether the component can be checked. cf.
 * checkCardinality.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface ICheckableCapability {

    /**
     * Returns a boolean value indicating whether the component can be checked.
     * 
     * @return boolean
     */
    boolean isCheckable();

    /**
     * Sets a boolean value indicating whether the component can be checked.
     * 
     * @param checkable
     *            boolean
     */
    void setCheckable(boolean checkable);

}
