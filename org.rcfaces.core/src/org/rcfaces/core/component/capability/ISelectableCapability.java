/*
 * $Id: ISelectableCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface ISelectableCapability {

    /**
     * Returns a boolean value indicating wether the component can receive a
     * user's selection
     * 
     * @return selectable boolean property
     */
    boolean isSelectable();

    /**
     * Sets a boolean value indicating wether the component can receive a user's
     * selection
     * 
     * @param selectable
     *            selectable boolean property
     */
    void setSelectable(boolean selectable);
}
