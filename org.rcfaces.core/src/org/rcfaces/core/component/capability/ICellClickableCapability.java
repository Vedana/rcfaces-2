/*
 * $Id: ICellClickableCapability.java,v 1.1 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating whether the component can be clicked.
 * 
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:25 $
 */
public interface ICellClickableCapability {

    /**
     * Returns a boolean value indicating whether the component can be clicked.
     * 
     * @return boolean
     */
    boolean isCellClickable();

    /**
     * Sets a boolean value indicating whether the component can be clicked.
     * 
     * @param checkable
     *            boolean
     */
    void setCellClickable(boolean clickable);

    /**
     * Returns a boolean value indicating whether the component can be clicked.
     * 
     * @return boolean
     */
    boolean isAllCellClickable();

    /**
     * Sets a boolean value indicating whether the component can be clicked.
     * 
     * @param checkable
     *            boolean
     */
    void setAllCellClickable(boolean clickable);

}
