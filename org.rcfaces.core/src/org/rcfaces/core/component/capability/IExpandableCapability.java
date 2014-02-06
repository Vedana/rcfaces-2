/*
 * $Id: IExpandableCapability.java,v 1.1 2011/04/12 09:25:25 oeuillot Exp $
 * 
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:25 $
 */
public interface IExpandableCapability {

    /**
     * Returns a boolean value indicating wether the component can receive a
     * user's expand command.
     * 
     * @return expandable boolean property
     */
    boolean isExpandable();

    /**
     * Sets a boolean value indicating wether the component can receive a user's
     * expand command.
     * 
     * @param expandable
     *            expandable boolean property
     */
    void setExpandable(boolean expandable);
}
