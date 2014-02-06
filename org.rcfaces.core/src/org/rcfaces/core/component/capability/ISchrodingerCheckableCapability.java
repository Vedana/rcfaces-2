/*
 * $Id: ISchrodingerCheckableCapability.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating whether the component can be checked. cf.
 * checkCardinality.
 * 
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public interface ISchrodingerCheckableCapability {

    /**
     * Returns a boolean value indicating whether the component
     * 
     * @return boolean
     */
    boolean isSchrodingerCheckable();

    /**
     * Sets a boolean value indicating whether the component .
     * 
     * @param checkable
     *            boolean
     */
    void setSchrodingerCheckable(boolean schrodingerCheckable);

}
