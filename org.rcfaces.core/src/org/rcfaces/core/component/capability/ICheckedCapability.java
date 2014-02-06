/*
 * $Id: ICheckedCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * A boolean value indicating the state of the component.
 * 
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ICheckedCapability {

    /**
     * Returns a boolean value indicating the state of the component.
     * 
     * @return boolean
     */
    boolean isChecked();

    /**
     * Sets a boolean value indicating the state of the component.
     * 
     * @param checked
     *            boolean
     */
    void setChecked(boolean checked);

}
