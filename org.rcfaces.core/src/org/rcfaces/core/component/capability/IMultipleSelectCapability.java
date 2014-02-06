/*
 * $Id: IMultipleSelectCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IMultipleSelectCapability {

    /**
     * Returns a boolean value indicating wether multiple selection is
     * permitted.
     * 
     * @return boolean
     */
    boolean isMultipleSelect();

    /**
     * Sets a boolean value indicating wether multiple selection is permitted.
     * 
     * @param multipleSelect
     *            boolean
     */
    void setMultipleSelect(boolean multipleSelect);
}
