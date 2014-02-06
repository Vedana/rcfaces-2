/*
 * $Id: ISelectedCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface ISelectedCapability {

    /**
     * Returns a boolean value indicating wether the component is selected.
     * 
     * @return true if selected, false otherwise
     */
    boolean isSelected();

    /**
     * Sets a boolean value indicating wether the component is selected.
     * 
     * @param selected
     *            true to select, false to unselect
     */
    void setSelected(boolean selected);
}
